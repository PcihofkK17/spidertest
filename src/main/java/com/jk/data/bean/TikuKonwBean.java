/**
 * Copyright 2017 bejson.com
 */
package com.jk.data.bean;

import java.util.List;

/**
 * Auto-generated: 2017-07-05 17:0:7
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
public class TikuKonwBean {

    private String message;
    private List<TObject> object;
    private int success;

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setObject(List<TObject> object) {
        this.object = object;
    }

    public List<TObject> getObject() {
        return object;
    }

    public void setSuccess(int success) {
        this.success = success;
    }

    public int getSuccess() {
        return success;
    }

}