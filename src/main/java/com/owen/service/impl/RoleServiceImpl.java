package com.owen.service.impl;

import com.owen.entity.Role;
import com.owen.mapper.RoleMapper;
import com.owen.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Set;

@Service
public class RoleServiceImpl implements RoleService {
    @Resource
    RoleMapper roleMapper;
    public Set<Role> findRolesByUserId(Integer id) {
        return roleMapper.findRolesByUserId(id);
    }
}
