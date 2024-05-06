package com.owen.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class BaseEntity implements Serializable {
    private Integer id;
    private String name;
    private String displayName;
}
