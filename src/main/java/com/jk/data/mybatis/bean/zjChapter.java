package com.jk.data.mybatis.bean;

/**
 * Created by 76204 on 2017/7/15.
 */
public class zjChapter {
    private String id;
    private String  relateBookId;
    private String  name;
    private String  pid;
    private Integer  knowledgeTag;
    private Integer sortNum;

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

    public Integer getSortNum() {
        return sortNum;
    }

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

    public void setKnowledgeTag(Integer knowledgeTag) {
        this.knowledgeTag = knowledgeTag;
    }

    public void setSortNum(Integer sortNum) {
        this.sortNum = sortNum;
    }

    @Override
    public String toString() {
        return "zjChapter{" +
                "id='" + id + '\'' +
                ", relateBookId='" + relateBookId + '\'' +
                ", name='" + name + '\'' +
                ", pid='" + pid + '\'' +
                ", knowledgeTag=" + knowledgeTag +
                ", sortNum=" + sortNum +
                '}';
    }
}
