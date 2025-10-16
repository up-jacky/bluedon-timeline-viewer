package com.bluedon.view;

import com.bluedon.interfaces.PageView;
import com.bluedon.view.ui.images.BluedonLogo;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import javafx.scene.image.ImageView;

public class HomeView implements PageView {

    public VBox createSidebar(VBox blueskyComponents, VBox mastodonComponents, Button refreshButton, Button logoutAllButton) {
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
            refreshButton,
            logoutAllButton
        );

        return sidebar;
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

    public Button createButton(String name, EventHandler<ActionEvent> e) {
    	Button button = new Button(name);
    	button.setOnAction(e);
    	
    	return button;
    }

    @Override
    public void displayPage(Stage primaryStage, Parent layout) {
        Scene homeScene = new Scene(layout, 1000, 600);
        homeScene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        primaryStage.setScene(homeScene);
        primaryStage.setTitle("Bluedon Home");
    }
}
