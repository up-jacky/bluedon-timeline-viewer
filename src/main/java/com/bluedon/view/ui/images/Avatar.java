package com.bluedon.view.ui.images;

import com.bluedon.view.ui.interfaces.ImageViewer;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;

public class Avatar implements ImageViewer {

    private String avatarUri;

    public Avatar(String uri) {
        avatarUri = uri;
    }

    @Override
    public ImageView getImage(double fitHeight, boolean ratio) {
        Image avatar = new Image(avatarUri);
        ImageView avatarView = new ImageView(avatar);
        avatarView.setFitHeight(fitHeight);
        avatarView.setPreserveRatio(ratio);
        
        return avatarView;
    }

    public Circle getCircleImage(double radius) {
        Circle shape = new Circle(radius);
        Image avatar = new Image(avatarUri);
        ImagePattern avatarPattern = new ImagePattern(avatar);
        shape.setFill(avatarPattern);
        return shape;
    }

}
