package com.owen.mapper;

import com.owen.entity.User;
import org.apache.ibatis.annotations.Select;

public interface UserMapper {
    @Select("select id,name,password,salt from t_user where name = #{name}")
    User findUserByName(String name);
}
