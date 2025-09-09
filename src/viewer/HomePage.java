package bluedon.timeline.viewer;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class HomePage extends BorderPane {

    private final Main app;
    private final String username;

    private boolean showBluesky = true;
    private boolean showMastodon = true;

    private VBox postsBox = new VBox(15);
    private List<Post> allPosts = new ArrayList<>();

    public HomePage(Main app, String username) {
        this.app = app;
        this.username = username;

        setLeft(buildSidebar());
        setCenter(buildPostsArea());

        // Load posts from CSV in resources
        loadPostsFromCSV("/posts.csv");

        refreshPosts();
    }

    private VBox buildSidebar() {
        VBox sidebar = new VBox(20);
        sidebar.getStyleClass().add("sidebar");
        sidebar.setPrefWidth(250);

        /* Image logo = new Image("file:resources/bluedon.png");
        ImageView logoView = new ImageView(logo);
        logoView.setFitHeight(100);
        logoView.setPreserveRatio(true); 
        Label logo = new Label("Bluedon");
        logo.getStyleClass().add("logo");*/

        ImageView logo = new ImageView(
            new Image(getClass().getResource("/bluedon.png").toExternalForm())
        );

        logo.setFitWidth(150);
        logo.setPreserveRatio(true);

        Button blueskyBtn = new Button("Bluesky Emails");
        blueskyBtn.getStyleClass().add("active");
        blueskyBtn.setOnAction(e -> {
            showBluesky = !showBluesky;
            if (showBluesky) {
                blueskyBtn.getStyleClass().add("active");
            } else {
                blueskyBtn.getStyleClass().remove("active");
            }
            refreshPosts();
        });

        Button mastodonBtn = new Button("Mastodon Emails");
        mastodonBtn.getStyleClass().add("active");
        mastodonBtn.setOnAction(e -> {
            showMastodon = !showMastodon;
            if (showMastodon) {
                mastodonBtn.getStyleClass().add("active");
            } else {
                mastodonBtn.getStyleClass().remove("active");
            }
            refreshPosts();
        });
        

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        Button logoutBtn = new Button("Log Out All Accounts");
        logoutBtn.setOnAction(e -> app.showLoginPage());

        sidebar.getChildren().addAll(logo, blueskyBtn, mastodonBtn, spacer, logoutBtn);
        return sidebar;
    }

    private ScrollPane buildPostsArea() {
      refreshPosts();
        postsBox.setPadding(new Insets(20));
        ScrollPane scrollPane = new ScrollPane(postsBox);
        scrollPane.setFitToWidth(true);
        return scrollPane;
    }

    /* private void refreshPosts() {
        postsBox.getChildren().clear();

        for (Post post : allPosts) {
            if ((post.type == AccountType.BLUESKY && showBluesky) ||
                (post.type == AccountType.MASTODON && showMastodon)) {
                postsBox.getChildren().add(createPostCard(post));
            }
        }
    } */   

    private void refreshPosts() {
        postsBox.getChildren().clear();

        for (Post post : allPosts) {
            boolean show = false;

            if (post.type == AccountType.BLUESKY && showBluesky) {
                show = true;
            }
            if (post.type == AccountType.MASTODON && showMastodon) {
                show = true;
            }

            if (show) {
                postsBox.getChildren().add(createPostCard(post));
            }
        }
    }

    private HBox createPostCard(Post post) {
        HBox card = new HBox(10);
        card.getStyleClass().add("post-card");

        Circle avatar = new Circle(20);
        avatar.getStyleClass().add("avatar");

        VBox content = new VBox(5);
        Label header = new Label(post.username + "    " + post.timestamp);
        header.getStyleClass().add("post-header");

        Label body = new Label(post.message);
        body.setWrapText(true);

        content.getChildren().addAll(header, body);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Circle marker = new Circle(8);
        if (post.type == AccountType.BLUESKY) {
            marker.getStyleClass().add("bluesky-marker");
        } else {
            marker.getStyleClass().add("mastodon-marker");
        }

        card.getChildren().addAll(avatar, content, spacer, marker);
        return card;
    }

    private void loadPostsFromCSV(String resourcePath) {
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
                    AccountType type = AccountType.valueOf(parts[3].trim().toUpperCase());
                    allPosts.add(new Post(user, message, timestamp, type));
                }
            }
        } catch (IOException | NullPointerException e) {
            System.err.println("Error loading CSV: " + e.getMessage());
        }
    }

    private static class Post {
        String username;
        String message;
        String timestamp;
        AccountType type;

        Post(String username, String message, String timestamp, AccountType type) {
            this.username = username;
            this.message = message;
            this.timestamp = timestamp;
            this.type = type;
        }
    }

    private enum AccountType {
        BLUESKY, MASTODON
    }

    public Scene getView() {
        Scene scene = new Scene(this, 1000, 600);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        return scene;
    }
}
