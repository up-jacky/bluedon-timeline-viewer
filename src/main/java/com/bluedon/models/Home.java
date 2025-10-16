package com.bluedon.models;

import com.bluedon.enums.Social;
import com.bluedon.view.ui.cards.Post;
import com.bluedon.view.ui.cards.LoginDialog;
import com.bluedon.exception.ErrorMessage;

import java.util.ArrayList;
import java.util.List;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;

import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;


public class Home {
    private boolean displayBluesky = true;
    private boolean isBlueskyLoggedIn = false;
    private String blueskyUsername = "";
    private Label blueskyUsernameLabel = new Label("");
    private Button blueskyLoginButton;
    
    private boolean displayMastodon = true;
    private boolean isMastodonLoggedIn = false;
    private String mastodonUsername = "";
    private Label mastodonUsernameLabel = new Label("");
    private Button mastodonLoginButton;
    
    private VBox postsContainer = new VBox(15);
    public List<Post> posts = new ArrayList<>();
    
    public Home(String blueskyEmail, String mastodonEmail) {
        if (blueskyEmail != null && !blueskyEmail.isBlank()) {
            isBlueskyLoggedIn = true;
            blueskyUsername = blueskyEmail;
        }
        if (mastodonEmail != null && !mastodonEmail.isBlank()) {
            isMastodonLoggedIn = true;
            mastodonUsername = mastodonEmail;
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
    
    public void setUsernameLabel(Social social, Label label) {
    	switch(social) {
    		case BLUESKY: blueskyUsernameLabel = label;
    			break;
    		case MASTODON: mastodonUsernameLabel = label;
    			break;
    		default: break;
    	}
    }
    
    public void setUsername(Social social, String username) {
    	switch(social) {
    		case BLUESKY: blueskyUsername = username;
    			break;
    		case MASTODON: mastodonUsername = username;
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
    
    public void login(Social social) {  
    	switch(social) {
			case BLUESKY: isBlueskyLoggedIn = true;
				break;
			case MASTODON: isMastodonLoggedIn = true;
				break;
			default: break;
		}
    }
    
    public void logout(Social social) {
    	switch(social) {
			case BLUESKY: isBlueskyLoggedIn = false;
				break;
			case MASTODON: isMastodonLoggedIn = false;
				break;
			default: break;
		}
    }
    
    public boolean isLoggedIn(Social social) {
    	switch(social) {
			case BLUESKY: return isBlueskyLoggedIn;
			case MASTODON: return isMastodonLoggedIn;
			default: return false;
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

    public Label getUsernameLabel(Social social) {
    	switch(social) {
			case BLUESKY: return blueskyUsernameLabel;
			case MASTODON: return mastodonUsernameLabel;
			default: return null;
    	}
    }
    
    public Button getLoginButton(Social social) {
    	switch(social) {
    		case BLUESKY: return blueskyLoginButton;
    		case MASTODON: return mastodonLoginButton;
    		default: return null;
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
    
    public Button getLogoutAllButton() {
    	Button button = new Button("Log Out All Accounts");
    	button.setOnAction(e -> {
    		isBlueskyLoggedIn = false;
    		blueskyUsername = null;
    		
    		isMastodonLoggedIn = false;
    		mastodonUsername = null;
    	});
    	return button;
    }
    
    public VBox getPostsContainer() {
    	return postsContainer;
    }
    
    public VBox getUIComponents(Social social) {
    	return new VBox(20, getFilterButton(social), getLoginButton(social), getUsernameLabel(social));
    }
    
    public void loadPostsFromCSV(String resourcePath) {
    	try (InputStream is = getClass().getResourceAsStream(resourcePath);
    			BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
    		
    		String line;
    		reader.readLine();
    		while ((line = reader.readLine()) != null) {
    			String[] parts = line.split(",", 4);
    			if (parts.length == 4) {
    				String user = parts[0].trim();
    				String message = parts[1].trim().replace("\"", "");
    				String timestamp = parts[2].trim();
    				Social type = Social.valueOf(parts[3].trim().toUpperCase());
    				posts.add(new Post(user, message, timestamp, type));
    			}
    		}
    		
    	} catch (IOException | NullPointerException e) {
    		new ErrorMessage(null,"Error loading CSV: " + e.getMessage());
    	}
    }
    
    public void updateBlueskyLoginButton() {
        if (isBlueskyLoggedIn) {
            blueskyLoginButton.setText("Log Out Bluesky");
            blueskyLoginButton.setOnAction(e -> {
            	isBlueskyLoggedIn = false;
                blueskyUsername = null;
                updateBlueskyLoginButton();
                refreshPosts();
            });
            blueskyUsernameLabel.setText("Logged in as @" + blueskyUsername);
        } else {
        	blueskyLoginButton.setText("Log In Bluesky");
        	blueskyLoginButton.setOnAction(e -> {
                LoginDialog user = new LoginDialog("Bluesky");
                if (user.getUsername() != null && !user.getUsername().isBlank()) {
                	isBlueskyLoggedIn = true;
                    blueskyUsername = user.getUsername();
                    updateBlueskyLoginButton();
                    refreshPosts();
                } else if(user.getUsername() == null && !user.getUsername().isBlank()) {
                    new ErrorMessage("Failed to Login", "Input cannot be empty.");
                }
            });
            blueskyUsernameLabel.setText("");
        }
    }

    private void updateFilterButtonStyle(Button btn, boolean active) {
        if (active) {
            if (!btn.getStyleClass().contains("active")) {
                btn.getStyleClass().add("active");
            }
        } else {
            btn.getStyleClass().remove("active");
        }
    }
    
    public void updateMastodonLoginButton() {
        if (isMastodonLoggedIn) {
            mastodonLoginButton.setText("Log Out Mastodon");
            mastodonLoginButton.setOnAction(e -> {
            	isMastodonLoggedIn = false;
                mastodonUsername = null;
                updateMastodonLoginButton();
                refreshPosts();
            });
            mastodonUsernameLabel.setText("Logged in as @" + mastodonUsername);
        } else {
        	mastodonLoginButton.setText("Log In Mastodon");
        	mastodonLoginButton.setOnAction(e -> {
                LoginDialog user = new LoginDialog("Mastodon");
                if (user.getUsername() != null) {
                	isMastodonLoggedIn = true;
                    mastodonUsername = user.getUsername();
                    updateMastodonLoginButton();
                    refreshPosts();
                } else {
                    new ErrorMessage("Failed to Login!", "Input cannot be empty.");
                }
            });
            mastodonUsernameLabel.setText("");
        }
    }
    
    public void refreshPosts() {
    	postsContainer.getChildren().clear();
    	
    	for (Post post: posts) {
    		boolean show = false;
    		
    		if(post.type == Social.BLUESKY && displayBluesky) show = isBlueskyLoggedIn;
    		if(post.type == Social.MASTODON && displayMastodon) show = isMastodonLoggedIn;
    		if(show) postsContainer.getChildren().add(post.createPostCard());
    	}
    }
    
}
