package com.bluedon.view.ui.buttons;

import com.bluedon.controllers.PageController;
import com.bluedon.enums.Page;
import com.bluedon.enums.Social;
import com.bluedon.services.LoginBluesky;
import com.bluedon.services.LoginMastodon;
import com.bluedon.services.LogoutBluesky;
import com.bluedon.services.LogoutMastodon;
import com.bluedon.services.Refresh;
import com.bluedon.services.ServiceRegistry;

import javafx.scene.control.Button;
import javafx.stage.Stage;

public class LoginButton {
    public static Button createButton(Social social, Stage stage) {
    	DefaultButton button = new DefaultButton();
    	button.getStyleClass().addAll("login", "main");

		switch (social) {
			case BLUESKY:
				button.getStyleClass().add("bluesky");
				if (!ServiceRegistry.isBlueskyLoggedIn()) {
					button.setText("Bluesky Login");
					button.setOnAction(e -> {
						if(PageController.currentPage == Page.LOGIN) Refresh.refreshLogin();
						LoginBluesky login = new LoginBluesky();
						Thread loginThread = new Thread(login);
						loginThread.setDaemon(true);
						loginThread.start();
					});
				} else {
					button.setText("Bluesky Logout");
					button.setOnAction(e -> {
						LogoutBluesky logout = new LogoutBluesky();
						Thread loginThread = new Thread(logout);
						loginThread.setDaemon(true);
						loginThread.start();
					});
				}
				break;
			case MASTODON:
				button.getStyleClass().add("mastodon");
				if (!ServiceRegistry.isMastodonLoggedIn()) {
					button.setText("Mastodon Login");
					button.setOnAction(e -> {
						if(PageController.currentPage == Page.LOGIN) Refresh.refreshLogin();
						LoginMastodon login = new LoginMastodon();
						Thread loginThread = new Thread(login);
						loginThread.setDaemon(true);
						loginThread.start();
					});
				} else {
					button.setText("Mastodon Logout");
					button.setOnAction(e -> {
						LogoutMastodon logout = new LogoutMastodon();
						Thread loginThread = new Thread(logout);
						loginThread.setDaemon(true);
						loginThread.start();
					});
				}
				break;
			default:
				return null;
		}
		
		button.playInitAnimation();

    	return button;
    }
}
