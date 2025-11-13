package com.bluedon.view.ui.embed.bluesky;

import org.json.JSONObject;

import com.bluedon.view.ui.images.Avatar;

import javafx.scene.shape.Circle;

public class Author {
    private String displayName;
    private String handle;
    private String avatarUrl;

    public static Circle getDummyAvatar(int radius) {
        return new Avatar(null).getCircleImage(radius);
    }

    public Author(JSONObject rawJson) {
        displayName = rawJson.getString("displayName");
        handle = rawJson.getString("handle");
        avatarUrl = rawJson.getString("avatar");
    }

    public String getName() {
        return displayName;
    }

    public String getHandle() {
        return handle;
    }

    public Circle getAvatar(int radius) {
        return new Avatar(avatarUrl).getCircleImage(radius);
    }
}
