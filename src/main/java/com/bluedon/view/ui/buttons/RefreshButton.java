package com.bluedon.view.ui.buttons;

import com.bluedon.services.FetchTimeline;
import com.bluedon.services.Refresh;

import javafx.scene.control.Button;

public class RefreshButton {
    
    public static Button createRefreshButton() {
    	Button button = new Button("Refresh Posts");
        button.getStyleClass().add("refresh");
    	button.setOnAction(e-> {
			Refresh.refreshHome();
			FetchTimeline.start();
		});
    	return button;
    }
}
