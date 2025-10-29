package com.sky.mapper;

import com.sky.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.Map;

@Mapper
public interface UserMapper {

    @Select("SELECT * FROM user WHERE openid = #{openid} LIMIT 1")
    User getByOpenid(String openid);

    void insert(User user);
    
    @Update("UPDATE user SET name = #{name}, avatar = #{avatar}, sex = #{sex} WHERE id = #{id}")
    void update(User user);

    Integer countByMap(Map map);
}
