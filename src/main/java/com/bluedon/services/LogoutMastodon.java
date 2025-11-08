package com.bluedon.services;

import com.bluedon.controllers.PageController;
import com.bluedon.utils.SessionFile;
import com.bluedon.utils.Toast;

import javafx.concurrent.Task;

public class LogoutMastodon extends Task<Boolean> {
    private MastodonClient mastodonClient = ServiceRegistry.getMastodonClient();


    @Override
    protected Boolean call() throws Exception {
        System.out.println("[DEBUG][LogoutMastodon][call] Thread: " + Thread.currentThread());
		System.out.println("[INFO][LogoutMastodon][call] Logging out of Mastodon...");
        Toast.info.showToast("Logging out of Mastodon...");

		AuthSession session = ServiceRegistry.getMastodonSession();
		if (session == null) {
			System.out.println("[FATAL][LogoutMastodon][call] How did you get here?");
            Toast.fatal.showToast("Logout to Mastodon failed! Fatal Error: State should not be possible.");
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
        Toast.success.showToast("Logout to Mastodon successful!");
        PageController.displayHomePage();
    }

    @Override
    protected void failed() {
        System.out.println("[DEBUG][LogoutMastodon][failed] Thread: " + Thread.currentThread());
        System.err.println("[ERROR][LogoutMastodon][failed] Logout failed!");
        System.err.println("[ERROR][LogoutMastodon][failed] Error: " + getException().getMessage());
        Toast.fatal.showToast("Logout to Mastodon failed! Error: " + getException().getMessage());
        getException().printStackTrace();
        PageController.displayHomePage();
    }

}
