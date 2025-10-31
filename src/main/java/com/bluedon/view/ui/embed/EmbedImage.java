package com.bluedon.view.ui.embed;

import org.json.JSONObject;

import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class EmbedImage {
    private String alt;
    private AspectRatio aspectRatio;
    private String fullsize;
    private String thumb;

    public EmbedImage(JSONObject rawJson, String subType) {

        switch(subType) {
            case "view":
                alt = rawJson.getString("alt");
                aspectRatio = new AspectRatio(rawJson.getJSONObject("aspectRatio"));
                fullsize = rawJson.getString("fullsize");
                thumb = rawJson.getString("thumb");
                break;
            default:
                alt = rawJson.getString("alt");
                aspectRatio = new AspectRatio(rawJson.getJSONObject("aspectRatio"));
                fullsize = rawJson.getString("fullsize");
                break;
        }
    }

    public String getAlt() {
        return alt;
    }

    public AspectRatio getAspectRatio() {
        return aspectRatio;
    }

    public String getFullsize() {
        return fullsize;
    }

    public String getThumb() {
        return thumb;
    }

    public ImageView getEmbed() {
        Image image = new Image(fullsize);
        ImageView imageView = new ImageView(image);
        imageView.setPreserveRatio(true);
        Tooltip imageAlt = new Tooltip(alt);
        Tooltip.install(imageView, imageAlt);

        return imageView;
    }
}
