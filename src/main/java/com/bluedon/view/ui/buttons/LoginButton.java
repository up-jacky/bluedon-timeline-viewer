package com.bluedon.view.ui.buttons;

import java.util.Map;

import com.bluedon.controllers.PageController;
import com.bluedon.enums.Social;
import com.bluedon.services.AuthSession;
import com.bluedon.services.BlueskyClient;
import com.bluedon.services.MastodonClient;
import com.bluedon.services.ServiceRegistry;
import com.bluedon.view.ui.cards.LoginDialog;

import javafx.scene.control.Button;

public class LoginButton {
	private final static BlueskyClient blueskyClient = ServiceRegistry.getBlueskyClient();
	private final static MastodonClient mastodonClient = ServiceRegistry.getMastodonClient();
	
	private static void blueskyLogin() {
		System.out.println("[INFO] Logging in using Bluesky...");

		String password = LoginDialog.showLoginDialog();

		if (password == null || password.trim().isEmpty()) {
			System.out.println("[ERROR] Error: Please input your password!");
			return;
		}

		try {
			String pdsOrigin = "https://bsky.social";
			AuthSession session = blueskyClient.startAuth(pdsOrigin);
			try {
				blueskyClient.createSession(session, pdsOrigin, password);
			} catch (Exception e) {
				System.err.println("[ERROR] Incorrect password!");
				return;
			}

			ServiceRegistry.setBlueskySession(session);
			ServiceRegistry.setBlueskyPdsOrigin(pdsOrigin);

			blueskyClient.getProfile(session, pdsOrigin);

			System.out.println("[INFO] Authentication successful!");
			System.out.println("[INFO] Logged in as: " + session.handle);
			PageController.displayHomePage();

		} catch (Exception e) {
			e.getStackTrace();
			System.out.println("[ERROR] Authentication failed!");
			System.out.println("[ERROR] Error: " + e.getMessage());
		}
	}

    private static void blueskyLogout() {
        System.out.println("[INFO] Logging out of Bluesky...");
		String pdsOrigin = "https://bsky.social";
		AuthSession session = ServiceRegistry.getBlueskySession();

		if (session == null || session.did == null) {
			System.out.println("[FATAL] How did you get here?");
		}

		try {
			blueskyClient.deleteSession(session, pdsOrigin);
			ServiceRegistry.setBlueskySession(null);
			PageController.displayHomePage();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("[ERROR] Logout failed!");
			System.out.println("[ERROR] Error: " + e.getMessage());
		}
    }

	private static void mastodonLogin() {
		System.out.println("[INFO] Logging in using Mastodon...");

		try {
			AuthSession session = mastodonClient.startAuth();
			ServiceRegistry.setMastodonSession(session);

			mastodonClient.getUserInfo(session);

			System.out.println("[INFO] Authentication successful!");
			System.out.println("[INFO] Logged in as: " + session.handle);
			
			PageController.displayHomePage();

		} catch (Exception e) {
			e.getStackTrace();
			System.out.println("[ERROR] Authentication failed!");
			System.out.println("[ERROR] Error: " + e.getMessage());
		}
	
	}

	private static void mastodonLogout() {
		System.out.println("[INFO] Logging out of Mastodon...");

		AuthSession session = ServiceRegistry.getMastodonSession();

		if (session == null || session.accessToken == null || session.instanceUrl == null) {
			System.out.println("[FATAL] How did you get here?");
		}

		try {
			mastodonClient.revokeAuth(session);
			ServiceRegistry.setMastodonSession(null);
			PageController.displayHomePage();
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("[ERROR] Logout failed!");
			System.out.println("[ERROR] Error: " + e.getMessage());
		}

	}
    
    public static Button createButton(Social social) {

    	Button button = new Button();
    	button.getStyleClass().add("login-btn");

		switch (social) {
			case BLUESKY:
				button.getStyleClass().add("bluesky");

				if (ServiceRegistry.getBlueskySession() == null) {
					button.setText("Bluesky Login");
					button.setOnAction(e -> {
						blueskyLogin();
					});
				} else {
					button.setText("Bluesky Logout");
					button.setOnAction(e -> {
						blueskyLogout();
					});
				}
				break;
			case MASTODON:
				button.getStyleClass().add("mastodon");
				if (ServiceRegistry.getMastodonSession() == null) {
					button.setText("Mastodon Login");
					button.setOnAction(e -> {
						mastodonLogin();
					});
				} else {
					button.setText("Mastodon Logout");
					button.setOnAction(e -> {
						mastodonLogout();
					});
				}
				break;
			default:
				return null;
		}

    	return button;
    }
}
