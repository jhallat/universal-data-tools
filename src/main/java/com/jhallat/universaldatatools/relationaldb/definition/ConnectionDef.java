package com.jhallat.universaldatatools.relationaldb.definition;

import java.sql.Connection;

public record ConnectionDef(String label, Connection connection) {
}
