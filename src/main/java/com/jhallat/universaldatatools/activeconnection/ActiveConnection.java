package com.jhallat.universaldatatools.activeconnection;

import com.jhallat.universaldatatools.connectiondefinitions.ConnectionLabel;

public abstract class ActiveConnection {

    private long lastUsed;

    public abstract ConnectionLabel getActiveConnectionType();

    public void markAsUsed() {
        lastUsed = System.currentTimeMillis();
    }

    public long getLastTimeUsed() {
        return lastUsed;
    }

}
