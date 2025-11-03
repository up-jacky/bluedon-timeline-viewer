package com.bluedon.view.ui.images;

import com.bluedon.view.ui.interfaces.ImageViewer;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Thumb implements ImageViewer {

    private String thumb;

    public Thumb(String uri) {
        thumb = uri;
    }

    @Override
    public ImageView getImage(double fitWidth, boolean ratio) {
        Image thumbImage = new Image(thumb);
        ImageView thumbView = new ImageView(thumbImage);
        thumbView.setFitWidth(fitWidth);
        thumbView.setPreserveRatio(ratio);
        
        return thumbView;
    }
}