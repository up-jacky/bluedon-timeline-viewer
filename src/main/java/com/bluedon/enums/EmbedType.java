package com.bluedon.enums;

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

    NONE,
}
