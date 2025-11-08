package com.bluedon.services;

import com.bluedon.controllers.PageController;
import com.bluedon.utils.SessionFile;
import com.bluedon.utils.Toast;

import javafx.concurrent.Task;

public class LogoutBluesky extends Task<Boolean> {
    private BlueskyClient blueskyClient = ServiceRegistry.getBlueskyClient();

    @Override
    protected Boolean call() throws Exception {
        System.out.println("[DEBUG][LogoutBluesky][call] Thread: " + Thread.currentThread());
        System.out.println("[INFO] Logging out of Bluesky...");
        Toast.info.showToast("Logging out of Bluesky...");
        AuthSession session = ServiceRegistry.getBlueskySession();

        if (session == null) {
            System.err.println("[FATAL][LogoutBluesky][call] How did you get here?");
            Toast.fatal.showToast("Logout to Bluesky failed! Fatal Error: State should not be possible.");
            throw new IllegalStateException("Fatal: State should not be possible");
        }

        blueskyClient.deleteSession(session, ServiceRegistry.getBlueskyPdsOrigin());
        return true;
    }

    @Override
    protected void succeeded() {
        System.out.println("[DEBUG][LogoutBluesky][succeeded] Thread: " + Thread.currentThread());
        ServiceRegistry.setBlueskySession(null);
        SessionFile.BlueskySessionFile.deleteSession();
        Toast.success.showToast("Logout to Bluesky successful!");
        PageController.displayHomePage();
    }

    @Override
    protected void failed() {
        System.out.println("[DEBUG][LogoutBluesky][failed] Thread: " + Thread.currentThread());
        System.err.println("[ERROR][LogoutBluesky][failed] Logout failed!");
        System.err.println("[ERROR][LogoutBluesky][failed] Error: " + getException().getMessage());
        Toast.error.showToast("Logout to Bluesky failed! Error: " + getException().getMessage());
        getException().printStackTrace();
        PageController.displayHomePage();
    }
}
