package viewer;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import application.Main;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class HomePage extends BorderPane {

    private final Main app;

    private boolean showBluesky = true;
    private boolean showMastodon = true;
    private boolean blueskyLoggedIn = false;
    private boolean mastodonLoggedIn = false;

    private String blueskyUser;
    private String mastodonUser;

    private VBox postsBox = new VBox(15);
    private List<Post> allPosts = new ArrayList<>();

    private Button blueskyLoginBtn;
    private Button mastodonLoginBtn;
    private Button blueskyFilterBtn;
    private Button mastodonFilterBtn;
    private Label blueskyUserLabel;
    private Label mastodonUserLabel;

    /**
     * Updated constructor: accepts Bluesky and Mastodon emails.
     */
    public HomePage(Main app, String blueskyEmail, String mastodonEmail) {
        this.app = app;

        if (blueskyEmail != null && !blueskyEmail.isBlank()) {
            blueskyLoggedIn = true;
            blueskyUser = blueskyEmail;
        }
        if (mastodonEmail != null && !mastodonEmail.isBlank()) {
            mastodonLoggedIn = true;
            mastodonUser = mastodonEmail;
        }

        setLeft(buildSidebar());
        setCenter(buildPostsArea());

        loadPostsFromCSV("/posts.csv");
        refreshPosts();
    }

    private VBox buildSidebar() {
        VBox sidebar = new VBox(20);
        sidebar.getStyleClass().add("sidebar");
        sidebar.setPrefWidth(250);

        ImageView logo = new ImageView(
            new Image(getClass().getResource("/bluedon.png").toExternalForm())
        );
        logo.setFitWidth(150);
        logo.setPreserveRatio(true);

        blueskyFilterBtn = new Button("Bluesky Posts");
        blueskyFilterBtn.getStyleClass().add("active");
        blueskyFilterBtn.setOnAction(e -> {
            showBluesky = !showBluesky;
            updateFilterButtonStyle(blueskyFilterBtn, showBluesky);
            refreshPosts();
        });

        mastodonFilterBtn = new Button("Mastodon Posts");
        mastodonFilterBtn.getStyleClass().add("active");
        mastodonFilterBtn.setOnAction(e -> {
            showMastodon = !showMastodon;
            updateFilterButtonStyle(mastodonFilterBtn, showMastodon);
            refreshPosts();
        });

        blueskyLoginBtn = new Button();
        mastodonLoginBtn = new Button();
        blueskyUserLabel = new Label();
        mastodonUserLabel = new Label();

        updateLoginButtons();

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        Button refreshBtn = new Button("Refresh Posts");
        refreshBtn.setOnAction(e -> refreshPosts());

        Button logoutAllBtn = new Button("Log Out All Accounts");
        logoutAllBtn.setOnAction(e -> {
            app.showLoginPage();  
        });

        sidebar.getChildren().addAll(
            logo,
            blueskyFilterBtn, blueskyLoginBtn, blueskyUserLabel,
            mastodonFilterBtn, mastodonLoginBtn, mastodonUserLabel,
            spacer,
            refreshBtn,
            logoutAllBtn
        );

        return sidebar;
    }

    private void addErrorMessage(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(msg);

        alert.showAndWait(); 
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

    private void updateLoginButtons() {
        if (blueskyLoggedIn) {
            blueskyLoginBtn.setText("Log Out Bluesky");
            blueskyLoginBtn.setOnAction(e -> {
                blueskyLoggedIn = false;
                blueskyUser = null;
                updateLoginButtons();
                refreshPosts();
            });
            blueskyUserLabel.setText("Logged in as @" + blueskyUser);
        } else {
            blueskyLoginBtn.setText("Log In Bluesky");
            blueskyLoginBtn.setOnAction(e -> {
                String user = showLoginDialog("Bluesky");
                if (user != null) {
                    blueskyLoggedIn = true;
                    blueskyUser = user;
                    updateLoginButtons();
                    refreshPosts();
                } else {
                    addErrorMessage("Input cannot be empty.");
                }
            });
            blueskyUserLabel.setText("");
        }

        if (mastodonLoggedIn) {
            mastodonLoginBtn.setText("Log Out Mastodon");
            mastodonLoginBtn.setOnAction(e -> {
                mastodonLoggedIn = false;
                mastodonUser = null;
                updateLoginButtons();
                refreshPosts();
            });
            mastodonUserLabel.setText("Logged in as @" + mastodonUser);
        } else {
            mastodonLoginBtn.setText("Log In Mastodon");
            mastodonLoginBtn.setOnAction(e -> {
                String user = showLoginDialog("Mastodon");
                if (user != null) {
                    mastodonLoggedIn = true;
                    mastodonUser = user;
                    updateLoginButtons();
                    refreshPosts();
                } else {
                    addErrorMessage("Input cannot be empty.");
                }
            });
            mastodonUserLabel.setText("");
        }
    }

    private String showLoginDialog(String serviceName) {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Log In to " + serviceName);

        Label userLabel = new Label("Username:");
        TextField userField = new TextField();
        Label passLabel = new Label("Password:");
        PasswordField passField = new PasswordField();

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
        grid.add(userLabel, 0, 0);
        grid.add(userField, 1, 0);
        grid.add(passLabel, 0, 1);
        grid.add(passField, 1, 1);

        dialog.getDialogPane().setContent(grid);

        ButtonType loginButtonType = new ButtonType("Log In", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

        dialog.setResultConverter(button -> {
            if (button == loginButtonType) {
                return userField.getText().isBlank() ? null : userField.getText();
            }
            return null;
        });

        return dialog.showAndWait().orElse(null);
    }

    private ScrollPane buildPostsArea() {
        postsBox.setPadding(new Insets(20));
        ScrollPane scrollPane = new ScrollPane(postsBox);
        scrollPane.setFitToWidth(true);
        return scrollPane;
    }

    private void refreshPosts() {
        postsBox.getChildren().clear();

        for (Post post : allPosts) {
            boolean show = false;

            if (post.type == AccountType.BLUESKY && showBluesky) {
                show = blueskyLoggedIn;
            }
            if (post.type == AccountType.MASTODON && showMastodon) {
                show = mastodonLoggedIn;
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
            addErrorMessage("Error loading CSV: " + e.getMessage());
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
