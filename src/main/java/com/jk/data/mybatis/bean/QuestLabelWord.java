package com.jk.data.mybatis.bean;

/**
 * Created by 76204 on 2017/7/4.
 */
public class QuestLabelWord {
   private String  id;
   private String  name;
   private String  type;
   private String  value;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
