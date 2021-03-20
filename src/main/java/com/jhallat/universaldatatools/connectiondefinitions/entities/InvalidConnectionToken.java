package com.jhallat.universaldatatools.connectiondefinitions.entities;

import java.util.HashMap;

public class InvalidConnectionToken extends ConnectionToken {

    public InvalidConnectionToken(String invalidDescription) {
        //TODO Replace "NONE" with a constant
        super("", "Invalid", "NONE", "invalid", invalidDescription, false, new HashMap<>());
    }

}
