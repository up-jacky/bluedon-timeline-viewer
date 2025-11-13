package com.bluedon.view.ui.embed.bluesky;

import com.bluedon.enums.EmbedType;

public class Embed {
    public static EmbedType getEmbedType(String rawType) {
        String parsedType = rawType.replaceAll(".*(?:\\.)|#.*", "");
        switch (parsedType) {
            case "external": return EmbedType.EXTERNAL;
            case "images": return EmbedType.IMAGES;
            case "record": return EmbedType.RECORD;
            case "recordWithMedia": return EmbedType.RECORD_WITH_MEDIA;
            case "video": return EmbedType.VIDEO;
            default: return EmbedType.NONE;
        }
    }

    public static String getSubType(String rawType) {
        return rawType.replaceAll(".*(?:#)", "");
    }
}
