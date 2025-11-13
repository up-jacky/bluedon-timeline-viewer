package com.bluedon.view.ui.cards;

import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

/**
 * Handles creating a profile container for the author.
 */
public class Profile {

    /**
     * Creates a profile container that contains the Author's avatar, display name, and username.
     * @param avatar {@link Circle} shaped image avatar of the author.
     * @param displayName Display name of the author.
     * @param username Username of the author.
     * @return Container that consists of the author's profile.
     */
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
