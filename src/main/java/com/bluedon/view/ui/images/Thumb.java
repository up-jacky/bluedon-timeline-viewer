package com.bluedon.view.ui.images;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Thumb {

    public static ImageView getImage(String thumb, boolean ratio, double fit, boolean isFitWidth) {
        Image thumbImage = new Image(thumb);
        ImageView imageView = new ImageView(thumbImage);
        if(isFitWidth) imageView.setFitWidth(fit);
        else imageView.setFitHeight(fit);
        imageView.setPreserveRatio(ratio);
    
        return imageView;
    }

    public static ImageView getImage(String thumb, double fitWidth) {
        return getImage(thumb, true, fitWidth, true);
    }

    public static ImageView getImage(String thumb, boolean ratio, double fitHeight) {
        return getImage(thumb, ratio, fitHeight, false);
    }

    public static ImageView getImage(String thumb, boolean preserveRatio) {
        Image thumbImage = new Image(thumb);
        ImageView imageView = new ImageView(thumbImage);
        imageView.setPreserveRatio(preserveRatio);

        return imageView;
    }
}