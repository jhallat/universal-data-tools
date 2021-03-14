package com.jhallat.universaldatatools.activeconnection;

import com.jhallat.universaldatatools.connectiondefinitions.ConnectionToken;

public interface ActiveConnectionFactory {

    ActiveConnection createConnection(ConnectionToken connectionToken);

}
