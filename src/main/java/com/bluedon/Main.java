package com.bluedon;

import com.bluedon.controllers.HomeController;
import com.bluedon.controllers.LoginController;
import com.bluedon.view.HomeView;
import com.bluedon.view.LoginView;
import com.bluedon.models.Home;
import com.bluedon.models.Login;

import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) {

//        Login model = new Login();
//        LoginView view = new LoginView();
//        LoginController controller = new LoginController(model, view);
        
    	Home model = new Home(null, null);
    	HomeView view = new HomeView();
    	HomeController controller = new HomeController(model, view);
    	
        controller.start(stage);
        stage.show();
    }

    public static void main(String[] args) {
        System.out.println("APP");
        launch(args);
    }
}
