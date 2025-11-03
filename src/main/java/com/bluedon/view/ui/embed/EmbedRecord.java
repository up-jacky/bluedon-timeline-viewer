package com.bluedon.view.ui.embed;

import java.awt.Desktop;
import java.net.URI;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.json.JSONArray;
import org.json.JSONObject;

import com.bluedon.enums.EmbedType;

import javafx.geometry.Insets;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;

public class EmbedRecord extends Embed {
    private Author author;
    private boolean blocked = false;
    private boolean detached = false;
    private boolean notFound = false;
    private String url;
    private JSONArray embeds;
    private String content;
    private LocalDateTime createdAt;
    private int likeCount;
    private int replyCount;
    private int quoteCount;
    private int repostCount;
    private DateTimeFormatter currentYearFormatter = DateTimeFormatter.ofPattern("MMM dd");
    private DateTimeFormatter generalFormatter = DateTimeFormatter.ofPattern("MMM dd yyyy");
    private DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");
    private boolean mediaOnly = false;
    private double cardWidth;
    private double padding = 24;
    private double fitWidth;

    public EmbedRecord(JSONObject rawJson, double fitWidth) {
        init(rawJson);
        cardWidth = fitWidth;
        this.fitWidth = cardWidth - (2.0*padding);
    }

    public EmbedRecord(JSONObject rawJson, double fitWidth, boolean mediaOnly) {
        init(rawJson);
        cardWidth = fitWidth;
        this.fitWidth = cardWidth - (2.0*padding);
        this.mediaOnly = mediaOnly;
    }

    private void init(JSONObject rawJson) {
        rawJson = rawJson.getJSONObject("record");
        String subType = rawJson.getString("$type").replaceAll(".*(?:#)", "");
        
        switch (subType) {
            case "viewBlocked":
                author = new Author(rawJson.getJSONObject("author"));
                blocked = true;
                url = uriToUrl(rawJson.getString("uri"), author.getHandle());
                break;
            case "viewDetached":
                author = new Author(rawJson.getJSONObject("author"));
                detached = true;
                url = uriToUrl(rawJson.getString("uri"), author.getHandle());
                break;
            case "viewNotFound":
                author = new Author(rawJson.getJSONObject("author"));
                notFound = true;
                url = uriToUrl(rawJson.getString("uri"), author.getHandle());
                break;
            case "viewRecord":
                author = new Author(rawJson.getJSONObject("author"));
                url = uriToUrl(rawJson.getString("uri"), author.getHandle());
                createdAt = parseDate(rawJson.getString("indexedAt"));
                likeCount = rawJson.getInt("likeCount");
                replyCount = rawJson.getInt("replyCount");
                quoteCount = rawJson.getInt("quoteCount");
                repostCount = rawJson.getInt("repostCount");
                content = rawJson.getJSONObject("value").getString("text");
                embeds = rawJson.get("embeds") == null? null: rawJson.getJSONArray("embeds");
                break;
            default:
                break;
        }
    }

    public VBox getEmbed() {
        if (blocked) {
            return viewBlockedRecord();
        } else if (detached) {
            return viewDetachedRecord();
        } else if (notFound) {
            return viewNotFoundRecord();
        } else {
            return viewRecord();
        }
    }

    private VBox viewBlockedRecord() {
        VBox card = new VBox(12);
        card.setPadding(new Insets(16));
        card.getStyleClass().add("post-card");

        Text content = new Text("Blocked");
        content.getStyleClass().add("post-content");
        content.getStyleClass().add("post-blocked");

        card.getChildren().add(content);
        return card;
    }

    private VBox viewDetachedRecord() {
        VBox card = new VBox(12);
        card.setPadding(new Insets(16));
        card.getStyleClass().add("post-card");

        Text content = new Text("Removed by author.");
        content.getStyleClass().add("post-content");
        content.getStyleClass().add("post-detached");

        card.getChildren().add(content);
        return card;
    }

    private VBox viewNotFoundRecord() {
        VBox card = new VBox(12);
        card.setPadding(new Insets(16));
        card.getStyleClass().add("post-card");

        Text content = new Text("Content Not Found");
        content.getStyleClass().add("post-content");
        content.getStyleClass().add("post-not-found");

        card.getChildren().add(content);
        return card;
    }
    
