package com.bluedon.utils;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.bluedon.controllers.PageController;
import com.bluedon.enums.ToastType;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
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

    private static ObservableList<String> toasts = FXCollections.observableArrayList();
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

                HBox root = new HBox(label);
                HBox.setHgrow(label,Priority.ALWAYS);
                root.getStyleClass().add(type.name().toLowerCase());

                Scene scene = new Scene(root);
                scene.setFill(Color.TRANSPARENT);
                scene.getStylesheets().add("css/toast.css");

                stage.setScene(scene);
                stage.show();
                
                // Displays the toast at the bottom right of the screen with Offsets equal to the Offset variables.
                double primaryX = owner.getX();
                double primaryY = owner.getY();
                double primaryWidth = owner.getWidth();
                double primaryHeight = owner.getHeight();
                lock.lock();
                double index = (double) toasts.indexOf(message) + 1;
                System.out.println(message + ": " + index);
                System.out.println("TOASTS: " + toasts);
                lock.unlock();
                double offsetX = 24;
                double offsetY = 24;

                owner.xProperty().addListener((o, oldValue, newValue) -> {
                    stage.setX(newValue.doubleValue() + owner.getWidth() - (root.getBoundsInLocal().getWidth() + offsetX));
                });

                owner.yProperty().addListener((o, oldValue, newValue) -> {
                    stage.setY(newValue.doubleValue() + owner.getHeight() - ((root.getBoundsInLocal().getHeight() * index) + offsetY + (12 * (index - 1))));
                });

                owner.widthProperty().addListener((o, oldValue, newValue) -> {
                    stage.setX(owner.getX() + newValue.doubleValue() - (root.getBoundsInLocal().getWidth() + offsetX));
                });
                
                owner.heightProperty().addListener((o, oldValue, newValue) -> {
                    stage.setY(owner.getY() + newValue.doubleValue() - ((root.getBoundsInLocal().getHeight() * index) + offsetY + (12 * (index - 1))));
                });

                toasts.addListener(new ListChangeListener<String>() {
                    @Override
                    public void onChanged(Change<? extends String> c) {
                        double newIndex = (double) c.getList().indexOf(message) + 1;
                        stage.setY(owner.getY() + owner.getHeight() -((root.getBoundsInLocal().getHeight() * newIndex) + offsetY + (12 * (newIndex - 1))));
                    }
                });

                stage.setX(primaryX + primaryWidth - (root.getBoundsInLocal().getWidth() + offsetX));
                stage.setY(primaryY + primaryHeight - ((root.getBoundsInLocal().getHeight() * index) + offsetY + (12 * (index - 1))));

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
