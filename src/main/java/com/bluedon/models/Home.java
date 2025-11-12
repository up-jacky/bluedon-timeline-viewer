package com.bluedon.models;

import com.bluedon.enums.Social;
import com.bluedon.view.ui.buttons.FilterButton;
import com.bluedon.view.ui.cards.Post;
import com.bluedon.view.ui.images.Avatar;
import com.bluedon.services.AuthSession;
import com.bluedon.services.ServiceRegistry;
import com.bluedon.utils.Toast;

import java.util.List;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;


public class Home {
    private boolean displayBluesky = true;
    private Button blueskyLoginButton;
    
    private boolean displayMastodon = true;
    private Button mastodonLoginButton;
    
    public VBox postsContainer;
    private List<Post> posts;

	public boolean getTimeline(Social social) {
		switch(social) {
			case BLUESKY: return displayBluesky;
			case MASTODON: return displayMastodon;
			default: return false;
		}
	}
    
    public void setButton(Social social, Button button) {
    	switch(social) {
    	case BLUESKY: blueskyLoginButton = button;
    		break;
    	case MASTODON: mastodonLoginButton = button;
    		break;
    	}
    }
    
    public void setTimeline(Social social, boolean bool) {
    	switch(social) {
    		case BLUESKY: displayBluesky = bool;
    			break;
    		case MASTODON: displayMastodon = bool;
    			break;
    		default: break;
    	}
    }

	public void setPosts(List<Post> posts) {
		this.posts = posts;
	}
    
    public boolean isDisplayed(Social social) {
    	switch(social) {
			case BLUESKY: return displayBluesky;
			case MASTODON: return displayMastodon;
			default: return false;
    	}
    }
    
    public Button getLoginButton(Social social) {
    	switch(social) {
    		case BLUESKY: return blueskyLoginButton;
    		case MASTODON: return mastodonLoginButton;
    		default: return null;
    	}
    }

	private Text createDisplayName(String text) {
		Text displayName = new Text(text);
		return displayName;
	}

	private Text createHandle(String text) {
		Text handle = new Text(text);
		return handle;
	}

	public Pane getUserProfile(Social social) {
		AuthSession blueskySession = ServiceRegistry.getBlueskySession();
		AuthSession mastodonSession = ServiceRegistry.getMastodonSession();
		VBox names;
		Pane profile;
		Circle avatar;

		switch (social) {
			case BLUESKY:
				if (blueskySession != null) {
					Text displayName = createDisplayName(blueskySession.displayName);
					Text handle = createHandle("@"+blueskySession.handle);
					displayName.getStyleClass().addAll("bluesky","name");
					handle.getStyleClass().addAll("bluesky","handle");
					names = new VBox(4, displayName, handle);
					avatar = new Avatar(blueskySession.avatarUri).getCircleImage(32);
					avatar.getStyleClass().addAll("bluesky","avatar");
					HBox temp = new HBox(16, avatar, FilterButton.createButton(social));
					temp.setAlignment(Pos.CENTER);
					profile = new VBox(16, temp, names);
					return profile;
				} break;
			case MASTODON:
				if (mastodonSession != null) {
					Text displayName = createDisplayName(mastodonSession.displayName);
					Text handle = createHandle("@"+mastodonSession.handle);
					displayName.getStyleClass().addAll("mastodon","name");
					handle.getStyleClass().addAll("mastodon","handle");
					names = new VBox(4, displayName, handle);
					avatar = new Avatar(mastodonSession.avatarUri).getCircleImage(32);
					avatar.getStyleClass().addAll("mastodon","avatar");
					HBox temp = new HBox(16, avatar, FilterButton.createButton(social));
					temp.setAlignment(Pos.CENTER);
					profile = new VBox(16, temp, names);
					return profile;
				} break;
			default:
				return null;
		}
		return null;
	}
    
    public VBox getUIComponents(Social social) {
		switch (social) {
			case BLUESKY:
				if (ServiceRegistry.getBlueskySession() == null) {
					return new VBox(20, getLoginButton(social));
				}  else {
					return new VBox(20, getUserProfile(social), getLoginButton(social));
				}
			default:
				if (ServiceRegistry.getMastodonSession()  == null) {
					return new VBox(20, getLoginButton(social));
				} else {
					return new VBox(20, getUserProfile(social), getLoginButton(social));
				}
		}
    	
    }
    
    public void refreshPosts() {
		if(postsContainer == null) postsContainer = new VBox();
    	postsContainer.getChildren().clear();

		int i = 0;
		System.out.println("[INFO][Home][refreshPosts] Updating postsContainer...");
		Toast.info.showToast("Updating posts...");
		System.out.println("[INFO][Home][refreshPosts] Total # of posts: " + posts.size());
		System.out.println("[INFO][Home][refreshPosts] Bluesky status: " + ServiceRegistry.isBlueskyLoggedIn() + " Mastodon status: " + ServiceRegistry.isMastodonLoggedIn());
		for (Post post: posts) {
			boolean display = false;

			if(post.social == Social.MASTODON && displayMastodon) display = ServiceRegistry.isMastodonLoggedIn();
			if(post.social == Social.BLUESKY && displayBluesky) display = ServiceRegistry.isBlueskyLoggedIn();
			if(display) {
				System.out.println("[INFO][Home][refreshPosts] Adding Post #" + i + "...");
				Pane postContainer = post.createPostCard();
				postsContainer.getChildren().add(postContainer);
				System.out.println("[INFO][Home][refreshPosts] Done adding Post #" + i);
				Toast.info.showToast("Loaded " + (i + 1) + "/" + posts.size() + " posts.", 1000);
			}
			i += 1;
		}
		System.out.println("[INFO][Home][refreshPosts] Done updating postsContainer.");
		Toast.success.showToast("Done updating posts!");
    }
}
