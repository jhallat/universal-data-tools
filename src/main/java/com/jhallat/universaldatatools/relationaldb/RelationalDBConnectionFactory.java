package com.jhallat.universaldatatools.relationaldb;

import com.jhallat.universaldatatools.activeconnection.ActiveConnection;
import com.jhallat.universaldatatools.activeconnection.ActiveConnectionFactory;
import com.jhallat.universaldatatools.activeconnection.NullConnection;
import com.jhallat.universaldatatools.connectiondefinitions.entities.ConnectionToken;

public class RelationalDBConnectionFactory implements ActiveConnectionFactory {

    @Override
    public ActiveConnection createConnection(ConnectionToken connectionToken) {
        //TODO implement
        return new NullConnection();
    }
}
