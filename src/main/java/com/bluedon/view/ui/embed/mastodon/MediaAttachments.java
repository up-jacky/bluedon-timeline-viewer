package com.bluedon.view.ui.embed.mastodon;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class MediaAttachments {
    private List<MediaAttachment> images = new ArrayList<>();
    
    public MediaAttachments(JSONArray rawJsonArray) {
        for(int i = 0; i < rawJsonArray.length(); i += 1) {
            JSONObject rawJson = rawJsonArray.getJSONObject(i);
            if(rawJson.getString("type").compareTo("image") == 0) images.add(new MediaAttachment(rawJson));
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

    public Pane getImages() {
        System.out.println("[INFO][MediaAttachments][getImages] Getting images...");
        ImageView img1, img2, img3, img4;
        StackPane sp1, sp2, sp3, sp4;
        Pane pane;
        switch (images.size()) {
            case 1:
                img1 = images.get(0).getImage();
                sp1 = constructImage(img1, 1, 600);
                pane = new StackPane(sp1);
                pane.getStyleClass().addAll("wrapper","images-1");
                pane.widthProperty().addListener((o, oldValue, newValue) -> {
                    sp1.setPrefWidth(oldValue.doubleValue() * 0.95);
                });
                return pane;
            case 2:
                img1 = images.get(0).getImage();
                img2 = images.get(1).getImage();
                sp1 = constructImage(img1, 1, 250);
                sp2 = constructImage(img2, 2, 250);
                pane = new HBox(sp1, sp2);
                pane.getStyleClass().addAll("wrapper","images-2");
                pane.widthProperty().addListener((o, oldValue, newValue) -> {
                    sp1.setPrefWidth(oldValue.doubleValue() * 0.45);
                    sp2.setPrefWidth(oldValue.doubleValue() * 0.45);
                });
                return pane;
            case 3:
                img1 = images.get(0).getImage();
                img2 = images.get(1).getImage();
                img3 = images.get(2).getImage();
                sp1 = constructImage(img1, 1, 250);
                sp2 = constructImage(img2, 2, 250);
                sp3 = constructImage(img3, 3, 250);
                pane = new HBox(sp1, new VBox(sp2, sp3));
                pane.getStyleClass().addAll("wrapper","images-3");
                pane.widthProperty().addListener((o, oldValue, newValue) -> {
                    sp1.setPrefWidth(oldValue.doubleValue() * 0.45);
                    sp2.setPrefWidth(oldValue.doubleValue() * 0.45);
                    sp3.setPrefWidth(oldValue.doubleValue() * 0.45);
                });
                return pane;
            case 4:
                img1 = images.get(0).getImage();
                img2 = images.get(1).getImage();
                img3 = images.get(2).getImage();
                img4 = images.get(3).getImage();
                sp1 = constructImage(img1, 1, 250);
                sp2 = constructImage(img2, 2, 250);
                sp3 = constructImage(img3, 3, 250);
                sp4 = constructImage(img4, 4, 250);
                pane = new HBox(new VBox(sp1, sp3), new VBox(sp2, sp4));
                pane.getStyleClass().addAll("wrapper","images-4");
                pane.widthProperty().addListener((o, oldValue, newValue) -> {
                    sp1.setPrefWidth(oldValue.doubleValue() * 0.45);
                    sp2.setPrefWidth(oldValue.doubleValue() * 0.45);
                    sp3.setPrefWidth(oldValue.doubleValue() * 0.45);
                    sp4.setPrefWidth(oldValue.doubleValue() * 0.45);
                });
                return pane;
            default: return new Pane();
        }
    }
}
