package com.bluedon.controllers;

import com.bluedon.services.AuthSession;
import com.bluedon.services.ServiceRegistry;

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
        login.start(stage);
    }

    /**
     * Displays the home page
     */
    public static void displayHomePage() {
        AuthSession blueskySession = ServiceRegistry.getBlueskySession();
        AuthSession mastodonSession = ServiceRegistry.getMastodonSession();

        if (blueskySession != null || mastodonSession != null) {
            home.start(stage);
        } else {
            displayLoginPage();
        }
    }

}
