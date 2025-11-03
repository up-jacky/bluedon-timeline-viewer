package com.bluedon.services;

import com.bluedon.controllers.PageController;
import com.bluedon.utils.SessionFile;

import javafx.concurrent.Task;

public class LogoutBluesky extends Task<Boolean> {
    private BlueskyClient blueskyClient = ServiceRegistry.getBlueskyClient();

    @Override
    protected Boolean call() throws Exception {
        System.out.println("[DEBUG][LogoutBluesky][call] Thread: " + Thread.currentThread());
        System.out.println("[INFO] Logging out of Bluesky...");
        String pdsOrigin = "https://bsky.social";
        AuthSession session = ServiceRegistry.getBlueskySession();

        if (session == null) {
            System.err.println("[FATAL][LogoutBluesky][call] How did you get here?");
            throw new IllegalStateException("Fatal: State should not be possible");
        }

        blueskyClient.deleteSession(session, pdsOrigin);
        return true;
    }

    @Override
    protected void succeeded() {
        System.out.println("[DEBUG][LogoutBluesky][succeeded] Thread: " + Thread.currentThread());
        ServiceRegistry.setBlueskySession(null);
        ServiceRegistry.setBlueskyPdsOrigin(null);
        SessionFile.BlueskySessionFile.deleteSession();
        PageController.displayHomePage();
    }

    @Override
    protected void failed() {
        System.out.println("[DEBUG][LogoutBluesky][failed] Thread: " + Thread.currentThread());
        System.err.println("[ERROR][LogoutBluesky][failed] Logout failed!");
        System.err.println("[ERROR][LogoutBluesky][failed] Error: " + getException().getMessage());
        getException().printStackTrace();
        PageController.displayHomePage();
    }
}
