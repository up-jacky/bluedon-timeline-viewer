package com.bluedon.main.view.ui.cards;

import com.bluedon.main.enums.Social;

public class Post {
    String username;
    String message;
    String timestamp;
    Social type;

    public Post(String username, String message, String timestamp, Social type) {
        this.username = username;
        this.message = message;
        this.timestamp = timestamp;
        this.type = type;
    }
}
