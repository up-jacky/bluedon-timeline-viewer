package com.bluedon.view.ui.buttons;

import com.bluedon.services.FetchTimeline;
import com.bluedon.services.Refresh;

import javafx.application.Platform;
import javafx.scene.control.Button;

/**
 * The RefreshButton class handles creating a refresh button 
 * using the {@link DefaultButton} as its base {@code Node}.
 */
public class RefreshButton {
    
	/**
	 * Creates a refresh button with transitions and effects.
	 * @return Button that refreshes the timeline.
	 */
    public static Button createButton() {
    	DefaultButton button = new DefaultButton("Refresh Posts");
        button.getStyleClass().add("refresh");
    	button.setOnAction(e-> {
			FetchTimeline instance = FetchTimeline.getInstance();
			if(instance != null && instance.isRunning()) Platform.runLater(() -> instance.cancel());
			Refresh.refreshHome();
			FetchTimeline.start();
		});
		button.playInitAnimation();
    	return button;
    }
}
