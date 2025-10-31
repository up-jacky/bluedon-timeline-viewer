package com.bluedon.view.ui.embed;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

public class Images extends EmbedMedia {

    private List<EmbedImage> images = new ArrayList<>();

    public Images(JSONObject rawJson) {
        String subType = getSubType(rawJson.getString("$type"));
        JSONArray jsonArray = rawJson.getJSONArray("images");
        for(int i = 0; i < jsonArray.length(); i += 1) {
            JSONObject rawImage = jsonArray.getJSONObject(i);
            images.add(new EmbedImage(rawImage, subType));
        }
    }

    @Override
    public Pane getEmbed() {
        ImageView img1, img2, img3, img4;
        Pane pane;
        switch (images.size()) {
            case 1: 
                img1 = images.get(0).getEmbed();
                pane = new HBox(img1);
                img1.setFitWidth(360);
                return pane;
            case 2: 
                img1 = images.get(0).getEmbed();
                img2 = images.get(1).getEmbed();
                pane = new HBox(32, img1, img2);
                img1.setFitWidth(164);
                img2.setFitWidth(164);
                return pane;
            case 3:
                img1 = images.get(0).getEmbed();
                img2 = images.get(1).getEmbed();
                img3 = images.get(2).getEmbed();
                img1.setFitWidth(360);
                img2.setFitWidth(164);
                img3.setFitWidth(164);
                pane = new HBox(32, img2, img3);
                return new VBox(32, img1, pane);
            case 4:
                img1 = images.get(0).getEmbed();
                img2 = images.get(1).getEmbed();
                img3 = images.get(2).getEmbed();
                img4 = images.get(3).getEmbed();
                img1.setFitWidth(164);
                img2.setFitWidth(164);
                img3.setFitWidth(164);
                img4.setFitWidth(164);
                Pane pane1 = new HBox(32, img1, img2);
                Pane pane2 = new HBox(32, img3, img4);
                return new VBox(32, pane1, pane2);
            default: return new Pane();
        }
    }
}
