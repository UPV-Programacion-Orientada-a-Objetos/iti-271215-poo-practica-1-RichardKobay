package edu.upvictoria.fpoo.exceptions;

public class EmptySelectException extends Exception{
    public EmptySelectException() {
        super();
    }

    public EmptySelectException(String message) {
        super(message);
    }
}
