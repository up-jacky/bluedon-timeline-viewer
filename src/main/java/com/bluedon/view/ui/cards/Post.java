package com.bluedon.view.ui.cards;

import com.bluedon.enums.Social;

import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;

public class Post {
    public String username;
    public String message;
    public String timestamp;
    public Social type;

    public Post(String username, String message, String timestamp, Social type) {
        this.username = username;
        this.message = message;
        this.timestamp = timestamp;
        this.type = type;
    }
    
    public HBox createPostCard() {
        HBox card = new HBox(10);
        card.getStyleClass().add("post-card");

        Circle avatar = new Circle(20);
        avatar.getStyleClass().add("avatar");

        VBox content = new VBox(5);
        Label header = new Label(username + "    " + timestamp);
        header.getStyleClass().add("post-header");

        Label body = new Label(message);
        body.setWrapText(true);

        content.getChildren().addAll(header, body);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Circle marker = new Circle(8);
        if (type == Social.BLUESKY) {
            marker.getStyleClass().add("bluesky-marker");
        } else {
            marker.getStyleClass().add("mastodon-marker");
        }

        card.getChildren().addAll(avatar, content, spacer, marker);
        return card;
    }
}
