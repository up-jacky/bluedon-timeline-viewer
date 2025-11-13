package com.bluedon.view.ui.embed.bluesky;

import com.bluedon.enums.EmbedType;

/**
 * Handles determining the type of embed in a Bluesky post.
 */
public class Embed {
    /**
     * Returns the type of the {@code Embed} based from its {@code rawType}.
     * @param rawType Type of the {@code Embed} that mostly follows the format of {@code app.bsky.embed.someType#subType}.
     * @return The type of the {@code Embed}.
     * 
     * @see EmbedType#EXTERNAL
     * @see EmbedType#IMAGES
     * @see EmbedType#RECORD
     * @see EmbedType#RECORD_WITH_MEDIA
     * @see EmbedType#VIDEO
     * @see EmbedType#NONE
     */
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

    /**
     * Returns the {@code subType} of the embed from its {@code rawType}. 
     * @param rawType Type of the embed that mostly follows the format of {@code app.bsky.embed.someType#subType}.
     * @return The {@code subType} of the embed.
     */
    public static String getSubType(String rawType) {
        return rawType.replaceAll(".*(?:#)", "");
    }
}
