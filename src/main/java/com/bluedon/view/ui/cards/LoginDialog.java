package com.bluedon.view.ui.cards;

import javafx.geometry.Insets;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.GridPane;

public class LoginDialog {

    public static String showLoginDialog() {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Bluesky Password");

        Label passwordLabel = new Label("Password:");
        PasswordField passwordField = new PasswordField();

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
        grid.add(passwordLabel, 0, 1);
        grid.add(passwordField, 1, 1);

        dialog.getDialogPane().setContent(grid);

        ButtonType loginButtonType = new ButtonType("Login", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(loginButtonType);

        dialog.setResultConverter(button -> {
            if (button == loginButtonType) {
                return passwordField.getText().isBlank() ? null : passwordField.getText();
            }
            return null;
        });

        dialog.showAndWait();

        return passwordField.getText();
    }
}
