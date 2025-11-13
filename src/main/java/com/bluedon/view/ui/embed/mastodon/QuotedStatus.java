package com.bluedon.view.ui.embed.mastodon;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.bluedon.view.ui.cards.MastodonPostCard;
import javafx.scene.layout.Pane;

/**
 * Handles creating a container for a quoted status in a Mastodon post.
 */
public class QuotedStatus {
    private String url;
    private String displayName;
    private String username;
    private String avatarUrl;
    private String content;
    private LocalDateTime createdAt;
    private JSONObject previewCard;
    private JSONArray mediaAttachments;
    private int favoritesCount;
    private int reblogsCount;
    private int quotesCount;

    /**
     * Creates a container for a quoted status in a Mastodon post.
     * @param rawJson Contains necessary information about the quoted post that can also be find in a post.
     */
    public QuotedStatus(JSONObject rawJson) {
        if(rawJson.getString("content") == null || rawJson.getString("content").trim().isEmpty()) {
            rawJson = rawJson.getJSONObject("reblog");
        }
        try {
            mediaAttachments = rawJson.getJSONArray("media_attachments");
        } catch (Exception e) {
            System.out.println("[INFO][QuotedStatus][getQuotedStatus] User has no media_attachments.");
            mediaAttachments = null;
        }
        try {
            previewCard = rawJson.getJSONObject("card");
        } catch (Exception e) {
            System.out.println("[INFO][QuotedStatus][getQuotedStatus] User has no card.");
            previewCard = null;
        }
        content = parseHtml(rawJson.getString("content"));
        JSONObject account = rawJson.getJSONObject("account");
        url = rawJson.getString("url");
        displayName = account.getString("display_name");
        username = account.getString("username");
        avatarUrl = account.getString("avatar");
        createdAt = parseDate(rawJson.getString("created_at"));
        favoritesCount = rawJson.getInt("favourites_count");
        reblogsCount = rawJson.getInt("reblogs_count");
        quotesCount = rawJson.getInt("quotes_count");
    }

    /**
     * Creates a quoted status for a Mastodon post.
     * @return {@code Pane} container for a Mastodon status.
     */
    public Pane getQuotedStatus() {

        String[] rawMetrics = {
            reblogsCount + " boosts",
            quotesCount + " quotes",
            favoritesCount + " favorites"
        };

        return MastodonPostCard.createPostCard(url, null, avatarUrl, displayName, username, createdAt, content, null, mediaAttachments, previewCard, rawMetrics);
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
    
    private static LocalDateTime parseDate(String rawDate) {
        return LocalDateTime.parse(rawDate.replaceAll("\\..*", "")).plus(Duration.ofHours(8));
    }
}
