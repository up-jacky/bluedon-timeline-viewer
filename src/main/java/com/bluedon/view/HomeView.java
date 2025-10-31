package com.bluedon.view;

import com.bluedon.interfaces.PageView;
import com.bluedon.view.ui.images.BluedonLogo;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import javafx.scene.image.ImageView;

public class HomeView implements PageView {

    private BorderPane layout = new BorderPane();
    private Scene scene;

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

    private StackPane getProgressIndicator() {
        ProgressIndicator pi = new ProgressIndicator();
        pi.setProgress(-1.0);
        return new StackPane(pi);
    }

    private StackPane getNoContent() {
        Text text = new Text("No Timeline");
        return new StackPane(text);
    }

    public ScrollPane createPostsArea(VBox posts) {
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setPannable(true);
        if(posts == null) {
            StackPane sp = getProgressIndicator();
            sp.setPrefHeight(scrollPane.getHeight());
            scrollPane.setContent(sp);
        } else if(posts.getChildren().isEmpty()) {
            StackPane sp = getNoContent();
            sp.setPrefHeight(scrollPane.getHeight());
            scrollPane.setContent(sp);
        } else {
    	    posts.setPadding(new Insets(36));
            scrollPane.setContent(posts);
            scrollPane.setPrefViewportWidth(ScrollPane.USE_COMPUTED_SIZE);
            posts.setOnScroll(e -> {
                double deltaY = e.getDeltaY() * (0.5);
                scrollPane.setVvalue(scrollPane.getVvalue() - deltaY / scrollPane.getHeight());
                e.consume();
            });
        }
        return scrollPane;
    }
    
    public void updateLayout(VBox sidebar, ScrollPane scrollPane) {
    	if(sidebar != null) layout.setLeft(sidebar);
    	if(scrollPane != null) layout.setCenter(scrollPane);
    }

    @Override
    public void init() { 
        layout = new BorderPane();
        scene = new Scene(layout, 1000,600);
        scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
    }

    @Override
    public void displayPage(Stage primaryStage) {
        if(scene == null) init();
        primaryStage.setScene(scene);
        primaryStage.setTitle("Bluedon Home");
    }
}
