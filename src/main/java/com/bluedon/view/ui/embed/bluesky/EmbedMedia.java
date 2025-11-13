package com.bluedon.view.ui.embed.bluesky;

import javafx.scene.layout.Pane;

public class EmbedMedia extends Embed {
    public enum EmbedMediaType {
        EXTERNAL, IMAGES, VIDEO, NONE
    }

    public static EmbedMediaType getEmbedMediaType(String rawType) {
        String parsedType = rawType.replaceAll(".*(?:\\.)|#.*", "");
        switch (parsedType) {
            case "external": return EmbedMediaType.EXTERNAL;
            case "images": return EmbedMediaType.IMAGES;
            case "video": return EmbedMediaType.VIDEO;
            default: return EmbedMediaType.NONE;
        }
    }

    public Pane getEmbed() {
        return null;
    }
}
