package com.jk.data.mybatis.dao;

import com.jk.data.mybatis.bean.Chapter;
import com.jk.data.mybatis.bean.RelateBook;
import org.apache.ibatis.annotations.Insert;

/**
 * Created by 76204 on 2017/7/4.
 */
public interface ChapterDao {
    @Insert("insert ignore  into chapter (`id`,`relateBookId`,`name`,`level`,`pk`,`knowledgeTag`) values (#{id},#{relateBookId},#{name},#{level},#{pk},#{knowledgeTag})")
    public int add(Chapter chapter);
}
