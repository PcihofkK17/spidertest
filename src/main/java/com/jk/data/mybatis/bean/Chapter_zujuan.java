package com.jk.data.mybatis.bean;

/**
 * Created by 76204 on 2017/7/15.
 */
public class Chapter_zujuan {
    private String id;
    private String  relateBookId;
    private String  name;
    private String  pid;
    private Integer  knowledgeTag;
    private Integer sortNum;

    public void setId(String id) {
        this.id = id;
    }

    public void setRelateBookId(String relateBookId) {
        this.relateBookId = relateBookId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }



    public String getId() {

        return id;
    }

    public String getRelateBookId() {
        return relateBookId;
    }

    public String getName() {
        return name;
    }

    public String getPid() {
        return pid;
    }

    public Integer getKnowledgeTag() {
        return knowledgeTag;
    }

    public void setKnowledgeTag(Integer knowledgeTag) {
        this.knowledgeTag = knowledgeTag;
    }

    @Override
    public String toString() {
        return "Chapter_zujuan{" +
                "id='" + id + '\'' +
                ", relateBookId='" + relateBookId + '\'' +
                ", name='" + name + '\'' +
                ", pid='" + pid + '\'' +
                ", knowledgeTag=" + knowledgeTag +
                ", sortNum=" + sortNum +
                '}';
    }

    public void setSortNum(Integer sortNum) {
        this.sortNum = sortNum;
    }

    public Integer getSortNum() {

        return sortNum;
    }
}
