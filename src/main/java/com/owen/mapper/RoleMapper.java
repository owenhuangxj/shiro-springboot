package com.owen.mapper;

import com.owen.entity.Role;
import org.apache.ibatis.annotations.Select;

import java.util.Set;

public interface RoleMapper {
    @Select("select tr.* from t_role tr left join user_role ur on ur.rid = tr.id where ur.uid = #{userId}")
    Set<Role> findRolesByUserId(Integer userId);
}
