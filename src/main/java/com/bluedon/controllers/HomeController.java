package com.bluedon.controllers;

import com.bluedon.enums.Social;
import com.bluedon.models.Home;
import com.bluedon.view.HomeView;

import javafx.stage.Stage;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

public class HomeController {
	private Home model;
	private HomeView view;
	
	public HomeController(Home model, HomeView view) {
		this.model = model;
		this.view = view;
	}
	
	public void start(Stage stage) {
		model.loadPostsFromCSV("/posts.csv");
		model.refreshPosts();
		
		model.setButton(Social.BLUESKY, view.createButton(null, null));
		model.setButton(Social.MASTODON, view.createButton(null, null));
		
		model.updateBlueskyLoginButton();
		model.updateMastodonLoginButton();
		
		VBox blueskyUIComponents = model.getUIComponents(Social.BLUESKY);
		VBox mastodonUIComponents = model.getUIComponents(Social.MASTODON);
		
		VBox sidebar = view.createSidebar(blueskyUIComponents, mastodonUIComponents, model.getRefreshButton(), model.getLogoutAllButton());
		ScrollPane postsArea = view.createPostsArea(model.getPostsContainer());
		
		BorderPane layout = view.createLayout(postsArea, sidebar);
		
		view.displayPage(stage, layout);
	}
	
	public Home getModel() {
		return model;
	}
	
	public HomeView getView() {
		return view;
	}
}
