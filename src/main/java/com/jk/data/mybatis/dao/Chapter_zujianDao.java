package com.jk.data.mybatis.dao;

import com.jk.data.mybatis.bean.zjChapter;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * Created by 76204 on 2017/7/15.
 */
public interface Chapter_zujianDao {
    @Insert("insert ignore  into chapter_zujuan (`id`,`relateBookId`,`name`,`pid`,`knowledgeTag`,`sortNum`) values (#{id},#{relateBookId},#{name},#{pid},#{knowledgeTag},#{sortNum})")
    public int add(zjChapter chapter);

    @Select("Select * from chapter_zujuan")
    public List<zjChapter> getAll();

    @Select("Select * from chapter_zujuan  where id=#{pid}")
    public zjChapter getByPid(String  pid);
}
