package com.bluedon.view.ui.buttons;

import com.bluedon.controllers.PageController;
import com.bluedon.enums.Social;
import com.bluedon.models.Home;
import com.bluedon.services.Refresh;

import javafx.scene.control.Button;

public class FilterButton {
    private static Home model = PageController.home.getModel();

    public static Button createButton(Social social) {
		DefaultButton button = new DefaultButton("Enabled");
		button.getStyleClass().addAll("active", "filter");
    	switch(social) {
    		case BLUESKY:
    			button.setOnAction(e -> {
    				model.setTimeline(Social.BLUESKY, !model.getTimeline(Social.BLUESKY));
    				updateFilterButtonStyle(button, model.getTimeline(Social.BLUESKY));
					Refresh.refreshPosts();
    			});
				break;
    		case MASTODON:
    			button.setOnAction(e -> {
    				model.setTimeline(Social.MASTODON, !model.getTimeline(Social.MASTODON));
    				updateFilterButtonStyle(button, model.getTimeline(Social.MASTODON));
					Refresh.refreshPosts();
    			});
				break;
    		default: return null;
    	}
		button.playInitAnimation();
		return button;
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
