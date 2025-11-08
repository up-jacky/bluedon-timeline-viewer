package com.bluedon.view.ui.embed;

import java.awt.Desktop;
import java.net.URI;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.bluedon.enums.Social;
import com.bluedon.utils.Toast;
import com.bluedon.view.ui.images.Avatar;

import javafx.geometry.Insets;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class QuotedStatus {
    public Social social;
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
    private double cardWidth;
    private double padding = 24;
    private double fitWidth;

    private DateTimeFormatter currentYearFormatter = DateTimeFormatter.ofPattern("MMM dd");
    private DateTimeFormatter generalFormatter = DateTimeFormatter.ofPattern("MMM dd yyyy");
    private DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");

    public QuotedStatus(JSONObject rawJson, double fitWidth) {
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
        cardWidth = fitWidth;
        this.fitWidth = cardWidth - (2.0 * padding);
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

    public Pane getQuotedStatus() {
        VBox card = new VBox(12);
        card.getStyleClass().add("post-card");
        card.setPadding(new Insets(padding));
        card.setPrefWidth(cardWidth);

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

        Pane mediaContainer = null;
        if (mediaAttachments != null) mediaContainer = new MediaAttachments(mediaAttachments, fitWidth).getImages();

        Pane previewCardContainer = null;
        if (previewCard != null) previewCardContainer = new PreviewCard(previewCard, fitWidth).getCard();

        HBox authorProfile = new HBox(16, avatar, name);
        authorProfile.getStyleClass().add("post-author-container");

        HBox metrics = new HBox(8, reblogsCountText, quotesCountText, favoritesCountText);

        card.getChildren().addAll(authorProfile, contentFlow);
        if(mediaContainer != null) card.getChildren().add(mediaContainer);
        if(previewCardContainer != null) card.getChildren().add(previewCardContainer);
        card.getChildren().addAll(createdAtText, metrics);
        card.setOnMouseClicked(e -> {
            if(e.getButton() == MouseButton.PRIMARY) {
                try {
                    if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(java.awt.Desktop.Action.BROWSE)) {
                        Desktop.getDesktop().browse(URI.create(url));
                    }
                } catch (Exception error) {
                    System.err.println("[ERROR][QuotedStatus][getQuotedStatus] Failed to launch in browser! " + error.getMessage());
                    System.out.println("[INFO][QuotedStatus][getQuotedStatus] Open the link to browser instead: " + url);
                    Toast.error.showToast("Failed to launch in browser! Error: " + error.getMessage());
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
    
    private static LocalDateTime parseDate(String rawDate) {
        return LocalDateTime.parse(rawDate.replaceAll("\\..*", "")).plus(Duration.ofHours(8));
    }
}
