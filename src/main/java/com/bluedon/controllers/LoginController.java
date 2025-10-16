package com.bluedon.controllers;

import com.bluedon.enums.Social;
import com.bluedon.models.Login;
import com.bluedon.view.LoginView;
import javafx.stage.Stage;
import javafx.scene.layout.VBox;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;

public class LoginController {

	private Login model;
	private LoginView view;
	
	
	public LoginController(Login model, LoginView view) {
		this.model = model;
		this.view = view;
	}
	
	private EventHandler<ActionEvent> blueskyLogin() {
		
		// CODE FOR LOGGING IN WITH BLUESKY
		
		return null;
	}

	private EventHandler<ActionEvent> mastodonLogin() {
		
		// CODE FOR LOGGING IN WITH MASTODON
		
		return null;
	}
	
	public void start(Stage stage) {
		model.setField(Social.BLUESKY, view.createTextField("Enter Bluesky Email"));
		model.setField(Social.MASTODON, view.createTextField("Enter Mastodon Email"));
		
		model.setButton(Social.BLUESKY, view.createButton("LOG IN", blueskyLogin()));
		model.setButton(Social.MASTODON, view.createButton("LOG IN", mastodonLogin()));
		
		VBox blueskyLayout = view.createContainer(10, model.getLabel(Social.BLUESKY), model.getField(Social.BLUESKY), model.getButton(Social.BLUESKY));
		VBox mastodonLayout = view.createContainer(10, model.getLabel(Social.MASTODON), model.getField(Social.MASTODON), model.getButton(Social.MASTODON));
		
		view.displayPage(stage, view.createLayout(40, blueskyLayout, mastodonLayout));
	}
	
	public Login getModel() {
		return model;
	}
	
	public LoginView getView() {
		return view;
	}
}
