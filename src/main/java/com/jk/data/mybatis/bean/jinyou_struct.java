package com.jk.data.mybatis.bean;

/**
 * Created by 76204 on 2017/7/4.
 */
public class jinyou_struct {



    private String  id;
    private String  course;
    private String  name;
    private int level;
    private String  pk;
    private int   knowledgeTag;

    public String getId() {
        return id;
    }


    public String getName() {
        return name;
    }

    public int getLevel() {
        return level;
    }

    public String getPk() {
        return pk;
    }



    public void setId(String id) {
        this.id = id;
    }


    public void setName(String name) {
        this.name = name;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public void setPk(String pk) {
        this.pk = pk;
    }

    public void setKnowledgeTag(int knowledgeTag) {
        this.knowledgeTag = knowledgeTag;
    }

    public int getKnowledgeTag() {

        return knowledgeTag;
    }

}
