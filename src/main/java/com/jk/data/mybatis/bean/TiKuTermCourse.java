package com.jk.data.mybatis.bean;

/**
 * Created by 76204 on 2017/7/5.
 */
public class TiKuTermCourse {
    private String  term;
    private String  course;
    private String  courseId;

    public String getTerm() {
        return term;
    }

    public String getCourse() {
        return course;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getCourseId() {
        return courseId;
    }
}
