package com.owen.service.impl;

import com.owen.entity.User;
import com.owen.mapper.UserMapper;
import com.owen.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class UserServiceImpl implements UserService {
    @Resource
    private UserMapper userMapper;

    public User findUserByName(String name) {
        return userMapper.findUserByName(name);
    }
}
