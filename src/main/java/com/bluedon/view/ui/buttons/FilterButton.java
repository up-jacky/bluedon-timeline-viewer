package com.bluedon.view.ui.buttons;

import com.bluedon.controllers.PageController;
import com.bluedon.enums.Social;
import com.bluedon.models.Home;
import com.bluedon.services.Refresh;

import javafx.scene.control.Button;

public class FilterButton {
    private static Home model = PageController.home.getModel();

    public static Button createButton(Social social) {
		DefaultButton button = new DefaultButton();
		button.getStyleClass().add("filter");
		if(model.isDisplayed(social)) {
			button.setText("Enabled");
			button.getStyleClass().add("active");
		} else {
			button.setText("Disabled");
		}
    	switch(social) {
    		case BLUESKY:
    			button.setOnAction(e -> {
    				model.setTimeline(Social.BLUESKY, !model.isDisplayed(Social.BLUESKY));
    				updateFilterButtonStyle(button, model.isDisplayed(Social.BLUESKY));
					Refresh.refreshPosts();
    			});
				break;
    		case MASTODON:
    			button.setOnAction(e -> {
    				model.setTimeline(Social.MASTODON, !model.isDisplayed(Social.MASTODON));
    				updateFilterButtonStyle(button, model.isDisplayed(Social.MASTODON));
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
