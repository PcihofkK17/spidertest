package com.jk.data.mybatis.bean;

/**
 * Created by 76204 on 2017/7/7.
 */
public class RbookQuest {
    private String  relateBookId;
    private String  qid;

    public String getRelateBookId() {
        return relateBookId;
    }

    public String getQid() {
        return qid;
    }

    public void setRelateBookId(String relateBookId) {
        this.relateBookId = relateBookId;
    }

    public void setQid(String qid) {
        this.qid = qid;
    }

    @Override
    public String toString() {
        return "RbookQuest{" +
                "relateBookId='" + relateBookId + '\'' +
                ", qid='" + qid + '\'' +
                '}';
    }
}
