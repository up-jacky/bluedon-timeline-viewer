package com.bluedon.services;

import com.bluedon.controllers.PageController;
import com.bluedon.models.Home;
import com.bluedon.utils.Toast;
import com.bluedon.view.HomeView;
import com.bluedon.view.LoginView;

import javafx.concurrent.Task;
import javafx.scene.control.ScrollPane;
import javafx.stage.Stage;

public class Refresh {

    public static void refreshHome() {
        HomeRefresh refresh = new HomeRefresh();
        Thread thread = new Thread(refresh);
        thread.setDaemon(true);
        thread.start();
    }

    public static void refreshLogin() {
        LoginRefresh refresh = new LoginRefresh();
        Thread thread = new Thread(refresh);
        thread.setDaemon(true);
        thread.start();
    }

    public static void refreshPosts() {
        HomeRefresh refreshHome = new HomeRefresh();
        Thread threadA = new Thread(refreshHome);

        threadA.setDaemon(true);
        threadA.start();
        try{threadA.join(1000);} catch (Exception e) {e.printStackTrace();}

        PostsRefresh refreshPosts = new PostsRefresh();
        Thread threadB = new Thread(refreshPosts);

        threadB.setDaemon(true);
        threadB.start();
    }

    private static class PostsRefresh extends Task <Boolean> {
        private Stage stage = PageController.getStage();
        private HomeView view = PageController.home.getView();
        private Home model = PageController.home.getModel();

        @Override
        protected Boolean call() throws Exception {
            System.out.println("[DEBUG][Refresh][call] Thread: " + Thread.currentThread());
            System.out.println("[INFO][Refresh][call] Refreshing posts...");
            Toast.info.showToast("Refreshing posts...");
            model.refreshPosts();
            return true;
        }

        @Override
        protected void succeeded() {
            System.out.println("[DEBUG][Refresh][succeeded] Thread: " + Thread.currentThread());
            System.out.println("[INFO][Refresh][succeeded] Refreshing posts success!");
            Toast.success.showToast("Successful refreshing posts!");
            ScrollPane postsArea = view.createPostsArea(model.postsContainer);
            view.updateLayout(null, postsArea);
            view.displayPage(stage);
            stage.show();
        }
    }

    private static class LoginRefresh extends Task<Boolean> {
        private Stage stage = PageController.getStage();
        private LoginView view = PageController.login.getView();

        @Override
        protected Boolean call() throws Exception {
            System.out.println("[DEBUG][Refresh][LoginRefresh][call] Thread: " + Thread.currentThread());
            return true;
        }

        @Override
        protected void succeeded() {
            System.out.println("[DEBUG][Refresh][LoginRefresh][succeeded] Thread: " + Thread.currentThread());
            System.out.println("[INFO][Refresh][LoginRefresh][succeeded] Setting Login page to refresh...");
            view.updateLayout(40);
            view.displayPage(stage);
            stage.show();
        }
    }

    private static class HomeRefresh extends Task<Boolean> {
        private Stage stage = PageController.getStage();
        private HomeView view = PageController.home.getView();
        private Home model = PageController.home.getModel();

        @Override
        protected Boolean call() throws Exception {
            System.out.println("[DEBUG][Refresh][HomeRefresh][call] Thread: " + Thread.currentThread());
            model.postsContainer = null;
            return true;
        }

        @Override
        protected void succeeded() {
            System.out.println("[DEBUG][Refresh][HomeRefresh][succeeded] Thread: " + Thread.currentThread());
            System.out.println("[INFO][Refresh][HomeRefresh][succeeded] Setting Home page to refresh...");
            ScrollPane postsArea = view.createPostsArea();
            view.updateLayout(null, postsArea);
            view.displayPage(stage);
            stage.show();
        }
    }
}
