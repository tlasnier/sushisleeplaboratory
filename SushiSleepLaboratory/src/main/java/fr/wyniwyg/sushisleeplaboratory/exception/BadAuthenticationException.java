package fr.wyniwyg.sushisleeplaboratory.exception;

/**
 * Created by t.lasnier on 27/08/13.
 */
public class BadAuthenticationException extends Exception{
    public BadAuthenticationException() {
    }

    public BadAuthenticationException(String message) {
        super(message);
    }
}
