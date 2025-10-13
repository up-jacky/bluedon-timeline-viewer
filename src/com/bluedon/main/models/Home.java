package com.bluedon.main.models;

import com.bluedon.main.enums.Social;

public class Home {
    private boolean displayBluesky = false;
    private boolean displayMastodon = false;
    
    private boolean blueskyLoggedIn = false;
    private boolean mastodonLoggedIn = false;
    
    private String blueskyUsername;
    private String mastodonUsername;
    
    public void setUsername(Social social, String username) {
    	switch(social) {
    		case BLUESKY: blueskyUsername = username;
    			break;
    		case MASTODON: mastodonUsername = username;
    			break;
    		default: break;
    	}
    }
    
    public void setTimeline(Social social, boolean bool) {
    	switch(social) {
    		case BLUESKY: displayBluesky = bool;
    			break;
    		case MASTODON: displayMastodon = bool;
    			break;
    		default: break;
    	}
    }
    
    public void login(Social social) {  
    	switch(social) {
			case BLUESKY: blueskyLoggedIn = true;
				break;
			case MASTODON: mastodonLoggedIn = true;
				break;
			default: break;
		}
    }
    
    public void logout(Social social) {
    	switch(social) {
			case BLUESKY: blueskyLoggedIn = false;
				break;
			case MASTODON: mastodonLoggedIn = false;
				break;
			default: break;
		}
    }
    
    public boolean isLoggedIn(Social social) {
    	switch(social) {
			case BLUESKY: return blueskyLoggedIn;
			case MASTODON: return mastodonLoggedIn;
			default: return false;
    	}
    }
    
    public boolean isDisplayed(Social social) {
    	switch(social) {
			case BLUESKY: return displayBluesky;
			case MASTODON: return displayMastodon;
			default: return false;
    	}
    }
    
    public String getUsername(Social social) {
    	switch(social) {
			case BLUESKY: return blueskyUsername;
			case MASTODON: return mastodonUsername;
			default: return "";
    	}
    }
    
}
