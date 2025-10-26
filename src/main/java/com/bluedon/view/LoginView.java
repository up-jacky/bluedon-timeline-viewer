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
import javafx.scene.Parent;
import javafx.scene.Scene;

public class LoginView implements PageView {

    private final static ImageView logo = new BluedonLogo().getImage(100, true);
    
    public VBox createContainer(double spacing, Node ... children) {
    	VBox container = new VBox(spacing, children);
    	container.setAlignment(Pos.CENTER);
    	container.getStyleClass().add("input-box");
    	return container;
    }
    
    public VBox createLayout(double spacing, Node ... children) {
        HBox accountsBox = new HBox(spacing, children);
        accountsBox.setAlignment(Pos.CENTER);

        VBox layout = new VBox(50, logo, accountsBox);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(40));
        layout.getStyleClass().add("login-root");
        return layout;
    }
    
    @Override
    public void displayPage(Stage primaryStage, Parent layout) {
        Scene loginScene = new Scene(layout, 1000, 600);
        loginScene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        primaryStage.setScene(loginScene);
        primaryStage.setTitle("Bluedon Login");
    }

}

