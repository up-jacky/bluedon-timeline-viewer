package com.bluedon.view.ui.cards;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import com.bluedon.utils.Toast;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.PathTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * Dialog box that appears when the Login button for Bluesky is clicked.
 */
public class LoginDialog {
    private static boolean isHandleValid = true;
    private static boolean isPasswordValid = true;

    /**
     * Displays the login dialog.
     * @return {@link JSONObject} that contains the {@code handle} and {@code password} of the user.
     */
    public static JSONObject showLoginDialog() {
        Dialog<JSONObject> dialog = new Dialog<>();
        dialog.setTitle("Bluesky Login");

        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getStylesheets().add("css/login.css");
        dialogPane.getScene().getRoot().setId("root");

        Label blueskyLabel = new Label("bsky.social");
        blueskyLabel.setFont(new Font("Consolas", 12));
        blueskyLabel.setTextFill(Color.WHITE);
        TextField handleField = new TextField();
        PasswordField passwordField = new PasswordField();
        handleField.setPromptText("Handle");
        passwordField.setPromptText("Password");
        handleField.setPrefWidth(96);
        passwordField.setPrefWidth(192);

        HBox handleContainer = new HBox(8, handleField, blueskyLabel);
        HBox passwordContainer = new HBox(8, passwordField);
        VBox formContainer = new VBox(8, handleContainer, passwordContainer);

        dialogPane.setContent(formContainer);

        ButtonType loginButtonType = new ButtonType("Login", ButtonBar.ButtonData.FINISH);

        dialog.getDialogPane().getButtonTypes().addAll(loginButtonType);

        Button loginButton = (Button) dialog.getDialogPane().lookupButton(loginButtonType);

        PathTransition loginTransition = new PathTransition();
        loginTransition.setDuration(Duration.millis(100));
        loginTransition.setNode(loginButton);
        
        Platform.runLater(() -> {
            double startX = loginButton.getBoundsInLocal().getCenterX();
            double startY = loginButton.getBoundsInLocal().getCenterY();
            loginTransition.setPath(new Line(startX, startY, startX, startY + 2));
        });

        loginButton.setOnMouseEntered(e -> {
            loginTransition.playFromStart();
        });

        loginButton.setOnMouseExited(e -> {
            loginTransition.setRate(-1);
            loginTransition.play();
        });

        loginButton.getStyleClass().addAll("login", "dialog");

        dialog.getDialogPane();

        dialog.setOnCloseRequest(e -> {
            if(isHandleValid == false) {
                isHandleValid = true;
                Toast.error.showToast("Login failed! Error: Handle field is empty.");
                e.consume();
            } else if(isPasswordValid == false) {
                isPasswordValid = true;
                Toast.error.showToast("Login failed! Error: Password field is empty.");
                e.consume();
            }
        });

        dialog.setResultConverter(button -> {
            if (button == loginButtonType) {
                JSONObject jsonCreds = new JSONObject();
                jsonCreds.put("handle", handleField.getText() + ".bsky.social");
                jsonCreds.put("password", passwordField.getText());

                List<KeyFrame> keyFrames = new ArrayList<>();
                Timeline timeline = new Timeline();
                PathTransition fieldTransition = new PathTransition();

                if (jsonCreds.getString("handle") == null || jsonCreds.getString("handle").trim().equals(".bsky.social")) {
                    createErrorFieldKeyFrames(keyFrames, handleField, fieldTransition);
                    for(KeyFrame kf: keyFrames) timeline.getKeyFrames().add(kf);
                    fieldTransition.play();
                    timeline.play();
                    isHandleValid = false;
                    return null;
                } else if (jsonCreds.getString("password") == null || jsonCreds.getString("password").trim().isEmpty()) {
                    createErrorFieldKeyFrames(keyFrames, passwordField, fieldTransition);
                    for(KeyFrame kf: keyFrames) timeline.getKeyFrames().add(kf);
                    fieldTransition.play();
                    timeline.play();
                    isPasswordValid = false;
                    return null;
                } else return jsonCreds;
            } 
            return null;
        });

        Stage dialogStage = (Stage) dialogPane.getScene().getWindow();
        dialogStage.setAlwaysOnTop(true);

        dialog.showAndWait();
        return dialog.getResult();
    }

    private static void createErrorFieldKeyFrames(List<KeyFrame> keyFrames, TextField field, PathTransition pathTransition) {
        pathTransition.setDuration(Duration.millis(150));
        pathTransition.setNode(field);
        double startX = field.getBoundsInLocal().getCenterX();
        double startY = field.getBoundsInLocal().getCenterY();
        pathTransition.setPath(new Line(startX - 2, startY, startX, startY));
        pathTransition.setCycleCount(4);
        keyFrames.add(new KeyFrame(Duration.millis(0), new KeyValue(field.borderProperty(), new Border(new BorderStroke(Color.web("#4A5899", 1), BorderStrokeStyle.SOLID, new CornerRadii(4), new BorderWidths(1))), Interpolator.EASE_OUT)));
        keyFrames.add(new KeyFrame(Duration.millis(50), new KeyValue(field.borderProperty(), new Border(new BorderStroke(Color.web("#4A5899", 0.5), BorderStrokeStyle.SOLID, new CornerRadii(4), new BorderWidths(1))))));
        keyFrames.add(new KeyFrame(Duration.millis(100), new KeyValue(field.borderProperty(), new Border(new BorderStroke(Color.web("#FF0000", 0.5), BorderStrokeStyle.SOLID, new CornerRadii(4), new BorderWidths(1))))));
        keyFrames.add(new KeyFrame(Duration.millis(150), new KeyValue(field.borderProperty(), new Border(new BorderStroke(Color.web("#FF0000", 1), BorderStrokeStyle.SOLID, new CornerRadii(4), new BorderWidths(1))), Interpolator.EASE_IN)));
    }
}
