package com.jk.data.mybatis.bean;

/**
 * Created by 76204 on 2017/7/3.
 */
public class RelateBook {
    private String  id;
    private String  siteId;
    private String  gradeId;
    private String  courseId;
    private String  branchId;
    private String  termId;
    private String  bookId;

    public String getId() {
        return id;
    }

    public String getSiteId() {
        return siteId;
    }

    public String getGradeId() {
        return gradeId;
    }

    public String getCourseId() {
        return courseId;
    }

    public String getBranchId() {
        return branchId;
    }

    public String getTermId() {
        return termId;
    }

    public String getBookId() {
        return bookId;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setSiteId(String siteId) {
        this.siteId = siteId;
    }

    public void setGradeId(String gradeId) {
        this.gradeId = gradeId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public void setBranchId(String branchId) {
        this.branchId = branchId;
    }

    public void setTermId(String termId) {
        this.termId = termId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    @Override
    public String toString() {
        return "RelateBook{" +
                "id='" + id + '\'' +
                ", siteId='" + siteId + '\'' +
                ", gradeId='" + gradeId + '\'' +
                ", courseId='" + courseId + '\'' +
                ", branchId='" + branchId + '\'' +
                ", termId='" + termId + '\'' +
                ", bookId='" + bookId + '\'' +
                '}';
    }
}
