package com.bluedon.view.ui.embed.bluesky;

import org.json.JSONObject;

import com.bluedon.view.ui.images.Avatar;

import javafx.scene.shape.Circle;

/**
 * Handles creating an Author information.
 */
public class Author {
    private String displayName;
    private String handle;
    private String avatarUrl;

    /**
     * Creates a dummy circle image avatar of the author.
     * @param radius Size of the circle in terms of radius.
     * @return {@link Circle} shape of the image.
     */
    public static Circle getDummyAvatar(int radius) {
        return new Avatar(null).getCircleImage(radius);
    }

    /**
     * Creates an Author information.
     * @param rawJson Information about the author, consisting of {@code displayName}, {@code handle}, and {@avatar}
     */
    public Author(JSONObject rawJson) {
        displayName = rawJson.getString("displayName");
        handle = rawJson.getString("handle");
        avatarUrl = rawJson.getString("avatar");
    }

    /**
     * @return Name of the author
     */
    public String getName() {
        return displayName;
    }

    /**
     * @return Handle of the author
     */
    public String getHandle() {
        return handle;
    }

    /**
     * Creates a circle image avatar of the author.
     * @param radius Size of the circle in terms of radius.
     * @return {@link Circle} shape of the image.
     */
    public Circle getAvatar(int radius) {
        return new Avatar(avatarUrl).getCircleImage(radius);
    }
}
