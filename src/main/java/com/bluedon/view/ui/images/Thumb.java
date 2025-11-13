package com.bluedon.view.ui.images;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * Handles creating an image for external preview.
 */
public class Thumb {

    /**
     * Creates an image from the input parameter {@code thumb}.
     * @param thumb The link to the image.
     * @param ratio Preserve image ratio?
     * @param fit Fit size of the application that depends on the {@code isFitWidth} parameter.
     * @param isFitWidth Determines whether the {@code fit} parameter is the image's {@code fitWidth} or {@code fitHeight}.
     * @return {@link ImageView} of the image.
     */
    public static ImageView getImage(String thumb, boolean ratio, double fit, boolean isFitWidth) {
        Image thumbImage = new Image(thumb);
        ImageView imageView = new ImageView(thumbImage);
        if(isFitWidth) imageView.setFitWidth(fit);
        else imageView.setFitHeight(fit);
        imageView.setPreserveRatio(ratio);
    
        return imageView;
    }

    /**
     * Creates an image from the input parameter {@code thumb}.
     * @param thumb The link to the image.
     * @param fitWidth Fit width of the image.
     * @return {@link ImageView} of the image.
     */
    public static ImageView getImage(String thumb, double fitWidth) {
        return getImage(thumb, true, fitWidth, true);
    }

    /**
     * Creates an image from the input parameter {@code thumb}.
     * @param thumb The link to the image.
     * @param ratio Preserve image ratio?
     * @param fitHeight Fit width of the image.
     * @return {@link ImageView} of the image.
     */
    public static ImageView getImage(String thumb, boolean ratio, double fitHeight) {
        return getImage(thumb, ratio, fitHeight, false);
    }

    /**
     * Creates an image from the input parameter {@code thumb}.
     * @param thumb The link to the image.
     * @param preserveRatio Preserve image ratio?
     * @return {@link ImageView} of the image.
     */
    public static ImageView getImage(String thumb, boolean preserveRatio) {
        Image thumbImage = new Image(thumb);
        ImageView imageView = new ImageView(thumbImage);
        imageView.setPreserveRatio(preserveRatio);

        return imageView;
    }
}