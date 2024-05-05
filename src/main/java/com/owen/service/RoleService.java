package com.owen.service;

import com.owen.entity.Role;

import java.util.Set;

public interface RoleService {
    Set<Role> findRolesByUserId(Integer id);
}
