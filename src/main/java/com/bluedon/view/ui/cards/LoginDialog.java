package com.bluedon.view.ui.cards;

import javafx.geometry.Insets;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

public class LoginDialog {
	
	private String serviceName;
	private String username;

	public LoginDialog(String serviceName) {
		this.serviceName = serviceName;
		showLoginDialog();
	}
	
	public String getUsername() {
		return username;
	}

    public void showLoginDialog() {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Log In to " + serviceName);

        Label userLabel = new Label("Username:");
        TextField userField = new TextField();
        Label passLabel = new Label("Password:");
        PasswordField passField = new PasswordField();

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
        grid.add(userLabel, 0, 0);
        grid.add(userField, 1, 0);
        grid.add(passLabel, 0, 1);
        grid.add(passField, 1, 1);

        dialog.getDialogPane().setContent(grid);

        ButtonType loginButtonType = new ButtonType("Log In", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(loginButtonType);

        dialog.setResultConverter(button -> {
            if (button == loginButtonType) {
                return userField.getText().isBlank() ? null : userField.getText();
            }
            return null;
        });

        username = dialog.showAndWait().orElse("");
    }
}
