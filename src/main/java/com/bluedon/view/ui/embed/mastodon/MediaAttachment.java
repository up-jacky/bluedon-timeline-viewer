package com.bluedon.view.ui.embed.mastodon;

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

public class MediaAttachment {
    private String alt;
    private String url;

    public MediaAttachment(JSONObject rawJson) {
        try{alt = rawJson.getString("description");} catch (Exception e) {alt = "";}
        url = rawJson.getString("url");
    }
    
    public ImageView getImage() {
        Image image = new Image(url);
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
            imageView.fitWidthProperty().bind(root.widthProperty().multiply(0.75));

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
