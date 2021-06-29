package com.jhallat.universaldatatools.relationaldb;

import com.jhallat.universaldatatools.relationaldb.definition.DataTypeDef;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DataTypeService {

    private Map<String, String> aliasMap = new HashMap<>();

    {
        aliasMap.put("bigint", "int8");
        aliasMap.put("bigserial", "serial8");
        aliasMap.put("bit varying", "varbit");
        aliasMap.put("boolean", "bool");
        aliasMap.put("character", "char");
        aliasMap.put("character varying", "varchar");
        aliasMap.put("double", "float8");
        aliasMap.put("integer", "int");
        aliasMap.put("numeric", "decimal");
        aliasMap.put("real", "float4");
        aliasMap.put("smallint", "int2");
        aliasMap.put("smallserial", "serial2");
        aliasMap.put("serial", "serial4");
        aliasMap.put("time with time zone", "timez");
        aliasMap.put("timestamp with time zone", "timestampz");
    }

    private List<DataTypeDef> dataTypes = new ArrayList<>();

    {
        dataTypes.add(new DataTypeDef("bigint", false, false, false));
        dataTypes.add(new DataTypeDef("bigserial", false, false, false));
        dataTypes.add(new DataTypeDef("bit", true, false, false));
        dataTypes.add(new DataTypeDef("varbit", true, false, false));
        dataTypes.add(new DataTypeDef("boolean", true, false, false));
        dataTypes.add(new DataTypeDef("box", false, false, false));
        dataTypes.add(new DataTypeDef("bytea", false, false, false));
        dataTypes.add(new DataTypeDef("character", true, false, false));
        dataTypes.add(new DataTypeDef("character varying", true, false, false));
        dataTypes.add(new DataTypeDef("cidr", false, false, false));
        dataTypes.add(new DataTypeDef("circle", false, false, false));
        dataTypes.add(new DataTypeDef("date", false, false, false));
        dataTypes.add(new DataTypeDef("double", false, false, false));
        dataTypes.add(new DataTypeDef("inet", false, false, false));
        dataTypes.add(new DataTypeDef("integer", false, false, false));
        dataTypes.add(new DataTypeDef("interval", false, false, true));
        dataTypes.add(new DataTypeDef("json", false, false, false));
        dataTypes.add(new DataTypeDef("jsonb", false, false, false));
        dataTypes.add(new DataTypeDef("line", false, false, false));
        dataTypes.add(new DataTypeDef("lseg", false, false, false));
        dataTypes.add(new DataTypeDef("macaddr", false, false, false));
        dataTypes.add(new DataTypeDef("macaddr8", false, false, false));
        dataTypes.add(new DataTypeDef("money", false, false, false));
        dataTypes.add(new DataTypeDef("numeric", false, true, true));
        dataTypes.add(new DataTypeDef("path", false, false, false));
        dataTypes.add(new DataTypeDef("pg_lsn", false, false, false));
        dataTypes.add(new DataTypeDef("pg_snapshot", false, false, false));
        dataTypes.add(new DataTypeDef("point", false, false, false));
        dataTypes.add(new DataTypeDef("polygon", false, false, false));
        dataTypes.add(new DataTypeDef("real", false, false, false));
        dataTypes.add(new DataTypeDef("smallint", false, false, false));
        dataTypes.add(new DataTypeDef("smallserial", false, false, false));
        dataTypes.add(new DataTypeDef("serial", false, false, false));
        dataTypes.add(new DataTypeDef("text", false, false, false));
        dataTypes.add(new DataTypeDef("time", false, false, true));
        dataTypes.add(new DataTypeDef("time with time zone", false, false, true));
        dataTypes.add(new DataTypeDef("timestamp", false, false, true));
        dataTypes.add(new DataTypeDef("timestamp with time zone", false, false, true));
        dataTypes.add(new DataTypeDef("tsquery", false, false, false));
        dataTypes.add(new DataTypeDef("tsvector", false, false, false));
        dataTypes.add(new DataTypeDef("txid_snapshot", false, false, false));
        dataTypes.add(new DataTypeDef("uuid", false, false, false));
        dataTypes.add(new DataTypeDef("xml", false, false, false));
    }

    public List<DataTypeDef> getDataTypes() {
        return this.dataTypes.stream().map(type -> new DataTypeDef(convertName(type.name()),
                                                                   type.isLengthProvided(),
                                                                   type.isScaleProvided(),
                                                                   type.isPrecisionProvided()))
                .collect(Collectors.toList());
    }

    public String convertName(String dataType) {
        return this.aliasMap.getOrDefault(dataType, dataType);
    }
}
