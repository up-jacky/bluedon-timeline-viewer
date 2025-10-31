package com.bluedon.controllers;

import java.util.concurrent.CompletableFuture;

import com.bluedon.enums.Social;
import com.bluedon.models.Home;
import com.bluedon.services.AuthSession;
import com.bluedon.services.ServiceRegistry;
import com.bluedon.view.HomeView;
import com.bluedon.view.ui.buttons.LoginButton;

import javafx.stage.Stage;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

public class HomeController {
	private static Home model = new Home();
	private static HomeView view = new HomeView();
	
	public void start(Stage stage) {
		view.init();

		AuthSession blueskySession = ServiceRegistry.getBlueskySession();
		AuthSession mastodonSession = ServiceRegistry.getMastodonSession();

		if (blueskySession != null) {
			model.setProfile(Social.BLUESKY, blueskySession.handle, blueskySession.displayName, blueskySession.avatarUri);
		} else {
			model.setProfile(Social.BLUESKY, null, null, null);
		}
		
		if (mastodonSession != null) {
			model.setProfile(Social.MASTODON, mastodonSession.handle, mastodonSession.displayName, mastodonSession.avatarUri);
		} else {
			model.setProfile(Social.MASTODON, null, null, null);
		}

		// model.fetchTimeline();
		// model.refreshPosts();

		// CompletableFuture.runAsync(() -> fetchTimeline(stage));
		
		model.setButton(Social.BLUESKY, LoginButton.createButton(Social.BLUESKY));
		model.setButton(Social.MASTODON, LoginButton.createButton(Social.MASTODON));
		
		VBox blueskyUIComponents = model.getUIComponents(Social.BLUESKY);
		VBox mastodonUIComponents = model.getUIComponents(Social.MASTODON);
		
		VBox sidebar = view.createSidebar(blueskyUIComponents, mastodonUIComponents, model.getRefreshButton());

		ScrollPane postsArea = view.createPostsArea(model.postsContainer);
		
		view.updateLayout(sidebar, postsArea);
		
		view.displayPage(stage);
		stage.show();

		fetchTimeline(stage);
		
	}

	private void fetchTimeline(Stage stage) {
		model.fetchTimeline();
		model.refreshPosts();

		ScrollPane pArea = view.createPostsArea(model.postsContainer);
		
		view.updateLayout(null, pArea);
		
		view.displayPage(stage);
		stage.show();
	}
	
	public Home getModel() {
		return model;
	}
	
	public HomeView getView() {
		return view;
	}
}
