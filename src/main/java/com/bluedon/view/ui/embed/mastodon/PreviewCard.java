package com.bluedon.view.ui.embed.mastodon;

import java.awt.Desktop;
import javafx.scene.shape.Rectangle;
import java.net.URI;

import org.json.JSONObject;

import com.bluedon.utils.Toast;
import com.bluedon.view.ui.images.Thumb;

import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

/**
 * Handles creating a container for external links in a Mastodon post.
 */
public class PreviewCard {
    private String url;
    private String imageUrl;
    private String title;
    private String description;

    /**
     * Creates a container for external links in a Mastodon post.
     * @param rawJson Contains necessary information for a preview.
     */
    public PreviewCard(JSONObject rawJson) {
        url = rawJson.getString("url");
        try { imageUrl = rawJson.getString("image"); } catch (Exception e) {imageUrl = "";}
        try { title = rawJson.getString("title"); } catch (Exception e) {title = "";}
        try { description = rawJson.getString("description"); } catch (Exception e) {description = "";}
    }

    /**
     * Creates a {@code Pane} container for an external link with its thumbnail image.
     * @return {@code VBox} if the preview contains an external image, else {@code HBox}
     * is returned with a dummy image.
     */
    public Pane getCard() {

        Text titleText = new Text(title);
        TextFlow titleFlow = new TextFlow(titleText);
        titleFlow.getStyleClass().add("title");
        
        Text descriptionText = new Text(description);
        TextFlow descriptionFlow = new TextFlow(descriptionText);
        descriptionFlow.getStyleClass().add("description");

        VBox info = new VBox(titleFlow, descriptionFlow);
        info.getStyleClass().add("info");

        Pane card;
        if(imageUrl != null && !imageUrl.trim().isEmpty()) {

            ImageView image = Thumb.getImage(imageUrl, 600);
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
                        Desktop.getDesktop().browse(URI.create(url));
                    }
                } catch (Exception error) {
                    System.err.println("[ERROR][PreviewCard][getCard] Failed to launch in browser! " + error.getMessage());
                    System.out.println("[INFO][PreviewCard][getCard] Open the link to browser instead: " + url);
                    Toast.error.showToast("Failed to launch in browser! Error: " + error.getMessage());
                }
            } else e.consume();
        });

        return card;
    }
}
