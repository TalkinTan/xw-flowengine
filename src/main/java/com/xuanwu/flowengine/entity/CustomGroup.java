package com.xuanwu.flowengine.entity;

import org.activiti.engine.identity.Group;

/**
 * 自定义Group继承类
 *
 * @author jkun
 */
public class CustomGroup implements Group {

    private String id;
    private String name;

    public CustomGroup(String id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getType() {
        return null;
    }

    @Override
    public void setType(String string) {

    }
}
