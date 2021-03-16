package com.jhallat.universaldatatools.activeconnection;

import com.jhallat.universaldatatools.connectiondefinitions.ConnectionLabel;

public class NullConnection extends ActiveConnection {
    @Override
    public ConnectionLabel getActiveConnectionType() {
        return ConnectionLabel.NONE;
    }

    @Override
    public void close() {
        //No implementation
    }
}
