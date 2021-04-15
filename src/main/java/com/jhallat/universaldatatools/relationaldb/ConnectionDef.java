package com.jhallat.universaldatatools.relationaldb;

import java.sql.Connection;

public record ConnectionDef(String label, Connection connection) {
}
