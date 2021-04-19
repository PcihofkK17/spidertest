package com.jk.data.mybatis.dao;

import com.jk.data.mybatis.bean.TikuKnowNum;
import com.jk.data.mybatis.bean.TikuStructBean;
import org.apache.ibatis.annotations.Insert;

/**
 * Created by 76204 on 2017/7/5.
 */
public interface TikuStructBeanDao {
    @Insert("insert  ignore into tikustruct (`courseId`,`id`,`knowledgeName`,`level`,`parentId`,`sortNo`) values (#{courseId},#{id},#{knowledgeName},#{level},#{parentId},#{sortNo})")
    public int add(TikuStructBean tikuStructBean);
}
