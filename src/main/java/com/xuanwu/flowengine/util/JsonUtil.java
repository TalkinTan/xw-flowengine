package com.xuanwu.flowengine.util;

import com.alibaba.fastjson.JSON;

/**
 * JSON 工具
 * <p>
 * author：Created by ttan on 2017/9/8 0008.
 */
public final class JsonUtil {
    public static String serialize(Object obj) {
        return JSON.toJSONString(obj);
    }

    public static <T> T deserialize(String jsonString, Class<T> clazz) {
        return JSON.parseObject(jsonString, clazz);
    }
}