    private VBox viewRecord() {
        VBox card = new VBox(12);
        card.setPadding(new Insets(padding));
        card.getStyleClass().add("post-card");
        card.setPrefWidth(cardWidth);

        Circle avatar = author.getAvatar(16);
        avatar.getStyleClass().add("post-avatar");

        Text displayNamText = new Text(author.getName());
        displayNamText.getStyleClass().add("post-display-name");
        Text usernameText = new Text(author.getHandle());
        usernameText.getStyleClass().add("post-username");

        VBox name = new VBox(4, displayNamText, usernameText);

        String parsedCreatedAt = createdAt.format(generalFormatter) + " at " + createdAt.format(timeFormatter);
        if(createdAt.getYear() == LocalDateTime.now().getYear()) parsedCreatedAt = createdAt.format(currentYearFormatter) + " at " + createdAt.format(timeFormatter);

        Text createdAtText = new Text(parsedCreatedAt);
        createdAtText.getStyleClass().add("post-time-created");

        Text contentText = new Text(content);
        contentText.getStyleClass().add("post-content");
        contentText.setWrappingWidth(fitWidth);
        TextFlow contentFlow = new TextFlow(contentText);
    
        HBox authorProfile = new HBox(16, avatar, name);
        authorProfile.getStyleClass().add("post-author-container");

        Text likeCountText = new Text(likeCount + " likes");
        likeCountText.getStyleClass().add("post-likes");
        
        Text replyCountText = new Text(replyCount + " replies");
        replyCountText.getStyleClass().add("post-replies");
        
        Text repostCountText = new Text(repostCount + " reposts");
        repostCountText.getStyleClass().add("post-reposts");

        Text quoteCountText = new Text(quoteCount + " quotes");
        quoteCountText.getStyleClass().add("post-quotes");

        HBox metrics = new HBox(8, likeCountText, replyCountText, repostCountText);
        Pane embedContainer = new Pane();

        if(embeds != null) {
            for(int i = 0; i < embeds.length(); i += 1) {
                Object rawEmbed = embeds.get(i);
                if(rawEmbed.getClass().getName() != JSONObject.class.getName()) {
                    System.err.println("[FATAL][Record][viewRecord] Embed records must be a JSONObject.");
                    return null;
                }
                JSONObject embed = embeds.getJSONObject(i);
                EmbedType eType = getEmbedType(embed.getString("$type"));
                
                switch(eType) {
                    case EXTERNAL: embedContainer =  new External(embed, fitWidth).getEmbed(); break;
                    case IMAGES: embedContainer = new Images(embed, fitWidth).getEmbed(); break;
                    case VIDEO: break;
                    case RECORD_WITH_MEDIA: 
                        if(mediaOnly) embedContainer = new RecordWithMedia(embed, fitWidth, mediaOnly).getEmbed(); 
                        else embedContainer = new RecordWithMedia(embed, fitWidth).getEmbed();
                        break;
                    default: break;
                }
            }
        }

        card.getChildren().addAll(authorProfile, contentFlow, embedContainer, createdAtText, metrics);
        card.setOnMouseClicked(e -> {
            if(e.isPrimaryButtonDown()) {
                try {
                    if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(java.awt.Desktop.Action.BROWSE)) {
                        Desktop.getDesktop().browse(URI.create(url));
                    }
                } catch (Exception error) {
                    System.err.println("[ERROR][Record][viewRecord] Failed to launch in browser! " + error.getMessage());
                    System.out.println("[INFO][Record][viewRecord] Open the link to browser instead: " + url);
                }
            } else e.consume();
        });

        return card;
    }

    private static String uriToUrl(String rawUri, String handle) {
        return "https://bsky.app/profile/" + handle + "/post/" + rawUri.replaceAll(".*(?:/)","");
    }

    private static LocalDateTime parseDate(String rawDate) {
        return LocalDateTime.parse(rawDate.replaceAll("\\..*", "")).plus(Duration.ofHours(8));
    }
}
