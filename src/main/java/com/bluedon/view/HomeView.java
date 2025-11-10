package com.bluedon.view;

import com.bluedon.interfaces.PageView;
import com.bluedon.view.ui.images.BluedonLogo;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import javafx.scene.image.ImageView;

public class HomeView implements PageView {

    private BorderPane layout;
    private Scene scene;

    public VBox createSidebar(VBox blueskyComponents, VBox mastodonComponents, Button refreshButton) {
        if(layout == null) init();
        VBox sidebar = new VBox();
        sidebar.getStyleClass().add("sidebar");
        sidebar.setFillWidth(true);
        sidebar.prefWidthProperty().bind(layout.widthProperty().multiply(0.2));

        ImageView logo = new BluedonLogo().getImage(true);
        logo.setFitWidth(200);

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        blueskyComponents.setFillWidth(true);
        mastodonComponents.setFillWidth(true);

        sidebar.getChildren().addAll(
            logo,
            blueskyComponents,
            mastodonComponents,
            spacer,
            refreshButton
        );

        return sidebar;
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
        if(layout == null) init();
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setPannable(true);
        scrollPane.setFitToWidth(true);
        if(posts == null) {
            scrollPane.setFitToHeight(true);
            StackPane sp = getProgressIndicator();
            sp.setPrefHeight(scrollPane.getHeight());
            scrollPane.setContent(sp);
        } else if(posts.getChildren().isEmpty()) {
            scrollPane.setFitToHeight(true);
            StackPane sp = getNoContent();
            sp.setPrefHeight(scrollPane.getHeight());
            scrollPane.setContent(sp);
        } else {
            posts.getStylesheets().add("/css/post.css");
            posts.getStyleClass().add("posts");
            scrollPane.setContent(posts);
            posts.setOnScroll(e -> {
                double deltaY = e.getTextDeltaY() * (0.5);
                scrollPane.setVvalue(scrollPane.getVvalue() - deltaY / scrollPane.getHeight());
                e.consume();
            });
        }
        return scrollPane;
    }
    
    public void updateLayout(VBox sidebar, ScrollPane scrollPane) {
        if(layout == null) init();
    	if(sidebar != null) layout.setLeft(sidebar);
    	if(scrollPane != null) layout.setCenter(scrollPane);
    }

    @Override
    public void init() { 
        layout = new BorderPane();
        layout.setId("root");
        scene = new Scene(layout, 1200,600);
        scene.getStylesheets().add(getClass().getResource("/css/home.css").toExternalForm());
    }

    @Override
    public void displayPage(Stage primaryStage) {
        if(scene == null) init();
        primaryStage.setScene(scene);
        primaryStage.setTitle("Bluedon Home");
    }
}
