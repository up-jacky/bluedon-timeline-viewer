package com.bluedon.services;

import com.bluedon.controllers.PageController;
import com.bluedon.enums.Page;
import com.bluedon.utils.SessionFile;
import com.bluedon.view.ui.cards.LoginDialog;

import javafx.concurrent.Task;

public class LoginBluesky extends Task<Boolean> {
    private BlueskyClient blueskyClient = ServiceRegistry.getBlueskyClient();

    @Override
    protected Boolean call() throws Exception {
        System.out.println("[DEBUG][LoginBluesky][call] Thread: " + Thread.currentThread());
        String pdsOrigin = "https://bsky.social";
        AuthSession session =  ServiceRegistry.getBlueskySession();
        if (session == null) {
            System.out.println("[INFO][LoginBluesky][call] Authenticating Bluesky...");
            session = blueskyClient.startAuth(pdsOrigin);
            ServiceRegistry.setBlueskySession(session);
            ServiceRegistry.setBlueskyPdsOrigin(pdsOrigin);
            blueskyClient.getProfile(session);
            SessionFile.BlueskySessionFile.saveSession();
        }
        return true;
    }

    @Override
    protected void succeeded() {
        System.out.println("[DEBUG][LoginBluesky][succeeded] Thread: " + Thread.currentThread());
        String pdsOrigin = "https://bsky.social";
        AuthSession session =  ServiceRegistry.getBlueskySession();try {
            
            String password = LoginDialog.showLoginDialog();

            if (ServiceRegistry.getBlueskySession() == null) {
                System.out.println("[INFO][LoginBluesky][succeeded] Logout successful!");
                PageController.displayLoginPage();
                return;
            }

            if (password.trim().isEmpty()) {
                System.out.println("[INFO][LoginBluesky][succeeded] Login dialog closed.");
                if(PageController.currentPage == Page.LOGIN) PageController.displayLoginPage();
                return;
            }

            blueskyClient.createSession(session, pdsOrigin, password);
            SessionFile.BlueskySessionFile.saveSession();
        } catch (Exception e) {
            System.err.println("[ERROR][LoginBluesky][succeeded] Failed to login to Bluesky: " +  e.getMessage());
            e.printStackTrace();
            return;
        }
        System.out.println("[INFO][LoginBluesky][succeeded] Authentication successful!");
        System.out.println("[INFO][LoginBluesky][succeeded] Logged in as: " + session.handle);
        PageController.displayHomePage();
    }

    @Override
    protected void failed() {
        System.out.println("[DEBUG][LoginBluesky][failed] Thread: " + Thread.currentThread());
        System.err.println("[ERROR][LoginBluesky][failed] Authentication failed!");
        System.err.println("[ERROR][LoginBluesky][failed] Error: " + getException().getMessage());
        getException().printStackTrace();
        if(PageController.currentPage == Page.LOGIN) PageController.displayLoginPage();
    }
}