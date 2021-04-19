package com.jk.data.mybatis.dao;

import com.jk.data.mybatis.bean.Chapter;
import com.jk.data.mybatis.bean.RbookQuest;
import org.apache.ibatis.annotations.Insert;

/**
 * Created by 76204 on 2017/7/7.
 */
public interface RbookQuestDao {
    @Insert("insert ignore  into relateBook_quest (`relateBookId`,`qid`) values (#{relateBookId},#{qid})")
    public int add(RbookQuest rbookQuest);
}
