package com.bluedon.view;

import com.bluedon.interfaces.PageView;
import com.bluedon.view.ui.images.BluedonLogo;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import javafx.scene.image.ImageView;

/**
 * Handles the display of the Home page.
 */
public class HomeView implements PageView {

    private BorderPane layout;
    private Scene scene;

    /**
     * @param blueskyComponents Components consisting of Avatar, Name, Handle, and FilterButton.
     * @param mastodonComponents Components consisting of Avatar, Name, Handle, and FilterButton.
     * @param refreshButton Refresh button generated from {@link com.bluedon.view.ui.buttons.RefreshButton#createButton()}
     * @return {@code VBox} container consisting of the components in the parameters.
     */
    public VBox createSidebar(VBox blueskyComponents, VBox mastodonComponents, Button refreshButton) {
        if(layout == null) init();
        VBox sidebar = new VBox();
        sidebar.getStyleClass().add("sidebar");
        sidebar.setFillWidth(true);
        sidebar.prefWidthProperty().bind(layout.widthProperty().multiply(0.2));

        ImageView logo = BluedonLogo.getImage();
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
        text.setFont(new Font("Consolas", 16));
        text.setFill(Color.WHITE);
        return new StackPane(text);
    }

    /**
     * @return {@code ScrollPane} that contains a loading screen of no indicator.
     */
    public ScrollPane createPostsArea() {
        if(layout == null) init();
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        StackPane sp = getProgressIndicator();
        scrollPane.setContent(sp);
        return scrollPane;
    }

    /**
     * @param errorMessage The error message to be displayed in the ScrollPane.
     * @return {@code ScrollPane} that contains an error message.
     */
    public ScrollPane createPostsArea(String errorMessage) {
        if(layout == null) init();
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        Text text = new Text(errorMessage);
        text.setFont(new Font("Consolas", 16));
        text.setFill(Color.RED);
        StackPane sp = new StackPane(text);
        scrollPane.setContent(sp);
        return scrollPane;
    }

    /**
     * @param posts The posts to be displayed in the ScrollPane.
     * @return {@code ScrollPane} that contains the timeline.
     */
    public ScrollPane createPostsArea(VBox posts) {
        if(layout == null) init();
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setPannable(true);
        scrollPane.setFitToWidth(true);
        if(posts.getChildren().isEmpty()) {
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
    
    /**
     * Updates the current layout of the homepage depending on the given parameter.
     * 
     * <p> Sets {@code sidebar} to the left side of the screen, and the {@code scrollPane}
     * to the center. </p>
     * 
     * <p> If a parameter is null, it simply does not update anything in the layout. </p>
     * @param sidebar The sidebar component of the layout.
     * @param scrollPane The scroll pane component of the layout.
     */
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
