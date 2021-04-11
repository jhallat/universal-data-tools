package com.jhallat.universaldatatools.docker;

import lombok.Data;
import org.apache.commons.lang.StringUtils;

@Data
public class Volume {

    private String source;
    private String target;

    public String getMapping() {
        if (!StringUtils.isBlank(source) && !StringUtils.isBlank(target)) {
            return String.format("%s:%s", source, target);
        }
        return "";
    }

}
