package com.bluedon.view.ui.images;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * The main logo for the application.
 */
public class BluedonLogo {
	
    /**
     * Returns an image of the Bluedon logo.
     * @return {@link ImageView} of the logo with its preserve ratio set to true.
     */
    public static ImageView getImage() {
        Image logo = new Image("/images/bluedon.png");
        ImageView logoView = new ImageView(logo);
        logoView.setPreserveRatio(true);
        
        return logoView;
    }
}
