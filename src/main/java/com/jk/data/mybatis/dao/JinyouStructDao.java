package com.jk.data.mybatis.dao;

import com.jk.data.mybatis.bean.Chapter;
import com.jk.data.mybatis.bean.JinyouStruct;
import org.apache.ibatis.annotations.Insert;

/**
 * Created by 76204 on 2017/7/12.
 */
public interface JinyouStructDao {
    @Insert("insert ignore  into jinyoustruct (`id`,`course`,`name`,`level`,`pk`,`knowledgeTag`) values (#{id},#{course},#{name},#{level},#{pk},#{knowledgeTag})")
    public int add(JinyouStruct jinyouStruct);
}
