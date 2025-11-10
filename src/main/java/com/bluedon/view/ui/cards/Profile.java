package com.bluedon.view.ui.cards;

import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

public class Profile {
    public static HBox createProfile(Circle avatar, String displayName, String username) {
        
        avatar.getStyleClass().add("avatar");

        Text displayNameText = new Text(displayName);
        displayNameText.getStyleClass().add("display-name");
        Text usernameText = new Text(username);
        usernameText.getStyleClass().add("username");

        VBox name = new VBox(displayNameText, usernameText);
        name.getStyleClass().add("name");
    
        HBox authorProfile = new HBox(avatar, name);
        authorProfile.getStyleClass().add("profile");

        return authorProfile;
    }
}
