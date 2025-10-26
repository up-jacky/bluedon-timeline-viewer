package com.bluedon.exception;

import javafx.scene.control.Alert;

public class ErrorMessage {

	public ErrorMessage(String header, String message) {

        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(header);
        alert.setContentText(message);
        
        System.err.println(header + ": " + message);

        alert.showAndWait(); 
	}
}
