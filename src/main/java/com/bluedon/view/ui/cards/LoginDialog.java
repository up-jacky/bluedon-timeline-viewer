package com.bluedon.view.ui.cards;

import org.json.JSONObject;

import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class LoginDialog {
    private static boolean isHandleValid = true;
    private static boolean isPasswordValid = true;

    public static JSONObject showLoginDialog() {
        Dialog<JSONObject> dialog = new Dialog<>();
        dialog.setTitle("Bluesky Login");

        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getStylesheets().add("css/login.css");


        Label  handleLabel = new Label("Handle:");
        Label  blueskyLabel = new Label("bsky.social");
        Label passwordLabel = new Label("Password:");
        TextField handleField = new TextField();
        PasswordField passwordField = new PasswordField();

        blueskyLabel.getStyleClass().add("bsky");
        handleLabel.getStyleClass().add("label");
        handleField.getStyleClass().add("handle-field");

        passwordLabel.getStyleClass().add("label");
        passwordField.getStyleClass().add("handle-field");

        HBox handleContainer = new HBox(8, handleLabel, handleField, blueskyLabel);
        HBox passwordContainer = new HBox(8, passwordLabel, passwordField);
        VBox formContainer = new VBox(8, handleContainer, passwordContainer);

        dialog.getDialogPane().setContent(formContainer);

        ButtonType loginButtonType = new ButtonType("Login", ButtonBar.ButtonData.FINISH);

        dialog.getDialogPane().getButtonTypes().addAll(loginButtonType);

        Button loginButton = (Button) dialog.getDialogPane().lookupButton(loginButtonType);

        loginButton.getStyleClass().add("login-button");

        dialog.getDialogPane();

        dialog.setOnCloseRequest(e -> {
            if(!isHandleValid || !isPasswordValid) {
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setTitle("Login failed!");
                if(!isHandleValid) {
                    isHandleValid = true;
                    errorAlert.setHeaderText("Handle Field is empty.");
                    errorAlert.setContentText("Please input your handle in the handle field.");
                    errorAlert.showAndWait();
                    e.consume();
                } else {
                    isPasswordValid = true;
                    errorAlert.setHeaderText("Password Field is empty.");
                    errorAlert.setContentText("Please input your password in the password field.");
                    errorAlert.showAndWait();
                    e.consume();
                }
            }
        });

        dialog.setResultConverter(button -> {
            if (button == loginButtonType) {
                JSONObject jsonCreds = new JSONObject();
                jsonCreds.put("handle", handleField.getText() + ".bsky.social");
                jsonCreds.put("password", passwordField.getText());

                if (jsonCreds.getString("handle") == null || jsonCreds.getString("handle").trim().isEmpty()) {
                    handleField.setText("");
                    isHandleValid = false;
                    
                } else if (jsonCreds.getString("password") == null || jsonCreds.getString("password").trim().isEmpty()) {
                    passwordField.setText("");
                    isPasswordValid = false;
                } else return jsonCreds;
            } 
            return null;
        });

        dialog.showAndWait();
        return dialog.getResult();
    }
}
