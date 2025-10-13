package com.bluedon.main;

import com.bluedon.main.controllers.LoginController;
import com.bluedon.main.view.LoginView;
import com.bluedon.main.models.Login;

import javafx.application.Application;
//import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

//    private Stage primaryStage;

    @Override
    public void start(Stage stage) {
//        this.primaryStage = stage;
        
        Login model = new Login();
        LoginView view = new LoginView();
        LoginController controller = new LoginController(model, view);
        
        controller.start(stage);
        stage.show();
    }

//    public void showHomePage(String blueskyEmail, String mastodonEmail) {
//        HomeView homeView = new HomeView(this, blueskyEmail, mastodonEmail);
//        Scene homeScene = homeView.getView();
//        primaryStage.setScene(homeScene);
//        primaryStage.setTitle("Bluedon Home");
//    }

    public static void main(String[] args) {
        launch(args);
    }
}
