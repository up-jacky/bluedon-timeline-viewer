package com.bluedon.view.ui.images;

import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;

/**
 * Handles creating an image with a circle as its shape. 
 */
public class Avatar {

    private String avatarUri;

    /**
     * Creates an Avatar image with its link in the {@code uri} parameter.
     * @param uri The link to the image.
     */
    public Avatar(String uri) {
        avatarUri = uri;
    }

    /**
     * Creates a circle image with a {@code radius} given on the parameter.
     * @param radius The radius of the circle image.
     * @return {@code Circle} shape of type {@code Node} with its fill set to the image.
     */
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
