package com.jk.data.mybatis.bean;

/**
 * Created by 76204 on 2017/7/5.
 */
public class TikuStructBean {
    private String  courseId;
    private String  id;
    private String  knowledgeName;
    private Integer  level;
    private String  parentId;
    private Integer  sortNo;

    @Override
    public String toString() {
        return "TikuStructBean{" +
                "courseId='" + courseId + '\'' +
                ", id='" + id + '\'' +
                ", knowledgeName='" + knowledgeName + '\'' +
                ", level=" + level +
                ", parentId='" + parentId + '\'' +
                ", sortNo=" + sortNo +
                '}';
    }

    public String getCourseId() {
        return courseId;
    }

    public String getId() {
        return id;
    }

    public String getKnowledgeName() {
        return knowledgeName;
    }

    public Integer getLevel() {
        return level;
    }

    public String getParentId() {
        return parentId;
    }

    public Integer getSortNo() {
        return sortNo;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setKnowledgeName(String knowledgeName) {
        this.knowledgeName = knowledgeName;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public void setSortNo(Integer sortNo) {
        this.sortNo = sortNo;
    }
}
