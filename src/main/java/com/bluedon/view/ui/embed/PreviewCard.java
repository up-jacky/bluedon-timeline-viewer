package com.bluedon.view.ui.embed;

import java.awt.Desktop;
import javafx.scene.shape.Rectangle;
import java.net.URI;

import org.json.JSONObject;

import com.bluedon.view.ui.images.Thumb;

import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class PreviewCard {
    private String url;
    private String imageUrl;
    private String title;
    private String description;
    private double fitWidth;

    public PreviewCard(JSONObject rawJson, double fitWidth) {
        this.fitWidth = fitWidth;
        url = rawJson.getString("url");
        try { imageUrl = rawJson.getString("image"); } catch (Exception e) {imageUrl = "";}
        try { title = rawJson.getString("title"); } catch (Exception e) {title = "";}
        try { description = rawJson.getString("description"); } catch (Exception e) {description = "";}
    }

    public Pane getCard() {
        
        ImageView thumbImage = null;
        Rectangle placeholder = null;
        if(imageUrl != null && !imageUrl.isEmpty()) {
            thumbImage = new Thumb(imageUrl).getImage(fitWidth, true);
            thumbImage.getStyleClass().add("post-embed-external-thumb");
        } else {
            placeholder = new Rectangle(64,64);
            placeholder.setFill(Color.web("#8645f6"));
        }

        Text titleText = new Text(title);
        titleText.getStyleClass().add("post-embed-external-title");
        
        Text descriptionText = new Text(description);
        descriptionText.getStyleClass().add("post-embed-external-description");
        TextFlow descriptionFlow = new TextFlow(descriptionText);
        
        if (thumbImage == null) {
            descriptionText.setWrappingWidth(fitWidth-80);
            descriptionFlow.setPrefWidth(fitWidth-80);
        } else {
            descriptionText.setWrappingWidth(fitWidth);
            descriptionFlow.setPrefWidth(fitWidth);
        }

        VBox info = new VBox(8, titleText, descriptionFlow);

        Pane card;
        if (thumbImage != null) card = new VBox(16, thumbImage, info);
        else card = new HBox(16, placeholder, info);
        card.getStyleClass().add("post-embed-external");
        card.setPrefWidth(fitWidth);

        card.setOnMouseClicked(e -> {
            try {
                if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(java.awt.Desktop.Action.BROWSE)) {
                    Desktop.getDesktop().browse(URI.create(url));
                }
            } catch (Exception error) {
                System.err.println("[ERROR][PreviewCard][getCard] Failed to launch in browser! " + error.getMessage());
                System.out.println("[INFO][PreviewCard][getCard] Open the link to browser instead: " + url);
            }
        });

        return card;
    }
}
