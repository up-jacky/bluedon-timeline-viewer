package com.bluedon.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.bluedon.controllers.PageController;
import com.bluedon.enums.ToastType;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import javafx.util.Duration;

public class Toast {
    public static ToastStream info = new ToastStream(ToastType.INFO);
    public static ToastStream warn = new ToastStream(ToastType.WARNING);
    public static ToastStream error = new ToastStream(ToastType.ERROR);
    public static ToastStream debug = new ToastStream(ToastType.DEBUG);
    public static ToastStream fatal = new ToastStream(ToastType.FATAL);
    public static ToastStream success = new ToastStream(ToastType.SUCCESS);

    private static List<String> toasts = new ArrayList<>();
    private static final Lock lock = new ReentrantLock();

    public static class ToastStream {
        private ToastType type;

        public ToastStream(ToastType type) {
            this.type = type;
        }

        public void showToast(Window owner, String message, int durationInMillis) {
            Platform.runLater(() -> {
                if(toasts.indexOf(message) != -1) return;
                else {
                    lock.lock();
                    toasts.add(message);
                    lock.unlock();
                }
                Stage stage = new Stage();

                stage.initOwner(owner);
                stage.setResizable(false);
                stage.initStyle(StageStyle.TRANSPARENT);
                
                Label label = new Label(String.format(message));
                label.getStyleClass().add("label");
                label.setPrefHeight(24);

                HBox root = new HBox(label);
                HBox.setHgrow(label,Priority.ALWAYS);
                root.getStyleClass().add(type.name().toLowerCase());

                Scene scene = new Scene(root);
                scene.setFill(Color.TRANSPARENT);
                scene.getStylesheets().add("css/toast.css");

                stage.setScene(scene);
                
                double primaryX = owner.getX();
                double primaryY = owner.getY();
                double primaryHeight = owner.getHeight();
                // Displays the toast at the bottom left of the screen with Offsets equal to the Offset variables.
                lock.lock();
                double index = (double) toasts.indexOf(message) + 1;
                System.out.println(message + ": " + index);
                System.out.println("TOASTS: " + toasts);
                lock.unlock();
                double offsetX = 24;
                double offsetY = 24;
                stage.setX(primaryX + offsetX);
                stage.setY(primaryY + primaryHeight - (24 * index) - offsetY - (12 * (index - 1)));
                stage.show();
                Timeline timeline = new Timeline(new KeyFrame(Duration.millis(durationInMillis), e -> {
                    stage.close();
                    lock.lock();
                    toasts.remove(message);
                    lock.unlock();
                }));
                timeline.play();
            });
        }

        public void showToast(String message, int durationInMillis) {
            showToast(PageController.getStage(), message, durationInMillis);
        }

        public void showToast(String message) {
            showToast(PageController.getStage(), message, 3000);
        }
        
        public void showToast(Window owner, String message) {
            showToast(owner, message, 3000);
        }
    }
}
