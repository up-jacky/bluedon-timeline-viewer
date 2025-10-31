package com.bluedon.view.ui.embed;

import java.awt.Desktop;
import java.net.URI;

import org.json.JSONObject;

import com.bluedon.view.ui.images.Thumb;

import javafx.scene.text.Text;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

public class External extends EmbedMedia {
    private String description;
    private String thumb;
    private String title;
    private String uri;

    public External(JSONObject rawJson) {
        String subType = getSubType(rawJson.getString("$type"));

        switch (subType) {
            case "view":
                rawJson = rawJson.getJSONObject("external");
                description = rawJson.getString("description");
                thumb = rawJson.getString("thumb");
                title = rawJson.getString("title");
                uri = rawJson.getString("uri");
                break;
            default:
                rawJson = rawJson.getJSONObject("external");
                description = rawJson.getString("description");
                thumb = null;
                title = rawJson.getString("title");
                uri = rawJson.getString("uri");
                break;
        }
    }

    public String getDescription() {
        return description;
    }
    
    public String getThumb() {
        return thumb;
    }
    
    public String getTitle() {
        return title;
    }
    
    public String getUri() {
        return uri;
    }

    @Override
    public VBox getEmbed() {
        VBox card = new VBox(16);
        card.getStyleClass().add("post-embed-external");
        card.setFillWidth(true);

        ImageView thumbImage = new Thumb(thumb).getImage(250, true);
        thumbImage.getStyleClass().add("post-embed-external-thumb");

        Text titleText = new Text(title);
        titleText.getStyleClass().add("post-embed-external-title");
        
        Text descriptionText = new Text(description);
        descriptionText.getStyleClass().add("post-embed-external-description");

        VBox info = new VBox(8, titleText, descriptionText);

        card.getChildren().addAll(thumbImage, info);
        card.setOnMouseClicked(e -> {
            try {
                if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(java.awt.Desktop.Action.BROWSE)) {
                    Desktop.getDesktop().browse(URI.create(uri));
                }
            } catch (Exception error) {
                System.err.println("[ERROR] Failed to launch in browser! " + error.getMessage());
                System.out.println("[INFO] Open the link to browser instead: " + uri);
            }
        });

        return card;
    }
}
