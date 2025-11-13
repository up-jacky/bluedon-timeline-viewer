package com.bluedon.services;

import org.json.JSONObject;

import com.bluedon.controllers.PageController;
import com.bluedon.enums.Page;
import com.bluedon.utils.SessionFile;
import com.bluedon.utils.Toast;
import com.bluedon.view.ui.cards.LoginDialog;

import javafx.application.Platform;
import javafx.concurrent.Task;

/**
 * LoginBluesky is a Task<Boolean> that logs in the user in Bluesky in a separate thread.
 */
public class LoginBluesky extends Task<Boolean> {
    private BlueskyClient blueskyClient = ServiceRegistry.getBlueskyClient();

    @Override
    protected Boolean call() throws Exception {
        System.out.println("[DEBUG][LoginBluesky][call] Thread: " + Thread.currentThread());
        return true;
    }

    @Override
    protected void succeeded() {
        System.out.println("[DEBUG][LoginBluesky][succeeded] Thread: " + Thread.currentThread());
        AuthSession session =  ServiceRegistry.getBlueskySession();
        String pdsOrigin = ServiceRegistry.getBlueskyPdsOrigin();
        if (session == null) {
            session = new AuthSession();
            System.out.println("[INFO][LoginBluesky][call] Logging in to Bluesky...");
            Toast.info.showToast("Logging in to Bluesky...");
            
            JSONObject rawCreds = LoginDialog.showLoginDialog();

            if (rawCreds == null) {
                System.out.println("[INFO][LoginBluesky][succeeded] Login dialog closed.");
                Toast.info.showToast("Login dialog closed.");
                if(PageController.currentPage == Page.LOGIN) PageController.displayLoginPage();
                return;
            }
            
            try {
                blueskyClient.createSession(session, pdsOrigin, rawCreds.getString("password"), rawCreds.getString("handle"));
                blueskyClient.getProfile(session, pdsOrigin);
                ServiceRegistry.setBlueskySession(session);
            } catch (Exception e) {
                System.err.println("[ERROR][LoginBluesky][succeeded] Login failed!");
                System.err.println("[ERROR][LoginBluesky][succeeded] Error: " + e.getMessage());
                Toast.error.showToast("Login failed! Error: " + e.getMessage());
                e.printStackTrace();
                if(PageController.currentPage == Page.LOGIN) PageController.displayLoginPage();
                return;
            }

            SessionFile.BlueskySessionFile.saveSession();
        }
        System.out.println("[INFO][LoginBluesky][succeeded] Login successful!");
        System.out.println("[INFO][LoginBluesky][succeeded] Logged in as: " + session.handle);
        Toast.success.showToast("Welcome " + session.handle + "!");
        FetchTimeline instance = FetchTimeline.getInstance();
        if(instance != null && instance.isRunning()) Platform.runLater(() -> instance.cancel());
        PageController.displayHomePage();
    }

    @Override
    protected void failed() {
        System.out.println("[DEBUG][LoginBluesky][failed] Thread: " + Thread.currentThread());
        System.err.println("[ERROR][LoginBluesky][failed] Login failed!");
        System.err.println("[ERROR][LoginBluesky][failed] Error: " + getException().getMessage());
        Toast.error.showToast("Login failed! Error: " + getException().getMessage());
        getException().printStackTrace();
        if(PageController.currentPage == Page.LOGIN) PageController.displayLoginPage();
    }
}