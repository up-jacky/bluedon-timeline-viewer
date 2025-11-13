package com.bluedon.enums;

/**
 * Enum for the types of embed that are used by the services.
 */
public enum EmbedType {
    /**
     * An image display.
     */
    IMAGES,
    
    /**
     * A link to an external source with a thumb image attached.
     */
    EXTERNAL,

    /**
     * An embed to another post in Bluesky.
     */
    RECORD,

    /**
     * An embed to another post or record with a media attached to it.
     */
    RECORD_WITH_MEDIA,

    /**
     * A video embed.
     */
    VIDEO,

    /**
     * No embed.
     */
    NONE,
}
