package com.bluedon.view.ui.cards;


import java.awt.Desktop;
import java.net.URI;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.bluedon.enums.EmbedType;
import com.bluedon.enums.Social;
import com.bluedon.utils.Toast;
import com.bluedon.view.ui.embed.Embed;
import com.bluedon.view.ui.embed.EmbedRecord;
import com.bluedon.view.ui.embed.External;
import com.bluedon.view.ui.embed.Images;
import com.bluedon.view.ui.embed.MediaAttachments;
import com.bluedon.view.ui.embed.PreviewCard;
import com.bluedon.view.ui.embed.QuotedStatus;
import com.bluedon.view.ui.embed.RecordWithMedia;
import com.bluedon.view.ui.images.Avatar;

import javafx.geometry.Insets;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class Post {
    private Pane cachedPost;
    private double cardWidth = 752;
    private double padding = 24;
    private double fitWidth = cardWidth - (2.0 * padding);

    public Social social;
    private String url;
    private String displayName;
    private String username;
    private String avatarUrl;
    private String content;
    private LocalDateTime createdAt;

    // Bluesky unique post elemnents
    private JSONObject embed;
    private JSONObject reason;
    private int likeCount;
    private int replyCount;
    private int repostCount;
    private int bookmarkCount;
    private EmbedType embedType;

    // Mastodon unique post elements
    private String boostedBy;
    private JSONObject quotedStatus;
    private JSONObject previewCard;
    private JSONArray mediaAttachments;
    private int favoritesCount;
    private int reblogsCount;
    private int quotesCount;

    private DateTimeFormatter currentYearFormatter = DateTimeFormatter.ofPattern("MMM dd");
    private DateTimeFormatter generalFormatter = DateTimeFormatter.ofPattern("MMM dd yyyy");
    private DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");

    /**
     * Post settings for Mastodon
     * @param social The service of the post.
     * @param post
    */
    public Post(Social social, JSONObject post) {
        this.social = social;
        if(post.getString("content") == null || post.getString("content").trim().isEmpty()) {
            JSONObject account = post.getJSONObject("account");
            try {
                boostedBy = account.getString("display_name");
            } catch (Exception e) {
                boostedBy = account.getString("username");
                System.out.println("[INFO][Post] User has no display_name, using username...");
            }
            post = post.getJSONObject("reblog");
        }
        try {
            quotedStatus = post.getJSONObject("quote").getJSONObject("quoted_status");
        } catch (Exception e) {
            System.out.println("[INFO][Post] User has no quoted status.");
            quotedStatus = null;
        }
        try {
            mediaAttachments = post.getJSONArray("media_attachments");
        } catch (Exception e) {
            System.out.println("[INFO][Post] User has no media_attachments.");
            mediaAttachments = null;
        }
        try {
            previewCard = post.getJSONObject("card");
        } catch (Exception e) {
            System.out.println("[INFO][Post] User has no card preview.");
            previewCard = null;
        }
        content = parseHtml(post.getString("content"));
        JSONObject account = post.getJSONObject("account");
        url = post.getString("url");
        displayName = account.getString("display_name");
        username = account.getString("username");
        avatarUrl = account.getString("avatar");
        createdAt = parseDate(post.getString("created_at"));
        favoritesCount = post.getInt("favourites_count");
        reblogsCount = post.getInt("reblogs_count");
        quotesCount = post.getInt("quotes_count");
    }

    /**
     * Post settings for Bluesky.
     * @param social
     * @param post
     * @param reason
     */
    public Post(Social social, JSONObject post, JSONObject reason) {
        this.social = social;
        JSONObject author = post.getJSONObject("author");
        displayName = author.getString("displayName");
        username = author.getString("handle");
        avatarUrl = author.getString("avatar");
        content = post.getJSONObject("record").getString("text");
        createdAt = parseDate(post.getString("indexedAt"));
        url = uriToUrl(post.getString("uri"), username);
        likeCount = post.getInt("likeCount");
        replyCount = post.getInt("replyCount");
        repostCount = post.getInt("repostCount");
        bookmarkCount = post.getInt("bookmarkCount");
        try{
            embed = post.getJSONObject("embed");
        } catch (Exception e) {
            embed = null;
            System.out.println("[INFO][Post] No embed found.");
        }
        embedType = embed == null? EmbedType.NONE: Embed.getEmbedType(embed.getString("$type"));
        this.reason = reason;
    }

    public Pane createPostCard() {
        if (cachedPost != null) return cachedPost;
        switch (social) {
            case BLUESKY: cachedPost = createBlueskyPostCard(); break;
            case MASTODON: cachedPost = createMastodonPostCard(); break;
            default: return null;
        }
        return cachedPost;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public VBox createBlueskyPostCard() {
        VBox card = new VBox(12);
        card.getStyleClass().add("post-card");
        card.setPadding(new Insets(padding));
        card.setPrefWidth(cardWidth);

        Text reasonName = null;
        TextFlow reasonFlow = null;
        if (reason != null) {
            String reasonNameString = "";
            try {
                reasonNameString = reason.getJSONObject("by").getString("displayName");
            } catch (Exception e) {
                System.out.println("[INFO][Post] Reposter has no displayName.");
                System.out.println("[INFO][Post] Using handle...");
                reasonNameString = reason.getJSONObject("by").getString("handle");
            }
            reasonName = new Text("Reposted by " + reasonNameString);
            reasonFlow = new TextFlow(reasonName);
        }

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

        Text contentText = new Text(content);
        contentText.getStyleClass().add("post-content");
        contentText.setWrappingWidth(fitWidth);
        TextFlow contentFlow = new TextFlow(contentText);
        contentFlow.setPrefWidth(fitWidth);
        
        Circle marker = new Circle(8);
        marker.getStyleClass().add("post-bluesky-marker");

        HBox authorProfile = new HBox(16, avatar, name);
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
        switch(embedType) {
            case EXTERNAL: embedContainer = new External(embed, fitWidth).getEmbed(); break;
            case IMAGES: embedContainer = new Images(embed, fitWidth).getEmbed(); break;
            case RECORD: embedContainer = new EmbedRecord(embed, fitWidth).getEmbed(); break;
            case RECORD_WITH_MEDIA: embedContainer = new RecordWithMedia(embed, fitWidth).getEmbed(); break;
            case VIDEO: break;
            default: break;
        }

        if (reason != null && reasonName != null && reasonFlow != null) card.getChildren().add(reasonFlow);
        card.getChildren().addAll(authorProfile, contentFlow, embedContainer, createdAtText, metrics);
        contentFlow.setOnMouseClicked(e -> {
            if(e.getButton() == MouseButton.PRIMARY) {
                try {
                    if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(java.awt.Desktop.Action.BROWSE)) {
                        Desktop.getDesktop().browse(URI.create(url));
                    }
                } catch (Exception error) {
                    System.err.println("[ERROR] Failed to launch in browser! " + error.getMessage());
                    System.out.println("[INFO][Post] Open the link to browser instead: " + url);
                    Toast.error.showToast("Failed to launch in browser! Error: " + error.getMessage());
                }
            } else e.consume();
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
        card.setPadding(new Insets(padding));
        card.setPrefWidth(cardWidth);
        
        Text boostedByText = null;
        TextFlow boostedByFlow = null;
        if (boostedBy != null && !boostedBy.trim().isEmpty()) {
            boostedByText = new Text(boostedBy + " boosted");
            boostedByFlow = new TextFlow(boostedByText);
        }

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

        Text contentText = new Text(content);
        contentText.getStyleClass().add("post-content");
        contentText.setWrappingWidth(fitWidth);
        TextFlow contentFlow = new TextFlow(contentText);
        contentFlow.setPrefWidth(fitWidth);

        Text reblogsCountText = new Text(reblogsCount + " boosts");
        reblogsCountText.getStyleClass().add("post-reblogs");

        Text quotesCountText = new Text(quotesCount + " quotes");
        quotesCountText.getStyleClass().add("post-quotes");

        Text favoritesCountText = new Text(favoritesCount + " favorites");
        favoritesCountText.getStyleClass().add("post-favorites");

        Pane quoteContainer = null;
        if (quotedStatus != null) quoteContainer = new QuotedStatus(quotedStatus, fitWidth).getQuotedStatus();

        Pane mediaContainer = null;
        if (mediaAttachments.length() > 0) mediaContainer = new MediaAttachments(mediaAttachments, fitWidth).getImages();

        Pane previewCardContainer = null;
        if (previewCard != null) previewCardContainer = new PreviewCard(previewCard, fitWidth).getCard();

        HBox authorProfile = new HBox(16, avatar, name);
        authorProfile.getStyleClass().add("post-author-container");

        HBox metrics = new HBox(8, reblogsCountText, quotesCountText, favoritesCountText);

        if (boostedBy != null && boostedByText != null && boostedByFlow != null) card.getChildren().add(boostedByFlow);
        card.getChildren().addAll(authorProfile, contentFlow);
        if(mediaContainer != null) card.getChildren().add(mediaContainer);
        if(previewCardContainer != null) card.getChildren().add(previewCardContainer);
        if(quoteContainer != null) card.getChildren().add(quoteContainer);
        card.getChildren().addAll(createdAtText, metrics);
        card.setOnMouseClicked(e -> {
            if(e.getButton() == MouseButton.PRIMARY) {
                try {
                    if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(java.awt.Desktop.Action.BROWSE)) {
                        Desktop.getDesktop().browse(URI.create(url));
                    }
                } catch (Exception error) {
                    System.err.println("[ERROR] Failed to launch in browser! " + error.getMessage());
                    System.out.println("[INFO][Post] Open the link to browser instead: " + url);
                }
            } else e.consume();
        });

        return card;
    }

    private static String parseHtml(String rawHtml) {
        rawHtml = rawHtml.replaceAll("&#39;", "\'");
        rawHtml = rawHtml.replaceAll("[^ -~]", "");
        String[] rawHtmlList = rawHtml.split("");
        List<String> parsed = new ArrayList<>();
        boolean isOpen = false;
        boolean isClosing = false;
        String invisible = "";
        String ellipsis = "";
        String prev = "";
        for (String c: rawHtmlList) {
            switch(c) {
                case "<":
                    isOpen = true;
                    break;
                case ">":
                    if(isOpen) isOpen = false;
                    else parsed.add(c);
                    break;
                case "/":
                    if(prev.compareTo("<") == 0) {
                        if(ellipsis == "ellipsis") parsed.add("...");
                        isClosing = true;
                        invisible = "";
                        ellipsis = "";
                    }
                    else if (isOpen) break;
                    else if (invisible.compareTo("invisible") == 0) break;
                    else parsed.add(c);
                    break;
                case "p":
                    if (isClosing) {
                        parsed.add("\n\n");
                        isClosing = false;
                    }
                    else if (isOpen) {
                        if(ellipsis.compareTo("ellipsis") != 0 && ellipsis.length() > 0) ellipsis += c;
                    }
                    else if (invisible.compareTo("invisible") == 0) break;
                    else parsed.add(c);
                    break;
                default:
                    if (isClosing) isClosing = false;
                    else if (isOpen) {
                        if(invisible.compareTo("invisible") != 0 && invisible.length() > 0) invisible += c;
                        if(ellipsis.compareTo("ellipsis") != 0 && ellipsis.length() > 0) ellipsis += c;
                        if(prev.compareTo("\"") == 0 && c.compareTo("i") == 0 && invisible.length() == 0) invisible += c;
                        if(prev.compareTo("\"") == 0 && c.compareTo("e") == 0 && ellipsis.length() == 0) ellipsis += c;
                    }
                    else if (invisible.compareTo("invisible") == 0) break;
                    else parsed.add(c);
                    break;
            }
            if(invisible.length() > 9) invisible = "";
            if(ellipsis.length() > 8) ellipsis = "";
            prev = c;
        }
        return String.join("", parsed).trim();
    }

    private static String uriToUrl(String rawUri, String handle) {
        return "https://bsky.app/profile/" + handle + "/post/" + rawUri.replaceAll(".*(?:/)","");
    }

    private static LocalDateTime parseDate(String rawDate) {
        return LocalDateTime.parse(rawDate.replaceAll("\\..*", "")).plus(Duration.ofHours(8));
    }
}
