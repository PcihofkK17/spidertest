package com.jk.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 76204 on 2017/6/30.
 */
public class JBean {

    private String  pk;
    private String  name;
    private List<ZSBean> zsBeans=new ArrayList<ZSBean>();



    public void addZsBean(ZSBean zsBean){
       zsBeans.add(zsBean);
    }

    public String getPk() {
        return pk;
    }

    public String getName() {
        return name;
    }

    public List<ZSBean> getZsBeans() {
        return zsBeans;
    }

    public void setPk(String pk) {
        this.pk = pk;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setZsBeans(List<ZSBean> zsBeans) {
        this.zsBeans = zsBeans;
    }

    @Override
    public String toString() {
        return "JBean{" +
                "pk='" + pk + '\'' +
                ", name='" + name + '\'' +
                ", zsBeans=" + zsBeans +
                '}';
    }
}
