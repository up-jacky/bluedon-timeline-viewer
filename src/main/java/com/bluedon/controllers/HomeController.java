package com.bluedon.controllers;

import com.bluedon.enums.Social;
import com.bluedon.models.Home;
import com.bluedon.services.FetchTimeline;
import com.bluedon.view.HomeView;
import com.bluedon.view.ui.buttons.LoginButton;
import com.bluedon.view.ui.buttons.RefreshButton;

import javafx.stage.Stage;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

/**
 * HomeController handles the communication between the {@code Home} model
 * and its view {@code HomeView}.
 * <p>
 * It initializes its local model and view for easy access.
 * 
 * Its model and view can be accessed via the methods {@code getModel} and {@code getView}
 * </p>
 */
public class HomeController {
	private static Home model = new Home();
	private static HomeView view = new HomeView();
	
	/**
	 * Initializes the view and the model to show the Home scene in the primarystage.
	 * @param stage primaryStage of the main controller goes here.
	 */
	public void start(Stage stage) {
		view.init();
		
		model.setButton(Social.BLUESKY, LoginButton.createButton(Social.BLUESKY, stage));
		model.setButton(Social.MASTODON, LoginButton.createButton(Social.MASTODON, stage));
		
		VBox blueskyUIComponents = model.getUIComponents(Social.BLUESKY);
		VBox mastodonUIComponents = model.getUIComponents(Social.MASTODON);
		
		VBox sidebar = view.createSidebar(blueskyUIComponents, mastodonUIComponents, RefreshButton.createRefreshButton());

		ScrollPane postsArea = view.createPostsArea();
		
		view.updateLayout(sidebar, postsArea);
		view.displayPage(stage);
		stage.show();

		FetchTimeline.start();
		
	}
	
	/**
	 * Returns the main model used by the controller.
	 * @return {@link Home}
	 */
	public Home getModel() {
		return model;
	}
	
	/**
	 * Returns the main view used by the controller.
	 * @return {@link HomeView}
	 */
	public HomeView getView() {
		return view;
	}
}
