package com.bluedon.models;

import com.bluedon.enums.Social;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class Login {
	
	// Bluesky UI Components
	private final static Label blueskyLabel = new Label("Bluesky Email");
	private TextField blueskyField;
	private Button blueskyButton;
	
	// Mastodon UI Components
	private final static Label mastodonLabel = new Label("Mastodon Email");
	private TextField mastodonField;
	private Button mastodonButton;
	
	public Label getLabel(Social social) {
		switch(social) {
		case BLUESKY: return blueskyLabel;
		case MASTODON: return mastodonLabel;
		default: return null;
		}
	}

	public TextField getField(Social social) {
		switch(social) {
		case BLUESKY: return blueskyField;
		case MASTODON: return mastodonField;
		default: return null;
		}
	}
	
	public Button getButton(Social social) {
		switch(social) {
			case BLUESKY: return blueskyButton;
			case MASTODON: return mastodonButton;
			default: return null;
		}
	}
	
	public void setField(Social social, TextField field) {
		switch(social) {
			case BLUESKY: blueskyField = field;
				break;
			case MASTODON: mastodonField = field;
				break;
		}
	}
	
	public void setButton(Social social, Button button) {
		switch(social) {
			case BLUESKY: blueskyButton = button;
				break;
			case MASTODON: mastodonButton = button;
				break;
		}
	}
	
}
