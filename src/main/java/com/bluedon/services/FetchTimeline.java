package com.bluedon.services;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.bluedon.controllers.PageController;
import com.bluedon.enums.Social;
import com.bluedon.models.Home;
import com.bluedon.utils.Toast;
import com.bluedon.view.HomeView;
import com.bluedon.view.ui.buttons.RefreshButton;
import com.bluedon.view.ui.cards.Post;

import javafx.concurrent.Task;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class FetchTimeline extends Task<Boolean> {
    private Stage stage = PageController.getStage();
    private HomeView view = PageController.home.getView();
    private Home model = PageController.home.getModel();
    private List<Post> posts = new ArrayList<>();
    private static FetchTimeline instance;

    public static FetchTimeline getInstance() {
        return instance;
    }

    public void fetchTimeline() {
        FetchBluesky fetchBluesky = new FetchBluesky();
        FetchMastodon fetchMastodon = new FetchMastodon();
        Thread blueskyThread = new Thread(fetchBluesky);
        Thread mastodonThread = new Thread(fetchMastodon);

		posts.clear();

		if (ServiceRegistry.isBlueskyLoggedIn()){
			try {
                if(ServiceRegistry.getBlueskyPdsOrigin() == null) {
                    System.err.println("[ERROR][FetchTimeline][fetchTimeline] No PDS origin found in Bluesky session. Please re-authenticate");
                    throw new IllegalStateException("Unauthorized Session: Registry has no 'blueskyPdsOrigin' or 'blueskyPdsOrigin' is empty.");
                } else {
                    blueskyThread.setDaemon(true);
                    blueskyThread.start();
				}
			} catch(Exception e) {
				System.err.println("[ERROR][FetchTimeline][fetchTimeline] Failed to fetch Bluesky timeline:" + e.getMessage());
                Toast.error.showToast("Failed to fetch Bluesky timeline! Error: " + e.getMessage());
				e.printStackTrace();
			}
		}

		if (ServiceRegistry.isMastodonLoggedIn()) {
			try {
                mastodonThread.setDaemon(true);
                mastodonThread.start();
			} catch(Exception e) {
				System.err.println("[ERROR][FetchTimeline][fetchTimeline] Failed to fetch Mastodon timeline: " + e.getMessage());
                Toast.error.showToast("Failed to fetch Mastodon timeline! Error: " + e.getMessage());
				e.printStackTrace();
			}
		}

        try {
            if (ServiceRegistry.isBlueskyLoggedIn()) {
                blueskyThread.join();
                posts.addAll(fetchBluesky.get());
            }
            if (ServiceRegistry.isMastodonLoggedIn()) {
                mastodonThread.join();
                posts.addAll(fetchMastodon.get());
            }
        } catch (Exception e) {
            System.err.println("[ERROR][FetchTimeline][fetchTimeline] Failed to fetch timeline: " + e.getMessage());
            Toast.error.showToast("Failed to fetch timeline! Error: " + e.getMessage());
            e.printStackTrace();
        }
        
		posts.sort(Comparator.comparing(Post::getCreatedAt).reversed());
	}

    private void printChild(int depth, String parent, String _class, String child, String value) {
        System.out.println("  ".repeat(depth+1) + String.format("|- [%s][%s] %s: %s", parent, _class, child, value));
    }
    
    @SuppressWarnings("unused")
    private void getAllKeySet( int depth, String parent, String child, JSONObject jsonObject) {
        if(depth > 0) System.out.println("  ".repeat(depth)+String.format("\\ [%s][%s] %s: ", parent, jsonObject.getClass().getName(), child) + jsonObject.keySet());
        else System.out.println(String.format("[%s][%s] %s: ", parent, jsonObject.getClass().getName(), child) + jsonObject.keySet());
		
		for(String key: jsonObject.keySet()) {
			if (jsonObject.get(key).getClass().getName() == JSONObject.class.getName()) {
				getAllKeySet(depth + 1, child, key, jsonObject.getJSONObject(key));
			} else if (jsonObject.get(key).getClass().getName() == JSONArray.class.getName()) {
                printChild(depth, child, jsonObject.get(key).getClass().getName(), key, "START");
				JSONArray jsonArray = jsonObject.getJSONArray(key);
                for(int i = 0; i < jsonArray.length(); i += 1) {
					if (jsonArray.get(i).getClass().getName() == JSONObject.class.getName()) getAllKeySet(depth + 1, key, String.format("%d",i), jsonArray.getJSONObject(i));
					else printChild(depth, key, jsonArray.get(i).getClass().getName(), String.format("%d",i), jsonArray.get(i).toString());
                }
                printChild(depth, child, jsonObject.get(key).getClass().getName(), key, "END");
			} else {
                printChild(depth, child, jsonObject.get(key).getClass().getName(), key, jsonObject.get(key).toString());
			}
		}
	}

    public static void start() {
        instance = new FetchTimeline();
        Thread thread = new Thread(instance);
        thread.setDaemon(true);
        thread.start();
    }
    
    @Override
    protected Boolean call() throws Exception {
        System.out.println("[DEBUG][FetchTimeline][call] Thread: " + Thread.currentThread());
        fetchTimeline();
        model.setPosts(posts);
        model.refreshPosts();
        if(isCancelled()) return false;
        return true;
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        System.out.println("[DEBUG][FetchTimeline][cancel] Thread: " + Thread.currentThread());
        System.out.println("[INFO][FetchTimeline][cancel] Fetch is interrupted.");
        posts.clear();
        return super.cancel(mayInterruptIfRunning);
    }

    @Override
    protected void succeeded() {
        System.out.println("[DEBUG][FetchTimeline][succeeded] Thread: " + Thread.currentThread());
        if(getValue().booleanValue()) {
            VBox sidebar = view.createSidebar(model.getUIComponents(Social.BLUESKY), model.getUIComponents(Social.MASTODON), RefreshButton.createRefreshButton());
            ScrollPane pArea = view.createPostsArea(model.postsContainer);
            view.updateLayout(sidebar, pArea);
            view.displayPage(stage);
            stage.show();
        } else {
        }
    }

    @Override
    protected void failed() {
        System.out.println("[DEBUG][FetchTimeline][failed] Thread: " + Thread.currentThread());
        System.err.println("[ERROR] Failed to fetch timeline: " + getException().getMessage());
        Toast.error.showToast("Failed to fetch timeline! Error: " + getException().getMessage());
        getException().printStackTrace();
        VBox sidebar = view.createSidebar(model.getUIComponents(Social.BLUESKY), model.getUIComponents(Social.MASTODON), RefreshButton.createRefreshButton());
        ScrollPane pArea = view.createPostsArea(getException().getMessage());
        view.updateLayout(sidebar, pArea);
        view.displayPage(stage);
        stage.show();
    }

    private class FetchMastodon extends Task<List<Post>> {
        private List<Post> posts = new ArrayList<>();

        @Override
        protected List<Post> call() throws Exception {
            System.out.println("[DEBUG][FetchTimeline][FetchMastodon][call] Thread: " + Thread.currentThread());
            JSONObject timelineRaw = ServiceRegistry.getMastodonClient().getTimeline(ServiceRegistry.getMastodonSession(), 25);
            JSONArray timeline = timelineRaw.getJSONArray("feed");

            for(int i = 0; i < timeline.length(); i += 1) {
                JSONObject post = timeline.getJSONObject(i);
                // System.out.println(String.format("[DEBUG][FetchTimeline][FetchMastodon][call] POST#%d: START", i));
                // getAllKeySet(0, "root", "post", post);
                Post postCard = new Post(Social.MASTODON, post);
                posts.add(postCard);
                // System.out.println(String.format("[DEBUG][FetchTimeline][FetchMastodon][call] POST#%d: END", i));
            }
            return posts;
        }

        @Override
        protected void succeeded() {
            System.out.println("[DEBUG][FetchTimeline][FetchMastodon][succeeded] Thread: " + Thread.currentThread());
            System.out.println("[INFO][FetchTimeline][FetchMastodon][succeeded] Successfully fetch Mastodon timeline.");
        }

        @Override
        protected void failed() {
            System.out.println("[DEBUG][FetchTimeline][FetchMastodon][failed] Thread: " + Thread.currentThread());
            System.err.println("[ERROR][FetchTimeline][FetchMastodon][failed] Failed to fetch Mastodon timeline: " + getException().getMessage());
            getException().printStackTrace();
        }
    }

    private class FetchBluesky extends Task<List<Post>> {
        private List<Post> posts = new ArrayList<>();

        @Override
        protected List<Post> call() throws Exception {
            System.out.println("[DEBUG][FetchTimeline][FetchBluesky][call] Thread: " + Thread.currentThread());
            JSONObject timelineRaw = ServiceRegistry.getBlueskyClient().getTimeline(ServiceRegistry.getBlueskySession(), ServiceRegistry.getBlueskyPdsOrigin(), 25);
            JSONArray timeline = timelineRaw.getJSONArray("feed");
            for(int i = 0; i < timeline.length(); i += 1) {
                JSONObject post = timeline.getJSONObject(i).getJSONObject("post");
                JSONObject reason = null;
                try {
                    reason = timeline.getJSONObject(i).getJSONObject("reason");
                    System.out.println(String.format("[DEBUG][FetchTimeline][FetchBluesky][call] POST#%d is a repost", i));
                } catch(Exception e) {
                    reason = null;
                }
                // System.out.println(String.format("[DEBUG][FetchTimeline][FetchBluesky][call] POST#%d: START", i));
                // getAllKeySet(0, "root", "post", post);
                Post postCard = new Post(Social.BLUESKY, post, reason);
                posts.add(postCard);
                // System.out.println(String.format("[DEBUG][FetchTimeline][FetchBluesky][call] POST#%d: END", i));
            }
            return posts;
        }

        @Override
        protected void succeeded() {
            System.out.println("[DEBUG][FetchTimeline][FetchBluesky][succeeded] Thread: " + Thread.currentThread());
            System.out.println("[INFO][FetchTimeline][FetchBluesky][succeeded] Successfully fetch Bluesky timeline.");
        }

        @Override
        protected void failed() {
            System.out.println("[DEBUG][FetchTimeline][FetchBluesky][failed] Thread: " + Thread.currentThread());
            System.err.println("[ERROR][FetchTimeline][FetchBluesky][failed] Failed to fetch Bluesky timeline: " + getException().getMessage());
            getException().printStackTrace();
        }
    }
}
