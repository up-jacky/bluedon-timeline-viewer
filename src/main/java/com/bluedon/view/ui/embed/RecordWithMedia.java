package com.bluedon.view.ui.embed;

import org.json.JSONObject;
import com.bluedon.view.ui.embed.EmbedMedia.EmbedMediaType;

import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

public class RecordWithMedia extends Embed {
    private EmbedMedia media;
    private EmbedMediaType mediaType;
    private EmbedRecord record;
    private boolean isMediaOnly = false;

    public RecordWithMedia(JSONObject rawJson) {
        init(rawJson);
    }

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

    public Pane getEmbed() {
        if(media == null && !isMediaOnly) return record.getEmbed();
        if(media != null && !isMediaOnly)return new VBox(16, media.getEmbed(), record.getEmbed());
        if(media != null && isMediaOnly) return media.getEmbed();
        return new Pane();
    }
}
