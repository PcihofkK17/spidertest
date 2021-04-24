package com.jk.data.mybatis.dao;

import com.jk.data.mybatis.bean.CourseRelateUrlValue;
import com.jk.data.mybatis.bean.RbookQuest;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;

/**
 * Created by 76204 on 2017/7/10.
 */
public interface CourceRelateUrlValueDao {
    @Insert("insert ignore  into courseurlvalue (`courseRelateId`,`urlValue`) values (#{courseRelateId},#{urlValue})")
    public int add(CourseRelateUrlValue courseRelateUrlValue);

    @Select("select * from courseurlvalue where  urlValue = #{urlValue}")
    public CourseRelateUrlValue getByUrlValue(String  urlValue);
}
