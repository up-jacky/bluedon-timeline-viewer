package com.bluedon.view.ui.embed.bluesky;

import org.json.JSONObject;

public class AspectRatio {
    private int width;
    private int height;

    public AspectRatio(JSONObject rawJson) {
        width = rawJson.getInt("width");
        height = rawJson.getInt("height");
    }

    public int getWidth() {
        return width;
    }
    
    public int getHeight() {
        return height;
    }
}
