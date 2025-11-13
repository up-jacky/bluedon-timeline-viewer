package com.bluedon.view.ui.images;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class BluedonLogo {
	
    public ImageView getImage(boolean ratio) {
        Image logo = new Image(getClass().getResource("/images/bluedon.png").toExternalForm());
        ImageView logoView = new ImageView(logo);
        logoView.setPreserveRatio(ratio);
        
        return logoView;
    }
}
