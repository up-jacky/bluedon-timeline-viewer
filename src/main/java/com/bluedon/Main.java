package com.bluedon;

import com.bluedon.controllers.PageController;

import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        PageController.setStage(stage);
        PageController.displayLoginPage();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
