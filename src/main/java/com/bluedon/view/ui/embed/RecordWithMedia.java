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
    private double cardWidth;

    public RecordWithMedia(JSONObject rawJson, double fitWidth) {
        cardWidth = fitWidth;
        init(rawJson);
    }

    public RecordWithMedia(JSONObject rawJson, double fitWidth, boolean isMediaOnly) {
        cardWidth = fitWidth;
        this.isMediaOnly = isMediaOnly;
        init(rawJson);
    }

    private void init(JSONObject rawJson) {
        if(!isMediaOnly) record = new EmbedRecord(rawJson.getJSONObject("record"), cardWidth, true);
        mediaType = EmbedMedia.getEmbedMediaType((rawJson.getJSONObject("media")).getString("$type"));
        switch(mediaType) {
            case EXTERNAL: media = new External(rawJson.getJSONObject("media"), cardWidth); break;
            case IMAGES: media = new Images(rawJson.getJSONObject("media"), cardWidth); break;
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
