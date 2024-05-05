package com.owen.entity;

import lombok.Data;

@Data
public class User extends BaseEntity {
    private String password;
    private String salt;
}
