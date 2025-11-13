package com.bluedon.view.ui.buttons;

import javafx.animation.FadeTransition;
import javafx.animation.PathTransition;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.InnerShadow;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.util.Duration;

/**
 * DefaultButton is an extension of {@link Button} with transition effects.
 */
public class DefaultButton extends Button {
    private PathTransition initTransition = new PathTransition();
    private FadeTransition fadeTransition = new FadeTransition();
    private PathTransition transition = new PathTransition();
    private ObjectProperty<Color> color = new SimpleObjectProperty<>(Color.GRAY);

    /**
     * Plays the transition animations.
     */
    public void playInitAnimation() {
        Platform.runLater(() -> {
            initTransition.play();
            fadeTransition.play();
        });
    }
    
    /**
     * Creates a button with the specified text as its label and its specified color as its fill.
     * @param name Label for the button.
     * @param color Color for the button.
     */
    public DefaultButton(String name, Color color) {
        super(name);
        this.color.set(color);
        init();
    }

    /**
     * Creates a button with the specified text as its label.
     * @param name Label for the button.
     */
    public DefaultButton(String name) {
        super(name);
        init();
    }
    
    /**
     * Creates a button with an empty string as its label and its specified color as its fill.
     * @param color Color for the button.
     */
    public DefaultButton(Color color) {
        super();
        this.color.set(color);
        init();
    }

    /**
     * Creates a default button.
     */
    public DefaultButton() {
        super();
        init();
    }
    
    /**
     * Initializes the button to have its own transition and effects.
     */
    public void init() {

        this.setEffect(new DropShadow(2,0,2, color.get().brighter()));
        this.setOpacity(0);

        transition.setDuration(Duration.millis(100));
        transition.setNode(this);

        initTransition = new PathTransition();
        initTransition.setDuration(Duration.millis(300));
        initTransition.setNode(this);
        initTransition.setDelay(Duration.millis(500));

        fadeTransition = new FadeTransition();
        fadeTransition.setNode(this);
        fadeTransition.setFromValue(0);
        fadeTransition.setToValue(1);
        fadeTransition.setDuration(Duration.millis(300));
        fadeTransition.setDelay(Duration.millis(500));
        
        Platform.runLater(() -> {
            double startX = this.getBoundsInLocal().getCenterX();
            double startY = this.getBoundsInLocal().getCenterY();
            transition.setPath(new Line(startX, startY, startX, startY + 2));
            initTransition.setPath(new Line(startX, startY + 8, startX, startY));
        });

        this.backgroundProperty().addListener((o, oldValue, newValue) -> {
            if (newValue != null) this.color.set((Color)newValue.getFills().get(0).getFill());
        });

        color.addListener((o, oldValue, newValue) -> {
            this.color.set(newValue);
            this.setEffect(new DropShadow(2,0,2, color.get().brighter()));
            
            this.setOnMouseEntered(e -> {
                this.setEffect(new DropShadow(2,0,2, color.get().brighter()));
                transition.playFromStart();
                transition.setOnFinished(event -> {this.setEffect(new InnerShadow(4, 0, 2, color.get().darker()));});
            });

            this.setOnMouseExited(e -> {
                this.setEffect(new InnerShadow(4, 0, 2, color.get().darker()));
                transition.setRate(-1);
                transition.play();
                transition.setOnFinished(event -> {this.setEffect(new DropShadow(2,0,2, color.get().brighter()));});
            });
        });

        this.setOnMouseEntered(e -> {
            this.setCursor(Cursor.HAND);
            this.setEffect(new DropShadow(2,0,2, color.get().brighter()));
            transition.playFromStart();
            transition.setOnFinished(event -> {this.setEffect(new InnerShadow(4, 0, 2, color.get().darker()));});
        });

        this.setOnMouseExited(e -> {
            this.setCursor(Cursor.DEFAULT);
            this.setEffect(new InnerShadow(4, 0, 2, color.get().darker()));
            transition.setRate(-1);
            transition.play();
            transition.setOnFinished(event -> {this.setEffect(new DropShadow(2,0,2, color.get().brighter()));});
        });
    }
}
