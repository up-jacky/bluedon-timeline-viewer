package com.bluedon.controllers;

import com.bluedon.enums.Page;
import com.bluedon.services.ServiceRegistry;
import com.bluedon.utils.SessionFile;

import javafx.stage.Stage;

/**
 * PageController is the main controller of the application
 * that handles switching between the two pages, Home and Login.
 * 
 * <p> It also holds the static value for the controllers
 * of both pages.
 * </p>
 */
public class PageController {

    private static Stage stage;

    /**
     * Static variable for the Login page.
     * 
     * <p> It is used to access models and view of the LoginController
     * </p>
     */
    public static LoginController login = new LoginController();

    /**
     * Static variable for the Home page.
     * 
     * <p> It is used to access models and view of the HomeController
     * </p>
     */
    public static HomeController home = new HomeController();

    /**
     * Type of the current page.
     */
    public static Page currentPage = Page.LOGIN;

    /**
     * Sets the value of the stage of this main page controller.
     * 
     * @param mainStage The main stage of the application.
     */
    public static void setStage(Stage mainStage) {
        stage = mainStage;
    }

    /**
     * Returns the primaryStage of the application.
     * @return {@link Stage}
     */
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
