package com.jk.data.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 76204 on 2017/6/30.
 */
public class StructBean {
    private String  pk;
    private String  name;
    private List<StructBean> sons=new ArrayList<StructBean>();

    public String getPk() {
        return pk;
    }

    public String getName() {
        return name;
    }

    public List<StructBean> getSons() {
        return sons;
    }

    public void setPk(String pk) {
        this.pk = pk;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSons(List<StructBean> sons) {
        this.sons = sons;
    }

    public void addSons(StructBean structBean){
        sons.add(structBean);
    }
}
