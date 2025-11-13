package com.bluedon.view.ui.embed.bluesky;

import org.json.JSONObject;

import com.bluedon.view.ui.embed.bluesky.EmbedMedia.EmbedMediaType;

import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

/**
 * Handles creating a container for an embedded record with media in a Bluesky post.
 */
public class RecordWithMedia extends Embed {
    private EmbedMedia media;
    private EmbedMediaType mediaType;
    private EmbedRecord record;
    private boolean isMediaOnly = false;

    /**
     * Creates a container for an embbeded record with media in a Bluesky post.
     * @param rawJson Contains necessary information about a Bluesky post.
     */
    public RecordWithMedia(JSONObject rawJson) {
        init(rawJson);
    }

    /**
     * Creates a container for an embbeded record with media in a Bluesky post.
     * @param rawJson Contains necessary information about a Bluesky post.
     * @param isMediaOnly If it should only return the media.
     */
    public RecordWithMedia(JSONObject rawJson, boolean isMediaOnly) {
        this.isMediaOnly = isMediaOnly;
        init(rawJson);
    }

    private void init(JSONObject rawJson) {
        if(!isMediaOnly) record = new EmbedRecord(rawJson.getJSONObject("record"), true);
        mediaType = EmbedMedia.getEmbedMediaType((rawJson.getJSONObject("media")).getString("$type"));
        switch(mediaType) {
            case EXTERNAL: media = new External(rawJson.getJSONObject("media")); break;
            case IMAGES: media = new Images(rawJson.getJSONObject("media")); break;
            default: break;
        }
    }

    /**
     * Creates a container for an embedded record with media in a Bluesky post. 
     * @return {@code Pane} container for an embedded recrod with media.
     */
    public Pane getEmbed() {
        if(media == null && !isMediaOnly) return record.getEmbed();
        if(media != null && !isMediaOnly)return new VBox(16, media.getEmbed(), record.getEmbed());
        if(media != null && isMediaOnly) return media.getEmbed();
        return new Pane();
    }
}
