package com.jk.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 76204 on 2017/6/30.
 */
public class ZBean {

 private String  pk;
 private String  name;
 private List<JBean> jBeans=new ArrayList<JBean>();

    public String getPk() {
        return pk;
    }

    public void setPk(String pk) {
        this.pk = pk;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void addJbean(JBean jBean){
        jBeans.add(jBean);
    }

    public List<JBean> getjBeans() {
        return jBeans;
    }

    public void setjBeans(List<JBean> jBeans) {
        this.jBeans = jBeans;
    }

    @Override
    public String toString() {
        return "ZBean{" +
                "pk='" + pk + '\'' +
                ", name='" + name + '\'' +
                ", jBeans=" + jBeans +
                '}';
    }
}
