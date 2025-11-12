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

public class HomeController {
	private static Home model = new Home();
	private static HomeView view = new HomeView();
	
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
	
	public Home getModel() {
		return model;
	}
	
	public HomeView getView() {
		return view;
	}
}
