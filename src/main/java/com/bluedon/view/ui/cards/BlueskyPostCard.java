package com.bluedon.view.ui.cards;

import java.awt.Desktop;
import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.json.JSONObject;

import com.bluedon.utils.Toast;
import com.bluedon.view.ui.embed.Author;

import javafx.animation.PathTransition;
import javafx.scene.Cursor;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.util.Duration;

public class BlueskyPostCard {
    private static DateTimeFormatter currentYearFormatter = DateTimeFormatter.ofPattern("MMM dd");
    private static DateTimeFormatter generalFormatter = DateTimeFormatter.ofPattern("MMM dd yyyy");
    private static DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");

    public static VBox createPostCard(
        String url,
        JSONObject reason,
        Author author,
        LocalDateTime createdAt,
        String content,
        Pane embedContainer,
        String[] rawMetrics

    ) {
        VBox card = new VBox();
        card.getStyleClass().addAll("post", "bluesky");

        Text reasonName = null;
        TextFlow reasonFlow = null;
        if (reason != null) {
            String reasonNameString = "";
            try {
                reasonNameString = reason.getJSONObject("by").getString("displayName");
            } catch (Exception e) {
                System.out.println("[INFO][BlueskyPostCard][createPostCard] Reposter has no displayName.");
                System.out.println("[INFO][BlueskyPostCard][createPostCard] Using handle...");
                reasonNameString = reason.getJSONObject("by").getString("handle");
            }
            reasonName = new Text("Reposted by " + reasonNameString);
            reasonName.getStyleClass().add("repost");
            reasonFlow = new TextFlow(reasonName);
        }

        HBox authorProfile = Profile.createProfile(author.getAvatar(20), author.getName(), author.getHandle());

        String parsedCreatedAt = createdAt.format(generalFormatter) + " at " + createdAt.format(timeFormatter);
        if(createdAt.getYear() == LocalDateTime.now().getYear()) parsedCreatedAt = createdAt.format(currentYearFormatter) + " at " + createdAt.format(timeFormatter);

        Text createdAtText = new Text(parsedCreatedAt);
        createdAtText.getStyleClass().add("time-created");

        Text contentText = new Text(content);
        TextFlow contentFlow = new TextFlow(contentText);
        contentFlow.getStyleClass().add("content");

        HBox metrics = Metrics.createMetrics(rawMetrics);

        if (reason != null && reasonName != null && reasonFlow != null) card.getChildren().add(reasonFlow);
        card.getChildren().addAll(authorProfile, contentFlow, embedContainer, createdAtText, metrics);
        contentFlow.setOnMouseClicked(e -> {
            if(e.getButton() == MouseButton.PRIMARY) {
                try {
                    if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(java.awt.Desktop.Action.BROWSE)) {
                        Desktop.getDesktop().browse(URI.create(url));
                    }
                } catch (Exception error) {
                    System.err.println("[ERROR][BlueskyPostCard][createPostCard] Failed to launch in browser! " + error.getMessage());
                    System.out.println("[INFO][BlueskyPostCard][createPostCard] Open the link to browser instead: " + url);
                    Toast.error.showToast("Failed to launch in browser! Error: " + error.getMessage());
                }
            } else e.consume();
        });

        PathTransition transition = new PathTransition();
        transition.setNode(card);
        transition.setDuration(Duration.millis(300));

        card.boundsInLocalProperty().addListener((o, oldValue, newValue) -> {
            double startX = card.getBoundsInLocal().getCenterX();
            double startY = card.getBoundsInLocal().getCenterY();
            transition.setPath(new Line(startX, startY, startX, startY + 2));
        });

        card.setOnMouseEntered(e -> {
            card.setCursor(Cursor.HAND);
            transition.playFromStart();
        });

        card.setOnMouseExited(e -> {
            card.setCursor(Cursor.DEFAULT);
            transition.setRate(-1);
            transition.play();
        });

        return card;
    }
}
