package com.bluedon.models;

import com.bluedon.enums.Social;

import javafx.scene.control.Button;

/**
 * The Login model holds the necessary buttons for the Login page.
 */
public class Login {
	
	// Bluesky UI Components
	private static Button blueskyButton;
	
	// Mastodon UI Components
	private static Button mastodonButton;
	
	/**
	 * @param social Type of service.
	 * @return Button with the type provided in the parameter social.
	 */
	public Button getButton(Social social) {
		switch(social) {
			case BLUESKY: return blueskyButton;
			case MASTODON: return mastodonButton;
			default: return null;
		}
	}
	
	/**
	 * Sets the button of the type of service provided in the parameter social
	 * to the button provided in the parameter button.
	 * @param social Type of service.
	 * @param button Button to set the one in the model.
	 */
	public void setButton(Social social, Button button) {
		switch(social) {
			case BLUESKY: blueskyButton = button;
				break;
			case MASTODON: mastodonButton = button;
				break;
		}
	}

}
