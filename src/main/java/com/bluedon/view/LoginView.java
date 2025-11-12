package com.bluedon.view;

import com.bluedon.interfaces.PageView;
import com.bluedon.view.ui.images.BluedonLogo;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ProgressIndicator;

public class LoginView implements PageView {

    private static VBox layout;
    private static Scene scene;
    private final static ImageView logo = new BluedonLogo().getImage(true);
    
    public VBox createContainer(double spacing, Node ... children) {
    	VBox container = new VBox(spacing, children);
    	container.setAlignment(Pos.CENTER);
    	return container;
    }
    
    private VBox getProgressIndicator() {
        ProgressIndicator pi = new ProgressIndicator();
        pi.setProgress(-1.0);
        pi.setPrefSize(64, 64);
        pi.setMinSize(64, 64);
        return new VBox(pi);
    }

    public void updateLayout(double spacing, Node ... children) {
        if (layout == null) init();
        layout.getChildren().clear();
        VBox sp = getProgressIndicator();
        HBox accountsBox = new HBox(spacing, children);
        if(accountsBox.getChildren().isEmpty()) accountsBox = new HBox(spacing, sp);
        accountsBox.setAlignment(Pos.CENTER);
        logo.setFitHeight(154);
        layout.getChildren().addAll(logo, accountsBox);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(40));
    }

    @Override
    public void init() {
        layout = new VBox(64);
        layout.setId("root");
        scene = new Scene(layout, 1200,600);
        scene.getStylesheets().add(getClass().getResource("/css/login.css").toExternalForm());
    }
    
    @Override
    public void displayPage(Stage primaryStage) {
        if(scene == null) init();
        primaryStage.setScene(scene);
        primaryStage.setTitle("Bluedon Login");
    }

}

