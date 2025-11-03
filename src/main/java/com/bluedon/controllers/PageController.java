package com.bluedon.controllers;

import com.bluedon.enums.Page;
import com.bluedon.services.ServiceRegistry;
import com.bluedon.utils.SessionFile;

import javafx.stage.Stage;

public class PageController {

    private static Stage stage;
    public static LoginController login = new LoginController();
    public static HomeController home = new HomeController();
    public static Page currentPage = Page.LOGIN;

    /**
     * Sets the value of the stage of this main page controller
     * 
     * @param mainStage the stage that will be switched often
     */
    public static void setStage(Stage mainStage) {
        stage = mainStage;
    }

    public static Stage getStage() {
        return stage;
    }
    
    /**
     *  Displays the login page
     */
    public static void displayLoginPage() {
        SessionFile.readSession();
        System.out.println("[INFO][PageController][displayLoginPage] Bluesky: " + ServiceRegistry.isBlueskyLoggedIn() + " Mastodon: " + ServiceRegistry.isMastodonLoggedIn());
        if (ServiceRegistry.isBlueskyLoggedIn() == false && ServiceRegistry.isMastodonLoggedIn() == false){
            currentPage = Page.LOGIN;
            login.start(stage);
        } else {
            currentPage = Page.HOME;
            displayHomePage();
        }
    }

    /**
     * Displays the home page
     */
    public static void displayHomePage() {
        if (ServiceRegistry.isBlueskyLoggedIn() == true || ServiceRegistry.isMastodonLoggedIn() == true){
            currentPage = Page.HOME;
            home.start(stage);
        } else {
            currentPage = Page.LOGIN;
            displayLoginPage();
        }
    }

}
