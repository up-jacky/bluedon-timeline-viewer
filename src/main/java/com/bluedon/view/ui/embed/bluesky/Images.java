package com.bluedon.view.ui.embed.bluesky;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
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

    private StackPane constructImage(ImageView img, int count, double fitWidth) {
        img.setFitWidth(fitWidth);
        img.setPreserveRatio(true);
        img.getStyleClass().add(String.format("img-%d", count));
        StackPane sp = new StackPane(img);
        sp.getStyleClass().add(String.format("sp-%d", count));
        sp.widthProperty().addListener((o, oldValue, newValue) -> {
            img.setFitWidth(newValue.doubleValue() * 0.95);
        });

        return sp;
    }

    @Override
    public Pane getEmbed() {
        ImageView img1, img2, img3, img4;
        StackPane sp1, sp2, sp3, sp4;
        Pane pane;
        switch (images.size()) {
            case 1:
                img1 = images.get(0).getEmbed();
                sp1 = constructImage(img1, 1, 300);
                pane = new StackPane(sp1);
                pane.getStyleClass().addAll("wrapper","images-1");
                return pane;
            case 2:
                img1 = images.get(0).getEmbed();
                img2 = images.get(1).getEmbed();
                sp1 = constructImage(img1, 1, 250);
                sp2 = constructImage(img2, 2, 250);
                pane = new HBox(sp1, sp2);
                HBox.setHgrow(sp1, Priority.ALWAYS);
                HBox.setHgrow(sp2, Priority.ALWAYS);
                pane.getStyleClass().addAll("wrapper","images-2");
                return pane;
            case 3:
                img1 = images.get(0).getEmbed();
                img2 = images.get(1).getEmbed();
                img3 = images.get(2).getEmbed();
                sp1 = constructImage(img1, 1, 250);
                sp2 = constructImage(img2, 2, 250);
                sp3 = constructImage(img3, 3, 250);
                pane = new HBox(sp1, new VBox(sp2, sp3));
                HBox.setHgrow(sp1, Priority.ALWAYS);
                HBox.setHgrow(pane.getChildren().get(1), Priority.ALWAYS);
                pane.getStyleClass().addAll("wrapper","images-3");
                return pane;
            case 4:
                img1 = images.get(0).getEmbed();
                img2 = images.get(1).getEmbed();
                img3 = images.get(2).getEmbed();
                img4 = images.get(3).getEmbed();
                sp1 = constructImage(img1, 1, 250);
                sp2 = constructImage(img2, 2, 250);
                sp3 = constructImage(img3, 3, 250);
                sp4 = constructImage(img4, 4, 250);
                pane = new HBox(new VBox(sp1, sp3), new VBox(sp2, sp4));
                HBox.setHgrow(pane.getChildren().get(0), Priority.ALWAYS);
                HBox.setHgrow(pane.getChildren().get(1), Priority.ALWAYS);
                pane.getStyleClass().addAll("wrapper","images-4");
                return pane;
            default: return new Pane();
        }
    }
}
