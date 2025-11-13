package com.bluedon.view.ui.embed.bluesky;

import java.time.Duration;
import java.time.LocalDateTime;

import org.json.JSONArray;
import org.json.JSONObject;

import com.bluedon.enums.EmbedType;
import com.bluedon.utils.Toast;
import com.bluedon.view.ui.cards.BlueskyPostCard;

import javafx.geometry.Insets;
import javafx.scene.text.Text;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

/**
 * Handles creating a container for an embedded record in a Bluesky post.
 */
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
    private boolean mediaOnly = false;

    /**
     * Creates a container for an embedded record in a Bluesky post.
     * @param rawJson Contains necessary information about the Bluesky post.
     */
    public EmbedRecord(JSONObject rawJson) {
        init(rawJson);
    }

    /**
     * Creates a container for an embedded record in a Bluesky post but only returns its media.
     * @param rawJson Contains necessary information about the Bluesky post.
     * @param mediaOnly If it should only return the media.
     */
    public EmbedRecord(JSONObject rawJson, boolean mediaOnly) {
        init(rawJson);
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

    /**
     * Creates a container for an embedded record for a Bluesky post.
     * @return {@code VBox} container containing the usual Bluesky post.
     */
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
        card.getStyleClass().add("post");

        Text content = new Text("Blocked");
        content.getStyleClass().add("content");
        content.getStyleClass().add("blocked");

        card.getChildren().add(content);
        return card;
    }

    private VBox viewDetachedRecord() {
        VBox card = new VBox(12);
        card.setPadding(new Insets(16));
        card.getStyleClass().add("post");

        Text content = new Text("Removed by author.");
        content.getStyleClass().add("content");
        content.getStyleClass().add("detached");

        card.getChildren().add(content);
        return card;
    }

    private VBox viewNotFoundRecord() {
        VBox card = new VBox(12);
        card.setPadding(new Insets(16));
        card.getStyleClass().add("post");

        Text content = new Text("Content Not Found");
        content.getStyleClass().add("content");
        content.getStyleClass().add("not-found");

        card.getChildren().add(content);
        return card;
    }
    
    private VBox viewRecord() {

        String[] rawMetrics = {
            likeCount + " likes",
            replyCount + " replies",
            repostCount + " reposts",
            quoteCount + " quotes"
        };

        Pane embedContainer = new Pane();
        if(embeds != null) {
            for(int i = 0; i < embeds.length(); i += 1) {
                Object rawEmbed = embeds.get(i);
                if(rawEmbed.getClass().getName() != JSONObject.class.getName()) {
                    System.err.println("[FATAL][Record][viewRecord] Embed records must be a JSONObject.");
                    Toast.fatal.showToast("Fatal Error: Embed records must be a JSONObject.");
                    return null;
                }
                JSONObject embed = embeds.getJSONObject(i);
                EmbedType eType = getEmbedType(embed.getString("$type"));
                
                switch(eType) {
                    case EXTERNAL: embedContainer =  new External(embed).getEmbed(); break;
                    case IMAGES: embedContainer = new Images(embed).getEmbed(); break;
                    case VIDEO: break;
                    case RECORD_WITH_MEDIA: 
                        if(mediaOnly) embedContainer = new RecordWithMedia(embed, mediaOnly).getEmbed(); 
                        else embedContainer = new RecordWithMedia(embed).getEmbed();
                        break;
                    default: break;
                }
            }
        }

        return BlueskyPostCard.createPostCard(url, null, author, createdAt, content, embedContainer, rawMetrics);
    }

    private static String uriToUrl(String rawUri, String handle) {
        return "https://bsky.app/profile/" + handle + "/post/" + rawUri.replaceAll(".*(?:/)","");
    }

    private static LocalDateTime parseDate(String rawDate) {
        return LocalDateTime.parse(rawDate.replaceAll("\\..*", "")).plus(Duration.ofHours(8));
    }
}
