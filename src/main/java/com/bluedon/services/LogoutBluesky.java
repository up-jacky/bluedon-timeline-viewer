package com.bluedon.services;

import com.bluedon.controllers.PageController;
import com.bluedon.utils.SessionFile;
import com.bluedon.utils.Toast;

import javafx.application.Platform;
import javafx.concurrent.Task;

/**
 * LogoutBluesky is a Task<Boolean> that logs in the user in Bluesky in a separate thread.
 */
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
        FetchTimeline instance = FetchTimeline.getInstance();
        if(instance != null && instance.isRunning()) Platform.runLater(() -> instance.cancel());
        PageController.displayHomePage();
    }

    @Override
    protected void failed() {
        System.out.println("[DEBUG][LogoutBluesky][failed] Thread: " + Thread.currentThread());
        System.err.println("[ERROR][LogoutBluesky][failed] Logout failed!");
        System.err.println("[ERROR][LogoutBluesky][failed] Error: " + getException().getMessage());
        Toast.error.showToast("Logout to Bluesky failed! Error: " + getException().getMessage());
        getException().printStackTrace();
        FetchTimeline instance = FetchTimeline.getInstance();
        if(instance != null && instance.isRunning()) Platform.runLater(() -> instance.cancel());
        PageController.displayHomePage();
    }
}
