package com.bluedon.models;

import com.bluedon.enums.Social;
import com.bluedon.view.ui.cards.Post;
import com.bluedon.view.ui.images.Avatar;
import com.bluedon.services.AuthSession;
import com.bluedon.services.BlueskyClient;
import com.bluedon.services.MastodonClient;
import com.bluedon.services.ServiceRegistry;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;


public class Home {
    private boolean displayBluesky = true;
    private String blueskyUsername;
	private String blueskyDisplayName;
	private String blueskyAvatarUri;
    private Button blueskyLoginButton;
    
    private boolean displayMastodon = true;
    private boolean isMastodonLoggedIn = false;
    private String mastodonUsername;
	private String mastodonDisplayName;
	private String mastodonAvatarUri;
    private Button mastodonLoginButton;
    
    private VBox postsContainer = new VBox(15);
    public List<Post> posts = new ArrayList<>();
    
    public void setButton(Social social, Button button) {
    	switch(social) {
    	case BLUESKY: blueskyLoginButton = button;
    		break;
    	case MASTODON: mastodonLoginButton = button;
    		break;
    	}
    }
    
    public void setProfile(Social social, String username, String displayName, String avatarUri) {
    	switch(social) {
    		case BLUESKY: 
				blueskyUsername = username;
				blueskyDisplayName = displayName;
				blueskyAvatarUri = avatarUri;
    			break;
    		case MASTODON: 
				mastodonUsername = username;
				mastodonDisplayName = displayName;
				mastodonAvatarUri = avatarUri;
    			break;
    		default: break;
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
    
    public boolean isDisplayed(Social social) {
    	switch(social) {
			case BLUESKY: return displayBluesky;
			case MASTODON: return displayMastodon;
			default: return false;
    	}
    }
    
    public String getUsername(Social social) {
    	switch(social) {
			case BLUESKY: return blueskyUsername;
			case MASTODON: return mastodonUsername;
			default: return "";
    	}
    }
    
    public Button getLoginButton(Social social) {
    	switch(social) {
    		case BLUESKY: return blueskyLoginButton;
    		case MASTODON: return mastodonLoginButton;
    		default: return null;
    	}
    }

	public HBox getUserProfile(Social social) {
		VBox names;
		HBox profile;
		Circle avatar;

		switch (social) {
			case BLUESKY:
				names = new VBox(4, new Label(blueskyDisplayName), new Label(blueskyUsername));
				avatar = new Avatar(blueskyAvatarUri).getCircleImage(36);
				profile = new HBox(4, avatar, names);
				return profile;
			case MASTODON:
				names = new VBox(4, new Label(mastodonDisplayName), new Label(mastodonUsername));
				avatar = new Avatar(mastodonAvatarUri).getCircleImage(36);
				profile = new HBox(4, avatar, names);
				return profile;
			default:
				return null;
		}
	}
    
    public Button getFilterButton(Social social) {
    	switch(social) {
    		case BLUESKY:
    			Button blueskyFilterButton = new Button("Bluesky Posts");
    			blueskyFilterButton.getStyleClass().add("active");
    			blueskyFilterButton.setOnAction(e -> {
    				displayBluesky = !displayBluesky;
    				updateFilterButtonStyle(blueskyFilterButton, displayBluesky);
    				refreshPosts();
    			});
    			return blueskyFilterButton;
    		case MASTODON:
    			Button mastodonFilterButton = new Button("Mastodon Posts");
    			mastodonFilterButton.getStyleClass().add("active");
    			mastodonFilterButton.setOnAction(e -> {
    				displayMastodon = !displayMastodon;
    				updateFilterButtonStyle(mastodonFilterButton, displayMastodon);
    				refreshPosts();
    			});
    			return mastodonFilterButton;
    		default: return null;
    	}
    }
    
    public Button getRefreshButton() {
    	Button button = new Button("Refresh Posts");
    	button.setOnAction(e-> refreshPosts());
    	return button;
    }
    
    // public Button getLogoutAllButton() {
    // 	Button button = new Button("Log Out All Accounts");
    // 	button.setOnAction(e -> {
    // 		// isBlueskyLoggedIn = false;
    // 		blueskyUsername = null;
    		
    // 		isMastodonLoggedIn = false;
    // 		mastodonUsername = null;
	// 		PageController.displayLoginPage();
    // 	});
    // 	return button;
    // }
    
    public VBox getPostsContainer() {
    	return postsContainer;
    }
    
    public VBox getUIComponents(Social social) {
		switch (social) {
			case BLUESKY:
				if (blueskyUsername == null) {
					return new VBox(20, getFilterButton(social), getLoginButton(social));
				}  else {
					return new VBox(20, getFilterButton(social), getUserProfile(social), getLoginButton(social));
				}
			default:
				if (mastodonUsername  == null) {
					return new VBox(20, getFilterButton(social), getLoginButton(social));
				} else {
					return new VBox(20, getFilterButton(social), getUserProfile(social), getLoginButton(social));
				}
		}
    	
    }

	public void getTimeline() {
		BlueskyClient blueskyClient = ServiceRegistry.getBlueskyClient();
		MastodonClient mastodonClient = ServiceRegistry.getMastodonClient();

		AuthSession blueskySession = ServiceRegistry.getBlueskySession();
		AuthSession mastodonSession = ServiceRegistry.getMastodonSession();

		if (blueskySession != null){
			try {
				if (blueskySession.accessToken == null) {
					System.err.println("[ERROR] Bluesky is not authenticated!");
				} else {
					String pdsOrigin = ServiceRegistry.getBlueskyPdsOrigin();
					if(pdsOrigin == null) {
						System.err.println("[ERROR] No PDS origin found in Bluesky session. Please re-authenticate");
					} else {
						Object timeline = blueskyClient.getTimeline(blueskySession, pdsOrigin);
						System.out.println("[INFO] GET Timeline Success! \n**\n" + timeline + "\n**");

						// for (Map<String, Object> post: timeline.) {

						// }
					}
				}
			} catch(Exception e) {
				System.err.println("[ERROR] Failed to get user timeline." + e.getMessage());
				e.printStackTrace();
			}
		}

		if (mastodonSession != null) {
			try {
				if (mastodonSession.accessToken == null) {
					System.err.println("[ERROR] Mastodon is not authenticated!");
				} else {
					Object timeline = mastodonClient.getTimeline(mastodonSession);
					System.out.println("[INFO] GET Timeline Success! \n**\n" + timeline + "\n**");
				}
			} catch(Exception e) {
				System.err.println("[ERROR] Failed to get user timeline." + e.getMessage());
				e.printStackTrace();
			}
		}
	}
    
    // public void updateBlueskyLoginButton() {
    //     if (isBlueskyLoggedIn) {
    //         blueskyLoginButton.setText("Log Out Bluesky");
    //         blueskyLoginButton.setOnAction(e -> {
    //         	isBlueskyLoggedIn = false;
    //             blueskyUsername = null;
    //             updateBlueskyLoginButton();
    //             refreshPosts();
    //         });
    //         blueskyUsernameLabel.setText("Logged in as @" + blueskyUsername);
    //     } else {
    //     	blueskyLoginButton.setText("Log In Bluesky");
    //     	blueskyLoginButton.setOnAction(e -> {
    //             LoginDialog user = new LoginDialog("Bluesky");
    //             if (user.getUsername() != null && !user.getUsername().isBlank()) {
    //             	isBlueskyLoggedIn = true;
    //                 blueskyUsername = user.getUsername();
    //                 updateBlueskyLoginButton();
    //                 refreshPosts();
    //             } else if(user.getUsername() == null && !user.getUsername().isBlank()) {
    //                 new ErrorMessage("Failed to Login", "Input cannot be empty.");
    //             }
    //         });
    //         blueskyUsernameLabel.setText("");
    //     }
    // }

    private void updateFilterButtonStyle(Button btn, boolean active) {
        if (active) {
            if (!btn.getStyleClass().contains("active")) {
                btn.getStyleClass().add("active");
            }
        } else {
            btn.getStyleClass().remove("active");
        }
    }
    
    // public void updateMastodonLoginButton() {
    //     if (isMastodonLoggedIn) {
    //         mastodonLoginButton.setText("Log Out Mastodon");
    //         mastodonLoginButton.setOnAction(e -> {
    //         	isMastodonLoggedIn = false;
    //             mastodonUsername = null;
    //             updateMastodonLoginButton();
    //             refreshPosts();
    //         });
    //         mastodonUsernameLabel.setText("Logged in as @" + mastodonUsername);
    //     } else {
    //     	mastodonLoginButton.setText("Log In Mastodon");
    //     	mastodonLoginButton.setOnAction(e -> {
    //             LoginDialog user = new LoginDialog("Mastodon");
    //             if (user.getUsername() != null) {
    //             	isMastodonLoggedIn = true;
    //                 mastodonUsername = user.getUsername();
    //                 updateMastodonLoginButton();
    //                 refreshPosts();
    //             } else {
    //                 new ErrorMessage("Failed to Login!", "Input cannot be empty.");
    //             }
    //         });
    //         mastodonUsernameLabel.setText("");
    //     }
    // }
    
    public void refreshPosts() {
    	postsContainer.getChildren().clear();
    	
    	for (Post post: posts) {
    		boolean show = false;
    		
    		// if(post.type == Social.BLUESKY && displayBluesky) show = isBlueskyLoggedIn;
    		if(post.type == Social.MASTODON && displayMastodon) show = isMastodonLoggedIn;
    		if(show) postsContainer.getChildren().add(post.createPostCard());
    	}
    }
    
}
