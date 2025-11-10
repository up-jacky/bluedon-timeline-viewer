package com.bluedon.view.ui.cards;

import java.awt.Desktop;
import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.json.JSONArray;
import org.json.JSONObject;

import com.bluedon.utils.Toast;
import com.bluedon.view.ui.embed.MediaAttachments;
import com.bluedon.view.ui.embed.PreviewCard;
import com.bluedon.view.ui.embed.QuotedStatus;
import com.bluedon.view.ui.images.Avatar;

import javafx.animation.PathTransition;
import javafx.scene.Cursor;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.util.Duration;

public class MastodonPostCard {
    private static DateTimeFormatter currentYearFormatter = DateTimeFormatter.ofPattern("MMM dd");
    private static DateTimeFormatter generalFormatter = DateTimeFormatter.ofPattern("MMM dd yyyy");
    private static DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");
  
    public static VBox createPostCard(
        String url,
        String boostedBy,
        String avatarUrl,
        String displayName,
        String username,
        LocalDateTime createdAt,
        String content,
        JSONObject quotedStatus,
        JSONArray mediaAttachments,
        JSONObject previewCard,
        String[] rawMetrics
    ) {
        VBox card = new VBox();
        card.getStyleClass().addAll("post", "mastodon");
        
        Text boostedByText = null;
        TextFlow boostedByFlow = null;
        if (boostedBy != null && !boostedBy.trim().isEmpty()) {
            boostedByText = new Text(boostedBy + " boosted");
            boostedByText.getStyleClass().add("repost");
            boostedByFlow = new TextFlow(boostedByText);
        }
        
        Circle avatar = new Avatar(avatarUrl).getCircleImage(20);
        
        HBox authorProfile = Profile.createProfile(avatar, displayName, "@"+username+"@mastodon.social");
        
        String parsedCreatedAt = createdAt.format(generalFormatter) + " at " + createdAt.format(timeFormatter);
        if(createdAt.getYear() == LocalDateTime.now().getYear()) parsedCreatedAt = createdAt.format(currentYearFormatter) + " at " + createdAt.format(timeFormatter);
        Text createdAtText = new Text(parsedCreatedAt);
        createdAtText.getStyleClass().add("time-created");
        
        Text contentText = new Text(content);
        TextFlow contentFlow = new TextFlow(contentText);
        contentFlow.getStyleClass().add("content");

        HBox metrics = Metrics.createMetrics(rawMetrics);

        Pane quoteContainer = null;
        if (quotedStatus != null) quoteContainer = new QuotedStatus(quotedStatus).getQuotedStatus();

        Pane mediaContainer = null;
        if (mediaAttachments.length() > 0) mediaContainer = new MediaAttachments(mediaAttachments).getImages();

        Pane previewCardContainer = null;
        if (previewCard != null) previewCardContainer = new PreviewCard(previewCard).getCard();

        if (boostedBy != null && boostedByText != null && boostedByFlow != null) card.getChildren().add(boostedByFlow);
        card.getChildren().addAll(authorProfile, contentFlow);
        if(mediaContainer != null) card.getChildren().add(mediaContainer);
        if(previewCardContainer != null) card.getChildren().add(previewCardContainer);
        if(quoteContainer != null) card.getChildren().add(quoteContainer);
        card.getChildren().addAll(createdAtText, metrics);
        contentFlow.setOnMouseClicked(e -> {
            if(e.getButton() == MouseButton.PRIMARY) {
                try {
                    if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(java.awt.Desktop.Action.BROWSE)) {
                        Desktop.getDesktop().browse(URI.create(url));
                    }
                } catch (Exception error) {
                    System.err.println("[ERROR][MastodonPostCard][createPostCard] Failed to launch in browser! " + error.getMessage());
                    System.out.println("[INFO][MastodonPostCard][createPostCard] Open the link to browser instead: " + url);
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
