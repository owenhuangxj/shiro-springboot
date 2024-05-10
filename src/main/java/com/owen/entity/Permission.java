package com.owen.entity;

public class Permission extends BaseEntity implements org.apache.shiro.authz.Permission {
    @Override
    public boolean implies(org.apache.shiro.authz.Permission permission) {
        if (permission instanceof Permission){
            return true;
        }
        return false;
    }
}
