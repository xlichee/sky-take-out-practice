package com.sky.mapper;

import com.sky.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {

    @Select("SELECT * FROM user WHERE openid = #{openid} LIMIT 1")
    User getByOpenid(String openid);

    void insert(User user);
}
