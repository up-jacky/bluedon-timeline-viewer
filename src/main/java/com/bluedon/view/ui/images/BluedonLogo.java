package com.bluedon.view.ui.images;

import com.bluedon.view.ui.interfaces.ImageViewer;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class BluedonLogo implements ImageViewer {
	
    @Override
    public ImageView getImage(double fitHeight, boolean ratio) {
        Image logo = new Image(getClass().getResource("/images/bluedon.png").toExternalForm());
        ImageView logoView = new ImageView(logo);
        logoView.setFitHeight(fitHeight);
        logoView.setPreserveRatio(ratio);
        
        return logoView;
    }
}
