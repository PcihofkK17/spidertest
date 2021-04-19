package com.jk.data.mybatis.dao;

import com.jk.data.mybatis.bean.TiKuTermCourse;
import com.jk.data.mybatis.bean.TikuStructBean;
import org.apache.ibatis.annotations.Insert;

/**
 * Created by 76204 on 2017/7/5.
 */
public interface TikuTermCourseDao {
    @Insert("insert  ignore into tiKuTermCourse (`term`,`course`,`courseId`) values (#{term},#{course},#{courseId})")
    public int add(TiKuTermCourse tiKuTermCourse);
}
