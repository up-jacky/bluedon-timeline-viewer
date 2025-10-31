package com.bluedon.view.ui.cards;


import java.awt.Desktop;
import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.json.JSONObject;

import com.bluedon.enums.EmbedType;
import com.bluedon.enums.Social;
import com.bluedon.view.ui.embed.Embed;
import com.bluedon.view.ui.embed.External;
import com.bluedon.view.ui.embed.Images;
import com.bluedon.view.ui.embed.RecordWithMedia;
import com.bluedon.view.ui.images.Avatar;

import javafx.geometry.Insets;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class Post {
    public Social social;
    private String url;
    private String displayName;
    private String username;
    private String avatarUrl;
    private String content;
    private LocalDateTime createdAt;

    // Bluesky unique post elemnents
    private JSONObject embed;
    private int likeCount;
    private int replyCount;
    private int repostCount;
    private int bookmarkCount;
    private EmbedType embedType;

    // Mastodon unique post elements
    private int favoritesCount;
    private int reblogsCount;
    private int quotesCount;

    private DateTimeFormatter currentYearFormatter = DateTimeFormatter.ofPattern("MMM dd");
    private DateTimeFormatter generalFormatter = DateTimeFormatter.ofPattern("MMM dd yyyy");
    private DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

    /**
     * Post settings for Mastodon
     * @param social The service of the post.
     * @param url URL to the post itself. (e.g. https://mastodon.social/...)
     * @param displayName The display name of the author of the post. (e.g. John Doe)
     * @param username The username of the author of the post. (e.g johndoe)
     * @param avatarUrl A link to the avatar of the user for display.
     * @param content The content of the post written in HTML/text.
     * @param createdAt Date and time of when the post was created.
     * @param favoritesCount Total number of favorites of the post.
     * @param reblogsCount Total number of boosts/reblogs of the post.
     * @param quotesCount Total number of quotes of the post.
    */
    public Post(
        Social social, String url, String displayName, String username, String avatarUrl, 
        String content, String createdAt, int favoritesCount, int reblogsCount, int quotesCount
    ) {
        this.social = social;
        this.url = url;
        this.displayName = displayName;
        this.username = username;
        this.avatarUrl = avatarUrl;
        this.content = content;
        this.createdAt = parseDate(createdAt);
        this.favoritesCount = favoritesCount;
        this.reblogsCount = reblogsCount;
        this.quotesCount = quotesCount;
    }

    /**
     * Post settings for Bluesky.
     * @param social
     * @param displayName
     * @param username
     * @param avatarUrl
     * @param content
     * @param uri
     * @param createdAt
     * @param replyCount
     * @param repostCount
     * @param likeCount
     * @param quoteCount
     * @param bookmarkCount
     * @param embed
     */
    public Post(
        Social social, String displayName, String username, String avatarUrl , String content,
        String uri, String createdAt, int replyCount, int repostCount, int likeCount, int quoteCount, int bookmarkCount,
        JSONObject embed
    ) {
        this.social = social;
        this.displayName = displayName;
        this.username = username;
        this.avatarUrl = avatarUrl;
        this.content = content;
        this.createdAt = parseDate(createdAt);
        url = uriToUrl(uri, username);
        this.likeCount = likeCount;
        this.replyCount = replyCount;
        this.repostCount = repostCount;
        this.bookmarkCount = bookmarkCount;
        this.embed = embed;
        embedType = embed == null? EmbedType.NONE: Embed.getEmbedType(embed.getString("$type"));
    }

    public Pane createPostCard() {
        switch (social) {
            case BLUESKY: return createBlueskyPostCard();
            case MASTODON: return createMastodonPostCard();
            default: return null;
        }
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public VBox createBlueskyPostCard() {
        VBox card = new VBox(12);
        card.getStyleClass().add("post-card");
        card.setPadding(new Insets(24));

        Circle avatar = new Avatar(avatarUrl).getCircleImage(16);
        avatar.getStyleClass().add("post-avatar");

        Text displayNamText = new Text(displayName);
        displayNamText.getStyleClass().add("post-display-name");
        Text usernameText = new Text(username);
        usernameText.getStyleClass().add("post-username");

        VBox name = new VBox(4, displayNamText, usernameText);
        name.setFillWidth(true);

        String parsedCreatedAt = createdAt.format(generalFormatter) + " at " + createdAt.format(timeFormatter);
        if(createdAt.getYear() == LocalDateTime.now().getYear()) parsedCreatedAt = createdAt.format(currentYearFormatter) + " at " + createdAt.format(timeFormatter);

        Text createdAtText = new Text(parsedCreatedAt);
        createdAtText.getStyleClass().add("post-time-created");

        Text contentText = new Text(parseHtml(content));
        contentText.getStyleClass().add("post-content");
        TextFlow contentFlow = new TextFlow(contentText);
        
        Circle marker = new Circle(8);
        marker.getStyleClass().add("post-bluesky-marker");

        HBox authorProfile = new HBox(8, avatar, name);
        authorProfile.getStyleClass().add("post-author-container");

        Text likeCountText = new Text(likeCount + " likes");
        likeCountText.getStyleClass().add("post-likes");
        
        Text replyCountText = new Text(replyCount + " replies");
        replyCountText.getStyleClass().add("post-replies");
        
        Text repostCountText = new Text(repostCount + " reposts");
        repostCountText.getStyleClass().add("post-reposts");

        Text bookmarkCountText = new Text(bookmarkCount + " saves");
        bookmarkCountText.getStyleClass().add("post-saves");

        HBox metrics = new HBox(8, likeCountText, replyCountText, repostCountText, bookmarkCountText);

        Pane embedContainer = new Pane();
        embedContainer.getStyleClass().add("post-embed");
        embedContainer.setPrefWidth(250.00);

        switch(embedType) {
            case EXTERNAL: embedContainer = new External(embed).getEmbed(); break;
            case IMAGES: embedContainer = new Images(embed).getEmbed(); break;
            case RECORD: embedContainer = new com.bluedon.view.ui.embed.Record(embed).getEmbed(); break;
            case RECORD_WITH_MEDIA: embedContainer = new RecordWithMedia(embed).getEmbed(); break;
            case VIDEO: break;
            default: break;
        }

        card.getChildren().addAll(authorProfile, contentFlow, embedContainer, createdAtText, metrics);
        contentFlow.setOnMouseClicked(e -> {
            try {
                if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(java.awt.Desktop.Action.BROWSE)) {
                    Desktop.getDesktop().browse(URI.create(url));
                }
            } catch (Exception error) {
                System.err.println("[ERROR] Failed to launch in browser! " + error.getMessage());
                System.out.println("[INFO] Open the link to browser instead: " + url);
            }
        });
        VBox.setVgrow(embedContainer, Priority.NEVER);

        return card;
    }
    
    /**
     * Generate a Node of the Post to have it in display.
     */
    public VBox createMastodonPostCard() {
        VBox card = new VBox(12);
        card.getStyleClass().add("post-card");
        card.setPadding(new Insets(24));
        card.setFillWidth(true);

        Circle avatar = new Avatar(avatarUrl).getCircleImage(16);
        avatar.getStyleClass().add("post-avatar");

        Text displayNameText = new Text(displayName);
        displayNameText.getStyleClass().add("post-display-name");
        Text usernameText = new Text(username);
        usernameText.getStyleClass().add("post-username");

        VBox name = new VBox(4, displayNameText, usernameText);

        String parsedCreatedAt = createdAt.format(generalFormatter) + " at " + createdAt.format(timeFormatter);
        if(createdAt.getYear() == LocalDateTime.now().getYear()) parsedCreatedAt = createdAt.format(currentYearFormatter) + " at " + createdAt.format(timeFormatter);

        Text createdAtText = new Text(parsedCreatedAt);
        createdAtText.getStyleClass().add("post-time-created");

        Text contentText = new Text(parseHtml(content));
        contentText.getStyleClass().add("post-content");
        TextFlow contentFlow = new TextFlow(contentText);

        Text reblogsCountText = new Text(reblogsCount + " boosts");
        reblogsCountText.getStyleClass().add("post-reblogs");

        Text quotesCountText = new Text(quotesCount + " quotes");
        quotesCountText.getStyleClass().add("post-quotes");

        Text favoritesCountText = new Text(favoritesCount + " favorites");
        favoritesCountText.getStyleClass().add("post-favorites");

        Circle marker = new Circle(8);
        marker.getStyleClass().add("post-mastodon-marker");

        HBox authorProfile = new HBox(8, avatar, name);
        authorProfile.getStyleClass().add("post-author-container");

        HBox metrics = new HBox(8, reblogsCountText, quotesCountText, favoritesCountText);

        card.getChildren().addAll(authorProfile, contentFlow, createdAtText, metrics);
        card.setOnMouseClicked(e -> {
            try {
                if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(java.awt.Desktop.Action.BROWSE)) {
                    Desktop.getDesktop().browse(URI.create(url));
                }
            } catch (Exception error) {
                System.err.println("[ERROR] Failed to launch in browser! " + error.getMessage());
                System.out.println("[INFO] Open the link to browser instead: " + url);
            }
                
        });

        return card;
    }

    private static String parseHtml(String rawHtml) {
        return rawHtml.replaceAll("<\\/?[^>]+(>|$)", "");
    }

    private static String uriToUrl(String rawUri, String handle) {
        return "https://bsky.app/profile/" + handle + "/post/" + rawUri.replaceAll(".*(?:/)","");
    }

    private static LocalDateTime parseDate(String rawDate) {
        return LocalDateTime.parse(rawDate.replaceAll("\\..*", ""));
    }
}
