package bluedon.timeline.viewer;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

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
    }

    public void showHomePage(String username) {
        HomePage homePage = new HomePage(this, username);
        Scene homeScene = homePage.getView();  // already a Scene
        primaryStage.setScene(homeScene);
        primaryStage.setTitle("Bluedon");
    }
    
    /* public void showHomePage(String username) {
        HomePage homePage = new HomePage(this, username);
        Scene homeScene = new Scene(homePage.getView(),1000, 600);
        primaryStage.setTitle("Bluedon Timeline Viewer - Home");
        primaryStage.setScene(homeScene);
    } */

    public static void main(String[] args) {
        launch(args);
    }
}
