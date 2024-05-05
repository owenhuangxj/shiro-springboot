package com.owen.service.impl;

import com.owen.entity.Permission;
import com.owen.mapper.PermissionMapper;
import com.owen.service.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.Set;

@Service
public class PermissionServiceImpl implements PermissionService {
    @Resource
    private PermissionMapper permissionMapper;
    @Override
    public Set<Permission> findPermissionByRoleIds(Collection<Integer> roleIds) {
        return permissionMapper.findByRoleIds(roleIds);
    }
}
