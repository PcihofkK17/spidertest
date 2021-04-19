package com.jk.data.mybatis.dao;

import com.jk.data.mybatis.bean.User;
import org.apache.ibatis.annotations.Insert;

/**
 * Created by 76204 on 2017/7/3.
 */
public interface UserDao {
    @Insert("insert into user (`name`) values (#{name})")
    public int add(User user);
}
