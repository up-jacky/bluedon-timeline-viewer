package com.bluedon.view.ui.embed.bluesky;

import org.json.JSONObject;

import com.bluedon.controllers.PageController;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

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
        Image image = new Image(thumb);
        ImageView imageView = new ImageView(image);
        Tooltip imageAlt = new Tooltip(alt);
        Tooltip.install(imageView, imageAlt);

        imageView.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.PRIMARY) viewImage(image);
        });

        return imageView;
    }

    private void viewImage(Image image) {
        Platform.runLater(() -> {
            Stage stage = new Stage();
            Stage primaryStage = PageController.getStage();
            stage.initOwner(PageController.getStage());
            stage.setResizable(false);
            stage.initStyle(StageStyle.TRANSPARENT);
            stage.setWidth(primaryStage.getWidth());
            stage.setHeight(primaryStage.getHeight());
            stage.setX(primaryStage.getX());
            stage.setY(primaryStage.getY());

            ImageView imageView = new ImageView(image);
            StackPane root = new StackPane(imageView);
            imageView.setPreserveRatio(true);
            imageView.fitHeightProperty().bind(root.heightProperty().multiply(0.95));

            root.setBackground(new Background(
                new BackgroundFill(
                    Color.web("#000000a0"), null, null
                )
            ));

            Scene scene = new Scene(root);
            scene.setFill(Color.TRANSPARENT);

            stage.setScene(scene);
            stage.show();

            scene.setOnMouseClicked(e -> {
                if (e.getButton() == MouseButton.PRIMARY) stage.close();
            });
        });
    }
}
