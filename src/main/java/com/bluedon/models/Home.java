package com.bluedon.models;

import com.bluedon.enums.Social;
import com.bluedon.view.ui.cards.Post;
import com.bluedon.view.ui.images.Avatar;
import com.bluedon.services.AuthSession;
import com.bluedon.services.BlueskyClient;
import com.bluedon.services.MastodonClient;
import com.bluedon.services.ServiceRegistry;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.json.JSONArray;
import org.json.JSONObject;

import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;


public class Home {
    private boolean displayBluesky = true;
    private String blueskyUsername;
	private String blueskyDisplayName;
	private String blueskyAvatarUri;
    private Button blueskyLoginButton;
    
    private boolean displayMastodon = true;
    private String mastodonUsername;
	private String mastodonDisplayName;
	private String mastodonAvatarUri;
    private Button mastodonLoginButton;
    
    public VBox postsContainer;
    private List<Post> posts = new ArrayList<>();
    
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
				names = new VBox(4, new Text(blueskyDisplayName), new Text(blueskyUsername));
				avatar = new Avatar(blueskyAvatarUri).getCircleImage(36);
				profile = new HBox(4, avatar, names);
				return profile;
			case MASTODON:
				names = new VBox(4, new Text(mastodonDisplayName), new Text(mastodonUsername));
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
    	button.setOnAction(e-> {
			fetchTimeline();
			refreshPosts();
		});
    	return button;
    }
    
	private final static CompletableFuture<Boolean> isDoneFetching = new CompletableFuture<>();

    public Boolean isDoneFetching(int timeoutInSecs) {
		try {
	    	return isDoneFetching.get(timeoutInSecs, TimeUnit.SECONDS);
		} catch (Exception e) {
			return null;
		}
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

	public void fetchTimeline() {
		BlueskyClient blueskyClient = ServiceRegistry.getBlueskyClient();
		MastodonClient mastodonClient = ServiceRegistry.getMastodonClient();

		AuthSession blueskySession = ServiceRegistry.getBlueskySession();
		AuthSession mastodonSession = ServiceRegistry.getMastodonSession();

		if (postsContainer != null) postsContainer.getChildren().clear();
		posts.clear();

		if (blueskySession != null){
			try {
				if (blueskySession.accessToken == null) {
					System.err.println("[ERROR] Bluesky is not authenticated!");
				} else {
					String pdsOrigin = ServiceRegistry.getBlueskyPdsOrigin();
					if(pdsOrigin == null) {
						System.err.println("[ERROR] No PDS origin found in Bluesky session. Please re-authenticate");
					} else {
						JSONObject timelineRaw = blueskyClient.getTimeline(blueskySession, pdsOrigin);
						JSONArray timeline = timelineRaw.getJSONArray("feed");
						for(int i = 0; i < timeline.length(); i += 1) {
							JSONObject post = timeline.getJSONObject(i).getJSONObject("post");
							System.out.print(String.format("[RECORD %d]: ", i));
							getAllKeySet(post, "root", 0);
							JSONObject author = post.getJSONObject("author");
							String displayName = author.getString("displayName");
							String username = author.getString("handle");
							String avatarUrl = author.getString("avatar");
							String content = post.getJSONObject("record").getString("text");
							String createdAt = post.getString("indexedAt");
							String uri = post.getString("uri");
							int likeCount = post.getInt("likeCount");
							int replyCount = post.getInt("replyCount");
							int repostCount = post.getInt("repostCount");
							int bookmarkCount = post.getInt("bookmarkCount");
							JSONObject embed = null;
							try{
								embed = post.getJSONObject("embed");
							} catch (Exception e) {
								System.out.println("[INFO] No embed found.");
							}
							Post postCard = new Post(Social.BLUESKY, displayName, username, avatarUrl, content, uri, createdAt, replyCount, repostCount, likeCount, likeCount, bookmarkCount, embed);
							posts.add(postCard);
						}
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
					JSONObject timelineRaw = mastodonClient.getTimeline(mastodonSession);
					JSONArray timeline = timelineRaw.getJSONArray("feed");

					for(int i = 0; i < timeline.length(); i += 1) {
						JSONObject post = timeline.getJSONObject(i);
						String content = post.getString("content");
						if(content == null || content.trim().isEmpty()) {
							post = post.getJSONObject("reblog");
						}
						content = post.getString("content");
						JSONObject account = post.getJSONObject("account");
						String url = post.getString("url");
						String displayName = account.getString("display_name");
						String username = account.getString("username");
						String avatarUrl = account.getString("avatar");
						String timeCreated = post.getString("created_at");
						int favoritesCount = post.getInt("favourites_count");
						int reblogsCount = post.getInt("reblogs_count");
						int quotesCount = post.getInt("quotes_count");
						Post postCard = new Post(Social.MASTODON, url, displayName, username, avatarUrl, content, timeCreated, favoritesCount, reblogsCount, quotesCount);
						posts.add(postCard);
					}
				}
			} catch(Exception e) {
				System.err.println("[ERROR] Failed to get user timeline. " + e.getMessage());
				e.printStackTrace();
			}
		}
		posts.sort(Comparator.comparing(Post::getCreatedAt).reversed());
		isDoneFetching.complete(true);
	}

	private void getAllKeySet(JSONObject jsonObject, String parent, int depth) {
		System.out.println("----".repeat(depth)+"("+parent+") " + jsonObject.keySet());
		
		for(String key: jsonObject.keySet()) {
			if (jsonObject.get(key).getClass().getName() == JSONObject.class.getName()) {
				getAllKeySet(jsonObject.getJSONObject(key), key, depth + 1);
			} else if (jsonObject.get(key).getClass().getName() == JSONArray.class.getName()) {
				JSONArray jsonArray = (JSONArray) jsonObject.get(key);
				if(jsonArray.length() > 0) {
					if (jsonArray.get(0).getClass().getName() == JSONObject.class.getName()) {
						getAllKeySet(jsonArray.getJSONObject(0), key, depth + 1);
					} else {
						System.out.println("----".repeat(depth)+"("+parent+") " + key  + " -- " + jsonObject.get(key).getClass().getName() + " -- " + jsonObject.get(key).toString());
					}
				}
			} else {
				System.out.println("----".repeat(depth)+"("+parent+") " + key + " -- " + jsonObject.get(key).getClass().getName() + " -- " + jsonObject.get(key).toString());
			}
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
    
    public void refreshPosts() {
		if(postsContainer == null) postsContainer = new VBox(24);
    	postsContainer.getChildren().clear();

		for (Post post: posts) {
			boolean display = false;

			if(post.social == Social.MASTODON && displayMastodon) display = ServiceRegistry.isMastodonLoggedIn();
			if(post.social == Social.BLUESKY && displayBluesky) display = ServiceRegistry.isBlueskyLoggedIn();
			if(display) {
				Pane postContainer = post.createPostCard();
				postsContainer.getChildren().add(postContainer);
			}

		}
    }
    
}
