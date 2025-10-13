package com.bluedon.main.view;

import com.bluedon.main.interfaces.PageView;
import com.bluedon.main.view.ui.images.BluedonLogo;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.event.EventHandler;
import javafx.event.ActionEvent;

public class LoginView implements PageView {

    private final static ImageView logo = new BluedonLogo().getImage(100, true);
    
    public VBox createContainer(double spacing, Node ... children) {
    	VBox container = new VBox(spacing, children);
    	container.setAlignment(Pos.CENTER);
    	container.getStyleClass().add("input-box");
    	return container;
    }
    
    public Button createButton(String name, EventHandler<ActionEvent> e) {
    	Button button = new Button(name);
    	button.getStyleClass().add("login-btn");
    	button.setOnAction(e);
    	
    	return button;
    }
    
    public TextField createTextField(String promptText) {
    	TextField textField = new TextField();
    	textField.setPromptText(promptText);
    	textField.getStyleClass().add("email-field");
    	
    	return textField;
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
    public void displayPage(Stage primaryStage, VBox layout) {
        Scene loginScene = new Scene(layout, 1000, 600);
        loginScene.getStylesheets().add("file:src/com/bluedon/resources/styles.css");
        primaryStage.setScene(loginScene);
        primaryStage.setTitle("Bluedon Login");
    }

}

