package com.bluedon.view.ui.embed;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

public class MediaAttachments {
    private List<MediaAttachment> images = new ArrayList<>();
    private double fitWidth;
    private double spacing;
    
    public MediaAttachments(JSONArray rawJsonArray, double fitWidth) {
        this.fitWidth = fitWidth;
        for(int i = 0; i < rawJsonArray.length(); i += 1) {
            JSONObject rawJson = rawJsonArray.getJSONObject(i);
            if(rawJson.getString("type").compareTo("image") == 0) images.add(new MediaAttachment(rawJson));
        }
    }

    public Pane getImages() {
        System.out.println("[INFO][MediaAttachments][getImages] Getting images...");
        ImageView img1, img2, img3, img4;
        Pane pane;
        switch (images.size()) {
            case 1: 
                img1 = images.get(0).getImage();
                pane = new HBox(img1);
                img1.setFitWidth(fitWidth);
                return pane;
            case 2: 
                img1 = images.get(0).getImage();
                img2 = images.get(1).getImage();
                pane = new HBox(spacing, img1, img2);
                img1.setFitWidth((fitWidth-spacing)/2.0);
                img2.setFitWidth((fitWidth-spacing)/2.0);
                return pane;
            case 3:
                img1 = images.get(0).getImage();
                img2 = images.get(1).getImage();
                img3 = images.get(2).getImage();
                img1.setFitWidth((fitWidth-spacing)/2.0);
                img2.setFitWidth((fitWidth-spacing)/2.0);
                img3.setFitWidth((fitWidth-spacing)/2.0);
                pane = new VBox(spacing, img2, img3);
                return new HBox(spacing, img1, pane);
            case 4:
                img1 = images.get(0).getImage();
                img2 = images.get(1).getImage();
                img3 = images.get(2).getImage();
                img4 = images.get(3).getImage();
                img1.setFitWidth((fitWidth-spacing)/2.0);
                img2.setFitWidth((fitWidth-spacing)/2.0);
                img3.setFitWidth((fitWidth-spacing)/2.0);
                img4.setFitWidth((fitWidth-spacing)/2.0);
                Pane pane1 = new HBox(spacing, img1, img2);
                Pane pane2 = new HBox(spacing, img3, img4);
                return new VBox(spacing, pane1, pane2);
            default: return new Pane();
        }
    }
}
