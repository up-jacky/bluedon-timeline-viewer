package com.bluedon.view.ui.images;

import com.bluedon.view.ui.interfaces.ImageViewer;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;

public class Avatar implements ImageViewer {

    private String avatarUri;

    public Avatar(String uri) {
        avatarUri = uri;
    }

    @Override
    public ImageView getImage(boolean ratio) {
        Image avatar = new Image(avatarUri);
        ImageView avatarView = new ImageView(avatar);
        avatarView.setPreserveRatio(ratio);
        
        return avatarView;
    }

    public Circle getCircleImage(double radius) {
        Circle shape = new Circle(radius);
        if(avatarUri != null && !avatarUri.trim().isEmpty()){
            Image avatar = new Image(avatarUri);
            ImagePattern avatarPattern = new ImagePattern(avatar);
            shape.setFill(avatarPattern);
            return shape;
        } else {
            Color color = Color.web("#279af1");
            shape.setFill(color);
            return shape;
        }
    }

}
