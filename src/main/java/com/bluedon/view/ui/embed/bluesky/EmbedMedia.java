package com.bluedon.view.ui.embed.bluesky;

import javafx.scene.layout.Pane;

/**
 * Handles determining the type of embedded in a Bluesky post.
 * 
 * <p> EmbedMedia is a type of Embed that only consists of 
 * the following Embed: </p>
 * <ul>
 *  <li> {@link EmbedMediaType#EXTERNAL} </li>
 *  <li> {@link EmbedMediaType#IMAGES} </li>
 *  <li> {@link EmbedMediaType#VIDEO} </li>
 *  <li> {@link EmbedMediaType#NONE} </li>
 * </ul>
 */
public class EmbedMedia extends Embed {
    /**
     * Subclass of the {@code EmbedType} with only
     * consisting of the following types:
     * <ul>
     *  <li> {@link EmbedMediaType#EXTERNAL} </li>
     *  <li> {@link EmbedMediaType#IMAGES} </li>
     *  <li> {@link EmbedMediaType#VIDEO} </li>
     *  <li> {@link EmbedMediaType#NONE} </li>
     * </ul>
     */
    public enum EmbedMediaType {
        /**
         * An image display.
         */
        IMAGES,
        
        /**
         * A link to an external source with a thumb image attached.
         */
        EXTERNAL,
        /**
         * A video embed.
         */
        VIDEO, 
        
        /**
         * No embed.
         */
        NONE,
    }

    /**
     * Returns the type of the {@code EmbedMedia} based from its {@code rawType}.
     * @param rawType Type of the {@code EmbedMedia} that mostly follows the format of {@code app.bsky.embed.someType#subType}.
     * @return The type of the {@code EmbedMedia}.
     * 
     * @see EmbedMediaType#EXTERNAL
     * @see EmbedMediaType#IMAGES
     * @see EmbedMediaType#VIDEO
     * @see EmbedMediaType#NONE
     */
    public static EmbedMediaType getEmbedMediaType(String rawType) {
        String parsedType = rawType.replaceAll(".*(?:\\.)|#.*", "");
        switch (parsedType) {
            case "external": return EmbedMediaType.EXTERNAL;
            case "images": return EmbedMediaType.IMAGES;
            case "video": return EmbedMediaType.VIDEO;
            default: return EmbedMediaType.NONE;
        }
    }

    /**
     * Creates a container for its embed.
     * @return Container of the {@code EmbedMedia} of its type.
     */
    public Pane getEmbed() {
        return null;
    }
}
