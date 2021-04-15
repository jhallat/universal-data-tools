package com.jhallat.universaldatatools.relationaldb;

import java.util.List;

public record DatabaseDef(String name, List<TableDef> tables, List<ViewDef> views) {}
