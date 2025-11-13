package com.bluedon.view.ui.buttons;

import com.bluedon.controllers.PageController;
import com.bluedon.enums.Social;
import com.bluedon.models.Home;
import com.bluedon.services.Refresh;

import javafx.scene.control.Button;

/**
 * The FilterButton class handles creating a filter button 
 * using the {@link DefaultButton} as its base {@code Node}.
 */
public class FilterButton {
    private static Home model = PageController.home.getModel();


	/**
	 * Creates a filter button with transitions and effects for its service type given in the {@code social} parameter.
	 * @param social Type of service for the button to filter.
	 * @return Button that filters out content based on its {@code social} parameter.
	 */
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
