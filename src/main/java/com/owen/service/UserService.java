package com.owen.service;

import com.owen.entity.User;

public interface UserService {
    User findUserByName(String name);
}
