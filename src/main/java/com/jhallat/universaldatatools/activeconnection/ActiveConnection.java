package com.jhallat.universaldatatools.activeconnection;

public abstract class ActiveConnection {

    private long lastUsed;

    public abstract String getLabel();

    public void markAsUsed() {
        lastUsed = System.currentTimeMillis();
    }

    public long getLastTimeUsed() {
        return lastUsed;
    }

    public abstract void close();

}
