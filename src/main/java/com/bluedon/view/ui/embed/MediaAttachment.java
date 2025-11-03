package com.bluedon.view.ui.embed;

import org.json.JSONObject;

import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class MediaAttachment {
    private String alt;
    private String url;

    public MediaAttachment(JSONObject rawJson) {
        try{alt = rawJson.getString("description");} catch (Exception e) {alt = "";}
        url = rawJson.getString("url");
    }

    public ImageView getImage() {
        Image image = new Image(url);
        ImageView imageView = new ImageView(image);
        imageView.setPreserveRatio(true);
        Tooltip imageAlt = new Tooltip(alt);
        Tooltip.install(imageView, imageAlt);

        return imageView;
    }
}
