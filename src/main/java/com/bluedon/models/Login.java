package com.bluedon.models;

import com.bluedon.enums.Social;

import javafx.scene.control.Button;

public class Login {
	
	// Bluesky UI Components
	private static Button blueskyButton;
	
	// Mastodon UI Components
	private static Button mastodonButton;
	
	public Button getButton(Social social) {
		switch(social) {
			case BLUESKY: return blueskyButton;
			case MASTODON: return mastodonButton;
			default: return null;
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
