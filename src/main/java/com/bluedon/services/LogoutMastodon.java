package com.bluedon.services;

import com.bluedon.controllers.PageController;
import com.bluedon.utils.SessionFile;

import javafx.concurrent.Task;

public class LogoutMastodon extends Task<Boolean> {
    private MastodonClient mastodonClient = ServiceRegistry.getMastodonClient();


    @Override
    protected Boolean call() throws Exception {
        System.out.println("[DEBUG][LogoutMastodon][call] Thread: " + Thread.currentThread());
		System.out.println("[INFO][LogoutMastodon][call] Logging out of Mastodon...");
		AuthSession session = ServiceRegistry.getMastodonSession();
		if (session == null) {
			System.out.println("[FATAL][LogoutMastodon][call] How did you get here?");
            throw new Exception("Fatal: State should not be possible");
		}
        mastodonClient.revokeAuth(session);
        return true;
    }
    
    @Override
    protected void succeeded() {
        System.out.println("[DEBUG][LogoutMastodon][succeeded] Thread: " + Thread.currentThread());
        ServiceRegistry.setMastodonSession(null);
        SessionFile.MastodonSessionFile.deleteSession();
        PageController.displayHomePage();
    }

    @Override
    protected void failed() {
        System.out.println("[DEBUG][LogoutMastodon][failed] Thread: " + Thread.currentThread());
        System.err.println("[ERROR][LogoutMastodon][failed] Logout failed!");
        System.err.println("[ERROR][LogoutMastodon][failed] Error: " + getException().getMessage());
        getException().printStackTrace();
        PageController.displayHomePage();
    }

}
