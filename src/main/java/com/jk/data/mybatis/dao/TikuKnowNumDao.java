package com.jk.data.mybatis.dao;

import com.jk.data.mybatis.bean.TikuKnowNum;
import org.apache.ibatis.annotations.Insert;

/**
 * Created by 76204 on 2017/7/5.
 */
public interface TikuKnowNumDao {
    @Insert("insert  ignore into tikuKnowNum (`id`,`num`,`url`) values (#{id},#{num},#{url})")
    public int add(TikuKnowNum tikuKnowNum);
}
