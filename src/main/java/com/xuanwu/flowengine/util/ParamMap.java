package com.xuanwu.flowengine.util;

import java.util.HashMap;

/**
 * Created by jkun on 2017/3/15.
 */
public class ParamMap extends HashMap<String, Object> {

    private static final long serialVersionUID = -298024471557969288L;

    public static ParamMap getInstance() {

        return new ParamMap();

    }

    public ParamMap add(String key, Object value) {
        if (value == null) {
            return this;
        }
        if (value instanceof String && value.equals("")) {
            return this;
        }
        super.put(key, value);
        return this;
    }

    @Override
    public Object put(String key, Object value) {
        if (value == null) {
            return this;
        }
        if (value instanceof String && value.equals("")) {
            return this;
        }
        return super.put(key, value);
    }
}
