package com.tidebreakerstudios.freedom_tide.exception;

/**
 * Exceção lançada quando um tutorial não é encontrado.
 */
public class TutorialNotFoundException extends RuntimeException {
    public TutorialNotFoundException(String message) {
        super(message);
    }
}