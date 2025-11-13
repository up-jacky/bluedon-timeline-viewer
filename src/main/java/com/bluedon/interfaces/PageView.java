package com.bluedon.interfaces;

import javafx.stage.Stage;

/**
 * Main interface for what methods of each page should have.
 */
public interface PageView {
	
	/**
	 * Displays the stage to the application.
	 * @param primaryStage
	 */
	public void displayPage(Stage primaryStage);

	/**
	 * Initializes the pages.
	 */
	public void init();

	
}
