package com.owen.service;

import com.owen.entity.Permission;

import java.util.Collection;
import java.util.Set;

public interface PermissionService {
    Set<Permission> findPermissionByRoleIds(Collection<Integer> roleIds);
}
