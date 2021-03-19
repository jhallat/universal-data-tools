package com.jhallat.universaldatatools.connectiondefinitions.entities;

import java.util.HashMap;

public class InvalidConnectionToken extends ConnectionToken {

    public InvalidConnectionToken(String invalidDescription) {
        super("", "Invalid", ConnectionLabel.NONE, "invalid", invalidDescription, false, new HashMap<>());
    }

}
