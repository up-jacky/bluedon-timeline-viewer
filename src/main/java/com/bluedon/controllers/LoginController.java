package com.bluedon.controllers;

import com.bluedon.enums.Social;
import com.bluedon.models.Login;
import com.bluedon.view.LoginView;
import com.bluedon.view.ui.buttons.LoginButton;

import javafx.stage.Stage;
import javafx.scene.layout.VBox;

public class LoginController {

	private static Login model = new Login();
	private static LoginView view = new LoginView();
	
	public void start(Stage stage) {
        System.out.println("In Login Controller");
		view.init();
		
		model.setButton(Social.BLUESKY, LoginButton.createButton(Social.BLUESKY));
		model.setButton(Social.MASTODON, LoginButton.createButton(Social.MASTODON));
		
		VBox blueskyLayout = view.createContainer(10, model.getButton(Social.BLUESKY));
		VBox mastodonLayout = view.createContainer(10, model.getButton(Social.MASTODON));
		view.updateLayout(40, blueskyLayout, mastodonLayout);
		
        System.out.println("Displaying Login Page...");
		view.displayPage(stage);
		stage.show();
	}
	
	public Login getModel() {
		return model;
	}
	
	public LoginView getView() {
		return view;
	}
}
