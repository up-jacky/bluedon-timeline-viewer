package com.bluedon.view.ui.cards;

import com.bluedon.services.ServiceRegistry;

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

    public static String showLoginDialog() {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Bluesky Password");

        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getStylesheets().add("css/login.css");

        Label  handleLabel = new Label("Handle:");
        Label passwordLabel = new Label("Password:");
        TextField handleField = new TextField();
        PasswordField passwordField = new PasswordField();

        handleLabel.getStyleClass().add("label");
        handleField.setEditable(false);
        handleField.getStyleClass().add("handle-field");
        handleField.setText(ServiceRegistry.getBlueskySession().handle);

        passwordLabel.getStyleClass().add("label");
        passwordField.getStyleClass().add("handle-field");

        HBox handleContainer = new HBox(8, handleLabel, handleField);
        HBox passwordContainer = new HBox(8, passwordLabel, passwordField);
        VBox formContainer = new VBox(8, handleContainer, passwordContainer);

        dialog.getDialogPane().setContent(formContainer);

        ButtonType logoutButtonType = new ButtonType("Logout", ButtonBar.ButtonData.FINISH);
        ButtonType loginButtonType = new ButtonType("Login", ButtonBar.ButtonData.FINISH);

        dialog.getDialogPane().getButtonTypes().addAll(logoutButtonType, loginButtonType, ButtonType.CLOSE);

        Button loginButton = (Button) dialog.getDialogPane().lookupButton(loginButtonType);
        Button logoutButton = (Button) dialog.getDialogPane().lookupButton(logoutButtonType);
        Button exitButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.CLOSE);

        loginButton.getStyleClass().add("login-button");
        logoutButton.getStyleClass().add("login-button");
        exitButton.getStyleClass().add("exit-button");

        dialog.getDialogPane();

        dialog.setOnCloseRequest(e -> {
            System.out.println("[INFO][LoginDialog][showLoginDialog] Closing login dialog...");
            if (passwordField.getText() == null) {
                e.consume();
            }
        });

        dialog.setResultConverter(button -> {
            if (button == loginButtonType) {
                String password = passwordField.getText();
                if (password == null || password.trim().isEmpty()) {
                    Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                    errorAlert.setTitle("Login failed!");
                    errorAlert.setHeaderText("Password Field is empty.");
                    errorAlert.setContentText("Please input your password in the password field.");
                    errorAlert.setResultConverter(b -> {
                        errorAlert.close();
                        return null;
                    });
                    errorAlert.showAndWait();
                    passwordField.setText(null);
                    return null;
                }
                return password;
            } else if (button == logoutButtonType) {
                ServiceRegistry.setBlueskySession(null);
                return "";
            } else if (button == ButtonType.CLOSE) {
                passwordField.setText("");
                return "";
            }
            return null;
        });

        dialog.showAndWait();
        return passwordField.getText();
    }
}
