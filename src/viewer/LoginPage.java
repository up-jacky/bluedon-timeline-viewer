package bluedon.timeline.viewer;

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

    private VBox layout;

    public LoginPage(Main app) {

        Image logo = new Image("file:resources/bluedon.png");
        ImageView logoView = new ImageView(logo);
        logoView.setFitHeight(100);
        logoView.setPreserveRatio(true);

        Label blueskyLabel = new Label("Bluesky Email");
        TextField blueskyField = new TextField();
        blueskyField.setPromptText("Enter Bluesky Email");
        blueskyField.getStyleClass().add("email-field");

        VBox blueskyBox = new VBox(5, blueskyLabel, blueskyField);
        blueskyBox.setAlignment(Pos.CENTER);
        blueskyBox.getStyleClass().add("input-box");

        Label mastodonLabel = new Label("Mastodon Email");
        TextField mastodonField = new TextField();
        mastodonField.setPromptText("Enter Mastodon Email");
        mastodonField.getStyleClass().add("email-field");

        VBox mastodonBox = new VBox(5, mastodonLabel, mastodonField);
        mastodonBox.setAlignment(Pos.CENTER);
        mastodonBox.getStyleClass().add("input-box");

        HBox emailsBox = new HBox(30, blueskyBox, mastodonBox);
        emailsBox.setAlignment(Pos.CENTER);

        Button loginBtn = new Button("LOG IN");
        loginBtn.getStyleClass().add("login-btn");
        loginBtn.setOnAction(e -> app.showHomePage(blueskyField.getText()));

        layout = new VBox(40, logoView, emailsBox, loginBtn);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(40));
        layout.getStyleClass().add("login-root"); 
    }

    public VBox getView() {
        return layout;
    }
}
