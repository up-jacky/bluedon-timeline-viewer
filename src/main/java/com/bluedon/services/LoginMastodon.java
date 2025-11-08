package com.bluedon.services;

import com.bluedon.controllers.PageController;
import com.bluedon.utils.SessionFile;
import com.bluedon.utils.Toast;

import javafx.concurrent.Task;

public class LoginMastodon extends Task<Boolean> {
    private MastodonClient mastodonClient = ServiceRegistry.getMastodonClient();

    @Override
    protected Boolean call() throws Exception {
        System.out.println("[DEBUG][LoginMastodon][call] Thread: " + Thread.currentThread());
        System.out.println("[INFO] Authenticating Mastodon...");
        Toast.info.showToast("Authenticating Mastodon...");
        AuthSession session = mastodonClient.startAuth();
        ServiceRegistry.setMastodonSession(session);
        mastodonClient.getUserInfo(session);
        return true;
    }

    @Override
    protected void succeeded() {
        System.out.println("[DEBUG][LoginMastodon][succeeded] Thread: " + Thread.currentThread());
        AuthSession session = ServiceRegistry.getMastodonSession();
        System.out.println("[INFO][LoginMastodon][succeeded] Authentication successful!");
        System.out.println("[INFO][LoginMastodon][succeeded] Logged in as: " + session.handle);
        Toast.success.showToast("Welcome " + session.handle + "!");
        SessionFile.MastodonSessionFile.saveSession();
        PageController.displayHomePage();
    }

    @Override
    protected void failed() {
        System.out.println("[DEBUG][LoginMastodon][failed] Thread: " + Thread.currentThread());
        System.out.println("[ERROR][LoginMastodon][failed] Authentication failed!");
        System.out.println("[ERROR][LoginMastodon][failed] Error: " + getException().getMessage());
        Toast.error.showToast("Login failed! Error " + getException().getMessage());
        getException().printStackTrace();
        PageController.displayLoginPage();
    }
}
