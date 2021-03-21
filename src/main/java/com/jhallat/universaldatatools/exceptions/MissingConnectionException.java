package com.jhallat.universaldatatools.exceptions;

public class MissingConnectionException extends Exception {

    public MissingConnectionException() {

        super("Connection not found or expired");
    }

}
