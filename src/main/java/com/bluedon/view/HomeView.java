package com.bluedon.view;

import com.bluedon.interfaces.PageView;
import com.bluedon.view.ui.images.BluedonLogo;

import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import javafx.scene.image.ImageView;

public class HomeView implements PageView {

    public VBox createSidebar(VBox blueskyComponents, VBox mastodonComponents, Button refreshButton) {
        VBox sidebar = new VBox(20);
        sidebar.getStyleClass().add("sidebar");
        sidebar.setPrefWidth(250);

        ImageView logo = new BluedonLogo().getImage(50, true);

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        sidebar.getChildren().addAll(
            logo,
            blueskyComponents,
            mastodonComponents,
            spacer,
            refreshButton
        );

        return sidebar;
    }

    public PasswordField createPasswordField(String prompText) {
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText(prompText);
        passwordField.getStyleClass().add("email-field");

        return passwordField;
    }


    public ScrollPane createPostsArea(VBox posts) {
    	posts.setPadding(new Insets(20));
        ScrollPane scrollPane = new ScrollPane(posts);
        scrollPane.setFitToWidth(true);
        return scrollPane;
    }
    
    public BorderPane createLayout(ScrollPane scrollPane, VBox sidebar) {
    	BorderPane layout = new BorderPane();
    	layout.setLeft(sidebar);
    	layout.setCenter(scrollPane);
    	return layout;
    }

    @Override
    public void displayPage(Stage primaryStage, Parent layout) {
        Scene homeScene = new Scene(layout, 1000, 600);
        homeScene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        primaryStage.setScene(homeScene);
        primaryStage.setTitle("Bluedon Home");
    }
}
