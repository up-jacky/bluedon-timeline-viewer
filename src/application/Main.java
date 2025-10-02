package application;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import viewer.HomePage;
import viewer.LoginPage;

public class Main extends Application {

    private Stage primaryStage;

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;

        showLoginPage();
        stage.show();
    }

    public void showLoginPage() {
        LoginPage loginPage = new LoginPage(this);
        Scene loginScene = new Scene(loginPage.getView(), 1000, 600);
        loginScene.getStylesheets().add("file:resources/styles.css");
        primaryStage.setScene(loginScene);
        primaryStage.setTitle("Bluedon Login");
    }

    public void showHomePage(String blueskyEmail, String mastodonEmail) {
        HomePage homePage = new HomePage(this, blueskyEmail, mastodonEmail);
        Scene homeScene = homePage.getView();
        primaryStage.setScene(homeScene);
        primaryStage.setTitle("Bluedon Home");
    }

    public static void main(String[] args) {
        launch(args);
    }
}
