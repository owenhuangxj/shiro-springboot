package com.owen.mapper;

import com.owen.entity.Permission;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.Set;

public interface PermissionMapper {
    Set<Permission> findByRoleIds(@Param("roleIds") Collection<Integer> roleIds);
}
