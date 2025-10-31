package com.bluedon.view.ui.buttons;

import com.bluedon.controllers.PageController;
import com.bluedon.enums.Social;
import com.bluedon.services.AuthSession;
import com.bluedon.services.BlueskyClient;
import com.bluedon.services.MastodonClient;
import com.bluedon.services.ServiceRegistry;
import com.bluedon.utils.SessionFile;
import com.bluedon.view.ui.cards.LoginDialog;

import javafx.scene.control.Button;

public class LoginButton {
	private final static BlueskyClient blueskyClient = ServiceRegistry.getBlueskyClient();
	private final static MastodonClient mastodonClient = ServiceRegistry.getMastodonClient();
	
	private static void blueskyLogin() {
		System.out.println("[INFO] Logging in using Bluesky...");

		try {
			String pdsOrigin = "https://bsky.social";
			AuthSession session =  ServiceRegistry.getBlueskySession();
			if (session == null) {
				session = blueskyClient.startAuth(pdsOrigin);
				ServiceRegistry.setBlueskySession(session);
				ServiceRegistry.setBlueskyPdsOrigin(pdsOrigin);
				blueskyClient.getProfile(session);
				SessionFile.BlueskySessionFile.saveSession();
			}

			try {
				
				String password = LoginDialog.showLoginDialog();

				if (ServiceRegistry.getBlueskySession() == null) {
					System.out.println("[INFO] Logout successful!");
					return;
				}

				if (password.trim().isEmpty()) {
					System.out.println("[INFO] Login dialog closed.");
					return;
				}

				blueskyClient.createSession(session, pdsOrigin, password);
				SessionFile.BlueskySessionFile.saveSession();
			} catch (Exception e) {
				System.err.println(e.getMessage());
				return;
			}

			System.out.println("[INFO] Authentication successful!");
			System.out.println("[INFO] Logged in as: " + session.handle);
			PageController.displayHomePage();

		} catch (Exception e) {
			e.printStackTrace();
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
			return;
		}

		try {
			blueskyClient.deleteSession(session, pdsOrigin);
			ServiceRegistry.setBlueskySession(null);
			ServiceRegistry.setBlueskyPdsOrigin(null);
			SessionFile.BlueskySessionFile.deleteSession();
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
			SessionFile.MastodonSessionFile.saveSession();
			PageController.displayHomePage();

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("[ERROR] Authentication failed!");
			System.out.println("[ERROR] Error: " + e.getMessage());
		}
	
	}

	private static void mastodonLogout() {
		System.out.println("[INFO] Logging out of Mastodon...");

		AuthSession session = ServiceRegistry.getMastodonSession();

		if (session == null || session.accessToken == null || session.instanceUrl == null) {
			System.out.println("[FATAL] How did you get here?");
			return;
		}

		try {
			mastodonClient.revokeAuth(session);
			ServiceRegistry.setMastodonSession(null);
			SessionFile.MastodonSessionFile.deleteSession();
			PageController.displayHomePage();
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("[ERROR] Logout failed!");
			System.out.println("[ERROR] Error: " + e.getMessage());
		}

	}
    
    public static Button createButton(Social social) {

    	Button button = new Button();
    	button.getStyleClass().add("login-button");

		switch (social) {
			case BLUESKY:
				button.getStyleClass().add("bluesky");

				if (!ServiceRegistry.isBlueskyLoggedIn()) {
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
				if (!ServiceRegistry.isMastodonLoggedIn()) {
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
