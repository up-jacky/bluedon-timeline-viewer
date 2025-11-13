package com.bluedon.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.bluedon.controllers.PageController;
import com.bluedon.enums.ToastType;

import javafx.animation.FadeTransition;
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

/**
 * Displays a toast message to the user in a stage over the current stage.
 * 
 * <p> Its color depends on the type of toast it was set to. </p>
 * 
 * @see Toast#info
 * @see Toast#warn
 * @see Toast#error
 * @see Toast#debug
 * @see Toast#fatal
 * @see Toast#success
 */
public class Toast {
    /**
     * An output stream of type info ready to send a message.
     * 
     * @see ToastStream#showToast(String)
     * @see ToastStream#showToast(String, int)
     * @see ToastStream#showToast(Window, String)
     * @see ToastStream#showToast(Window, String, int)
     */
    public static ToastStream info = new ToastStream(ToastType.INFO);
    
    /**
     * An output stream of type warning ready to send a message.
     * 
     * @see ToastStream#showToast(String)
     * @see ToastStream#showToast(String, int)
     * @see ToastStream#showToast(Window, String)
     * @see ToastStream#showToast(Window, String, int)
     */
    public static ToastStream warn = new ToastStream(ToastType.WARNING);
    
    /**
     * An output stream of type error ready to send a message.
     * 
     * @see ToastStream#showToast(String)
     * @see ToastStream#showToast(String, int)
     * @see ToastStream#showToast(Window, String)
     * @see ToastStream#showToast(Window, String, int)
     */
    public static ToastStream error = new ToastStream(ToastType.ERROR);
    
    /**
     * An output stream of type debug ready to send a message.
     * 
     * @see ToastStream#showToast(String)
     * @see ToastStream#showToast(String, int)
     * @see ToastStream#showToast(Window, String)
     * @see ToastStream#showToast(Window, String, int)
     */
    public static ToastStream debug = new ToastStream(ToastType.DEBUG);
    
    /**
     * An output stream of type fatal ready to send a message.
     * 
     * @see ToastStream#showToast(String)
     * @see ToastStream#showToast(String, int)
     * @see ToastStream#showToast(Window, String)
     * @see ToastStream#showToast(Window, String, int)
     */
    public static ToastStream fatal = new ToastStream(ToastType.FATAL);
    
    /**
     * An output stream of type success ready to send a message.
     * 
     * @see ToastStream#showToast(String)
     * @see ToastStream#showToast(String, int)
     * @see ToastStream#showToast(Window, String)
     * @see ToastStream#showToast(Window, String, int)
     */
    public static ToastStream success = new ToastStream(ToastType.SUCCESS);

    private static ObservableList<String> toasts = FXCollections.observableArrayList();
    private static final Lock lock = new ReentrantLock();

    /**
     * An output stream for toast.
     */
    public static class ToastStream {
        private ToastType type;

        /**
         * Creates a ToastStream of the type given in the paramater type.
         * @param type Type of the Toast in {@link ToastType}.
         * 
         * @see ToastType#INFO
         * @see ToastType#WARNING
         * @see ToastType#ERROR
         * @see ToastType#DEBUG
         * @see ToastType#SUCCESS
         * @see ToastType#FATAL
         */
        public ToastStream(ToastType type) {
            this.type = type;
        }

        /**
         * Shows the toast message at the bottom right corner of the screen.
         * @param owner Parent window to anchor to. To be able to set its message at the bottom right corner of the window.
         * @param message The toast message.
         * @param durationInMillis The duration in ms before the toast message disappears.
         */
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
                root.setOpacity(0);

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
                        c.next();
                        if(c.wasRemoved() && !c.getList().isEmpty()){
                            double newIndex = (double) c.getList().indexOf(message) + 1;
                            double oldY = stage.getY();
                            double newY = owner.getY() + owner.getHeight() -((root.getBoundsInLocal().getHeight() * newIndex) + offsetY + (12 * (newIndex - 1)));
                            double stepY = (newY - oldY) / 30;
                            List<KeyFrame> keyFrames = new ArrayList<>();
                            int easeStep = 0;
                            for(int i = 0; i < 7; i += 1) {
                                easeStep += i;
                                int step = easeStep;
                                keyFrames.add(new KeyFrame(Duration.millis(i*10), e -> stage.setY(oldY + (stepY * step))));
                            }
                            for(int i = 1; i < 10; i += 1) {
                                int step = i;
                                keyFrames.add(new KeyFrame(Duration.millis((i + 6) * 10), e -> stage.setY(oldY + (stepY * (21 + step)))));
                            }
                            Timeline timeline = new Timeline(
                                keyFrames.toArray(new KeyFrame[16])
                            );
                            timeline.play();
                        }
                    }
                });

                stage.setX(primaryX + primaryWidth - (root.getBoundsInLocal().getWidth() + offsetX));
                stage.setY(primaryY + primaryHeight - ((root.getBoundsInLocal().getHeight() * index) + offsetY + (12 * (index - 1))));

                Timeline timeline = new Timeline(
                    new KeyFrame(Duration.ZERO, e -> {
                        FadeTransition fadeTransition = new FadeTransition(Duration.millis(durationInMillis/10), root);
                        fadeTransition.setFromValue(0);
                        fadeTransition.setToValue(1);
                        fadeTransition.play();
                    }),
                    new KeyFrame(Duration.millis(durationInMillis - (durationInMillis/10)), e -> {
                        FadeTransition fadeTransition = new FadeTransition(Duration.millis(durationInMillis/10), root);
                        fadeTransition.setFromValue(1);
                        fadeTransition.setToValue(0);
                        fadeTransition.play();
                    }),
                    new KeyFrame(Duration.millis(durationInMillis), e -> {
                        stage.close();
                        lock.lock();
                        toasts.remove(message);
                        lock.unlock();
                    })
                );

                timeline.play();
            });
        }

        /**
         * Shows the toast message at the bottom right corner of the default window in {@link PageController#getStage()}.
         * @param message The toast message.
         * @param durationInMillis The duration in ms before the toast message disappears.
         */
        public void showToast(String message, int durationInMillis) {
            showToast(PageController.getStage(), message, durationInMillis);
        }

        /**
         * Shows the toast message at the bottom right corner of the default window in {@link PageController#getStage()} for 3s.
         * @param message The toast message.
         */
        public void showToast(String message) {
            showToast(PageController.getStage(), message, 3000);
        }
        
        /**
         * Shows the toast message at the bottom right corner of the screen for 3s.
         * @param owner Parent window to anchor to. To be able to set its message at the bottom right corner of the window.
         * @param message The toast message.
         */
        public void showToast(Window owner, String message) {
            showToast(owner, message, 3000);
        }
    }
}
