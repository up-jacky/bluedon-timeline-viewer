package com.bluedon.controllers;

import com.bluedon.enums.Social;
import com.bluedon.models.Login;
import com.bluedon.view.LoginView;
import com.bluedon.view.ui.buttons.LoginButton;

import javafx.stage.Stage;
import javafx.scene.layout.VBox;

/**
 * LoginController handles the communication between the {@code Login} model
 * and its view {@code LoginView}.
 * <p>
 * It initializes its local model and view for easy access.
 * 
 * Its model and view can be accessed via the methods {@code getModel} and {@code getView}
 * </p>
 */
public class LoginController {

	private static Login model = new Login();
	private static LoginView view = new LoginView();
	
	/**
	 * Initializes the view and the model to show the Home scene in the primarystage.
	 * @param stage primaryStage of the main controller goes here.
	 */
	public void start(Stage stage) {
        System.out.println("[INFO][LoginController][start] In Login...");
		view.init();
		
		model.setButton(Social.BLUESKY, LoginButton.createButton(Social.BLUESKY, stage));
		model.setButton(Social.MASTODON, LoginButton.createButton(Social.MASTODON, stage));
		
		VBox blueskyLayout = view.createContainer(10, model.getButton(Social.BLUESKY));
		VBox mastodonLayout = view.createContainer(10, model.getButton(Social.MASTODON));
		view.updateLayout(40, blueskyLayout, mastodonLayout);
		
        System.out.println("[INFO][LoginController][start] Displaying Login Page...");
		view.displayPage(stage);
		stage.show();
	}
	
	/**
	 * Returns the main model used by the controller.
	 * @return {@link Login}
	 */
	public Login getModel() {
		return model;
	}
	
	/**
	 * Returns the main view used by the controller.
	 * @return {@link LoginView}
	 */
	public LoginView getView() {
		return view;
	}
}
