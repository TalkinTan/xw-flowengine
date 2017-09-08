package com.xuanwu.flowengine.entity;

import org.activiti.engine.identity.User;

/**
 * 自定义用户类
 * File created  on 2017/3/23.
 *
 * @author jkun
 */
public class CustomUser implements User {

    private String id;
    private String firstName;

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getFirstName() {
        return firstName;
    }

    @Override
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @Override
    public void setLastName(String lastName) {

    }

    @Override
    public String getLastName() {
        return null;
    }

    @Override
    public void setEmail(String email) {

    }

    @Override
    public String getEmail() {
        return null;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public void setPassword(String string) {

    }

    @Override
    public boolean isPictureSet() {
        return false;
    }
}
