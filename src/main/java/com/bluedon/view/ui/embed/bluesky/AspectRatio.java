package com.bluedon.view.ui.embed.bluesky;

import org.json.JSONObject;

/**
 * Handles creating an aspect ratio for an embed.
 */
public class AspectRatio {
    private int width;
    private int height;

    /**
     * Creates an aspect ratio for an embed.
     * @param rawJson Consists of {@code width} and {@code height}.
     */
    public AspectRatio(JSONObject rawJson) {
        width = rawJson.getInt("width");
        height = rawJson.getInt("height");
    }

    /**
     * @return Width of the embed.
     */
    public int getWidth() {
        return width;
    }
    
    /**
     * @return Height of the embed.
     */
    public int getHeight() {
        return height;
    }
}
