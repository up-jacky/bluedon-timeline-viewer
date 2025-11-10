package com.bluedon.view.ui.buttons;

import com.bluedon.services.FetchTimeline;
import com.bluedon.services.Refresh;

import javafx.scene.control.Button;

public class RefreshButton {
    
    public static Button createRefreshButton() {
    	DefaultButton button = new DefaultButton("Refresh Posts");
        button.getStyleClass().add("refresh");
    	button.setOnAction(e-> {
			Refresh.refreshHome();
			FetchTimeline.start();
		});
		button.playInitAnimation();
    	return button;
    }
}
