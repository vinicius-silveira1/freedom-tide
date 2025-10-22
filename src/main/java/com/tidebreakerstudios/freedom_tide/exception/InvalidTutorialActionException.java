package com.tidebreakerstudios.freedom_tide.exception;

/**
 * Exceção lançada quando uma ação de tutorial não é válida para a fase atual.
 */
public class InvalidTutorialActionException extends RuntimeException {
    public InvalidTutorialActionException(String message) {
        super(message);
    }
}