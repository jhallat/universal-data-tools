package com.jhallat.universaldatatools.activeconnection;

public class NullConnection extends ActiveConnection {

    @Override
    public String getLabel() {
        return "NONE";
    }

    @Override
    public void close() {
        //No implementation
    }
}
