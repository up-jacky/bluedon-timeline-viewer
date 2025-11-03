package com.bluedon.view.ui.buttons;

import com.bluedon.controllers.PageController;
import com.bluedon.enums.Social;
import com.bluedon.models.Home;
import com.bluedon.services.Refresh;

import javafx.scene.control.Button;

public class FilterButton {
    private static Home model = PageController.home.getModel();

    public static Button createButton(Social social) {
    	switch(social) {
    		case BLUESKY:
    			Button blueskyFilterButton = new Button("Enabled");
    			blueskyFilterButton.getStyleClass().add("active");
    			blueskyFilterButton.getStyleClass().add("filter");
    			blueskyFilterButton.setOnAction(e -> {
    				model.setTimeline(Social.BLUESKY, !model.getTimeline(Social.BLUESKY));
    				updateFilterButtonStyle(blueskyFilterButton, model.getTimeline(Social.BLUESKY));
					Refresh.refreshPosts();
    			});
    			return blueskyFilterButton;
    		case MASTODON:
    			Button mastodonFilterButton = new Button("Enabled");
    			mastodonFilterButton.getStyleClass().add("active");
    			mastodonFilterButton.getStyleClass().add("filter");
    			mastodonFilterButton.setOnAction(e -> {
    				model.setTimeline(Social.MASTODON, !model.getTimeline(Social.MASTODON));
    				updateFilterButtonStyle(mastodonFilterButton, model.getTimeline(Social.MASTODON));
					Refresh.refreshPosts();
    			});
    			return mastodonFilterButton;
    		default: return null;
    	}
    }
    
    private static void updateFilterButtonStyle(Button button, boolean active) {
        if (active) {
            if (!button.getStyleClass().contains("active")) {
                button.getStyleClass().add("active");
                button.setText("Enabled");
            }
        } else {
            button.getStyleClass().remove("active");
                button.setText("Disabled");
        }
    }
}
