package viewer;

import application.Main;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class LoginPage {

    private final VBox layout;

    public LoginPage(Main app) {
        Image logo = new Image(getClass().getResource("/bluedon.png").toExternalForm());
        ImageView logoView = new ImageView(logo);
        logoView.setFitHeight(100);
        logoView.setPreserveRatio(true);

        Label blueskyLabel = new Label("Bluesky Email");
        TextField blueskyField = new TextField();
        blueskyField.setPromptText("Enter Bluesky Email");
        blueskyField.getStyleClass().add("email-field");

        Button blueskyLoginBtn = new Button("LOG IN");
        blueskyLoginBtn.getStyleClass().add("login-btn");
        blueskyLoginBtn.setOnAction(e -> {
            String email = blueskyField.getText().trim();
            if (!email.isEmpty()) {
                app.showHomePage("BLUESKY", email);
            }
        });

        VBox blueskyBox = new VBox(10, blueskyLabel, blueskyField, blueskyLoginBtn);
        blueskyBox.setAlignment(Pos.CENTER);
        blueskyBox.getStyleClass().add("input-box");

        Label mastodonLabel = new Label("Mastodon Email");
        TextField mastodonField = new TextField();
        mastodonField.setPromptText("Enter Mastodon Email");
        mastodonField.getStyleClass().add("email-field");

        Button mastodonLoginBtn = new Button("LOG IN");
        mastodonLoginBtn.getStyleClass().add("login-btn");
        mastodonLoginBtn.setOnAction(e -> {
            String email = mastodonField.getText().trim();
            if (!email.isEmpty()) {
                app.showHomePage("MASTODON", email);
            }
        });

        VBox mastodonBox = new VBox(10, mastodonLabel, mastodonField, mastodonLoginBtn);
        mastodonBox.setAlignment(Pos.CENTER);
        mastodonBox.getStyleClass().add("input-box");

        HBox accountsBox = new HBox(40, blueskyBox, mastodonBox);
        accountsBox.setAlignment(Pos.CENTER);

        layout = new VBox(50, logoView, accountsBox);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(40));
        layout.getStyleClass().add("login-root");
    }

    public VBox getView() {
        return layout;
    }
}

