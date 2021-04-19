package com.jk.data;

import java.util.List;

/**
 * Created by 76204 on 2017/6/30.
 */
public class ZSBean {
    private String pk;
    private String name;

    public String getPk() {
        return pk;
    }

    public String getName() {
        return name;
    }

    public void setPk(String pk) {
        this.pk = pk;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "ZSBean{" +
                "pk='" + pk + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
