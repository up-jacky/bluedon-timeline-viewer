package bluedon.timeline.viewer;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class HomePage {

    private VBox layout;

    public HomePage(Main app, String username) {
        Label welcome = new Label("Welcome, " + username + "!");
        Button logoutBtn = new Button("Logout");

        logoutBtn.setOnAction(e -> app.showLoginPage());

        layout = new VBox(10, welcome, logoutBtn);
    }

    public VBox getView() {
        return layout;
    }
}
