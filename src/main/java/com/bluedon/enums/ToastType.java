package com.bluedon.enums;

/**
 * Type of Toasts that appears when running the application.
 */
public enum ToastType {
    /**
     * Provides helpful information about the parts of the back-end to frontend.
     */
    INFO, 
    
    /**
     * Mostly used for calling methods with throwables and returns success.
     */
    SUCCESS, 
    
    /**
     * Debugging purposes.
     */
    DEBUG, 
    
    /**
     * If something unxepected in the program happens but the program may still run.
     */
    WARNING, 
    
    /**
     * If something unexpected happens in the program that has affected some features. 
     * May or may not stop the application.
     */
    ERROR, 
    
    /**
     * If something unexpected happens in the program that needs immediate fixing.
     */
    FATAL
}
