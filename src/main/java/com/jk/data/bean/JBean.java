package com.jk.data.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 76204 on 2017/6/30.
 */
public class JBean {

    private String  pk;
    private String  name;
    private List<JBean> jBeans2=new ArrayList<JBean>();
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

    public void addJBeans2(JBean jBean){
        jBeans2.add(jBean);

    }

    public List<JBean> getjBeans2() {
        return jBeans2;
    }

    public void setjBeans2(List<JBean> jBeans2) {
        this.jBeans2 = jBeans2;
    }
}
