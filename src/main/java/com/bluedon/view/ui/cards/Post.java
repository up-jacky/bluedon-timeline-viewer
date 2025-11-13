package com.bluedon.view.ui.cards;


import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.bluedon.enums.EmbedType;
import com.bluedon.enums.Social;
import com.bluedon.view.ui.embed.bluesky.Author;
import com.bluedon.view.ui.embed.bluesky.Embed;
import com.bluedon.view.ui.embed.bluesky.EmbedRecord;
import com.bluedon.view.ui.embed.bluesky.External;
import com.bluedon.view.ui.embed.bluesky.Images;
import com.bluedon.view.ui.embed.bluesky.RecordWithMedia;

import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

/**
 * Handles creating a post card for both the Bluesky and Mastodon post.
 */
public class Post {
    private Pane cachedPost;

    /**
     * Type of service.
     */
    public final Social social;
    private String url;
    private String displayName;
    private String username;
    private String avatarUrl;
    private String content;
    private LocalDateTime createdAt;

    // Bluesky unique post elemnents
    private JSONObject author;
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

    /**
     * Initializes the information of the posts needed to create a post for Mastodon.
     * @param social The service of the post.
     * @param post Information about the post in {@link JSONObject} format.
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
     * Initializes the information of the posts needed to create a post for Bluesky.
     * @param social The service of the post.
     * @param post Information about the post in {@link JSONObject} format.
     * @param reason Information about the author that reposted the {@code post}.
     */
    public Post(Social social, JSONObject post, JSONObject reason) {
        this.social = social;
        author = post.getJSONObject("author");
        content = post.getJSONObject("record").getString("text");
        createdAt = parseDate(post.getString("indexedAt"));
        url = uriToUrl(post.getString("uri"), author.getString("handle"));
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

    /**
     * Creates a post card if there is no initial post that has been created.
     * @return {@link Pane} that contains the post.
     */
    public Pane createPostCard() {
        if (cachedPost != null) return cachedPost;
        switch (social) {
            case BLUESKY: cachedPost = createBlueskyPostCard(); break;
            case MASTODON: cachedPost = createMastodonPostCard(); break;
            default: return null;
        }
        return cachedPost;
    }

    /**
     * @return Time created of the post.
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    private VBox createBlueskyPostCard() {

        Author authorProfile = new Author(author);

        String[] rawMetrics = {
            likeCount + " likes",
            replyCount + " replies",
            repostCount + " reposts",
            bookmarkCount + " saves"
        };

        Pane embedContainer = new Pane();
        switch(embedType) {
            case EXTERNAL: embedContainer = new External(embed).getEmbed(); break;
            case IMAGES: embedContainer = new Images(embed).getEmbed(); break;
            case RECORD: embedContainer = new EmbedRecord(embed).getEmbed(); break;
            case RECORD_WITH_MEDIA: embedContainer = new RecordWithMedia(embed).getEmbed(); break;
            case VIDEO: break;
            default: break;
        }

        return BlueskyPostCard.createPostCard(url, reason, authorProfile, createdAt, content, embedContainer, rawMetrics);
    }
    
    private VBox createMastodonPostCard() {

        String[] rawMetrics = {
            reblogsCount + " boosts",
            quotesCount + " quotes",
            favoritesCount + " favorites"
        };

        return MastodonPostCard.createPostCard(url, boostedBy, avatarUrl, displayName, username, createdAt, content, quotedStatus, mediaAttachments, previewCard, rawMetrics);
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
