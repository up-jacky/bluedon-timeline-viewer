package com.bluedon.controllers;

import com.bluedon.services.ServiceRegistry;
import com.bluedon.utils.SessionFile;

import javafx.stage.Stage;

public class PageController {

    private static Stage stage;
    private static LoginController login = new LoginController();
    private static HomeController home = new HomeController();

    /**
     * Sets the value of the stage of this main page controller
     * 
     * @param mainStage the stage that will be switched often
     */
    public static void setStage(Stage mainStage) {
        stage = mainStage;
    }
    
    /**
     *  Displays the login page
     */
    public static void displayLoginPage() {
        SessionFile.readSession();
        System.out.println("[INFO] Bluesky: " + ServiceRegistry.isBlueskyLoggedIn() + " Mastodon: " + ServiceRegistry.isMastodonLoggedIn());
        if (ServiceRegistry.isBlueskyLoggedIn() == false && ServiceRegistry.isMastodonLoggedIn() == false){
            login.start(stage);
        } else {
            displayHomePage();
        }
    }

    /**
     * Displays the home page
     */
    public static void displayHomePage() {
        if (ServiceRegistry.isBlueskyLoggedIn() == true || ServiceRegistry.isMastodonLoggedIn() == true){
            home.start(stage);
        } else {
            displayLoginPage();
        }
    }

}
