package com.bluedon.controllers;

import com.bluedon.enums.Social;
import com.bluedon.models.Login;
import com.bluedon.view.LoginView;
import com.bluedon.view.ui.buttons.LoginButton;

import javafx.stage.Stage;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class LoginController {

	private static Login model = new Login();
	private static LoginView view = new LoginView();
	
	public void start(Stage stage) {
        System.out.println("In Login Controller");

		// model.setHandleField(view.createTextField("User handle"));
		
		model.setButton(Social.BLUESKY, LoginButton.createButton(Social.BLUESKY));
		model.setButton(Social.MASTODON, LoginButton.createButton(Social.MASTODON));

		Label mastodonLabel = new Label("@mastodon.social");
		mastodonLabel.getStyleClass().add("mastodon");

		// HBox mastodonFields = new HBox(8, mastodonLabel);
		
		VBox blueskyLayout = view.createContainer(10, model.getButton(Social.BLUESKY));
		VBox mastodonLayout = view.createContainer(10, model.getButton(Social.MASTODON));
		
		
        System.out.println("Displaying Login Page...");
		view.displayPage(stage, view.createLayout(40, blueskyLayout, mastodonLayout));
		stage.show();
	}
	
	public Login getModel() {
		return model;
	}
	
	public LoginView getView() {
		return view;
	}
}
