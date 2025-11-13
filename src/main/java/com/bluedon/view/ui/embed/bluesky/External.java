package com.bluedon.view.ui.embed.bluesky;

import java.awt.Desktop;
import java.net.URI;

import org.json.JSONObject;

import com.bluedon.utils.Toast;
import com.bluedon.view.ui.images.Thumb;

import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;

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
    public Pane getEmbed() {
        Text titleText = new Text(title);
        TextFlow titleFlow = new TextFlow(titleText);
        titleFlow.getStyleClass().add("title");
        
        Text descriptionText = new Text(description);
        TextFlow descriptionFlow = new TextFlow(descriptionText);
        descriptionFlow.getStyleClass().add("description");

        VBox info = new VBox(titleFlow, descriptionFlow);
        info.getStyleClass().add("info");

        Pane card;
        if(thumb != null && !thumb.trim().isEmpty()) {

            ImageView image = Thumb.getImage(thumb, 600);
            StackPane centeredImage = new StackPane(image);
            centeredImage.getStyleClass().add("thumb-img");
            
            card = new VBox(centeredImage, info);
            image.fitWidthProperty().bind(card.widthProperty().multiply(0.90));

        } else {
            Rectangle placeholder = new Rectangle(120, 120);
            placeholder.getStyleClass().add("placeholder");
            info.getStyleClass().add("no-img");
            card = new HBox(placeholder, info);
            info.prefWidthProperty().bind(card.widthProperty().subtract(120));
        }
        card.getStyleClass().add("external");
        card.setOnMouseClicked(e -> {
            if(e.getButton() == MouseButton.PRIMARY) {
                try {
                    if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(java.awt.Desktop.Action.BROWSE)) {
                        Desktop.getDesktop().browse(URI.create(uri));
                    }
                } catch (Exception error) {
                    System.err.println("[ERROR][PreviewCard][getCard] Failed to launch in browser! " + error.getMessage());
                    System.out.println("[INFO][PreviewCard][getCard] Open the link to browser instead: " + uri);
                    Toast.error.showToast("Failed to launch in browser! Error: " + error.getMessage());
                }
            } else e.consume();
        });

        return card;
    }
}
