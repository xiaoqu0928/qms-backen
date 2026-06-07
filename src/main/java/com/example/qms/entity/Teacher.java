package com.example.qms.entity;

import lombok.Data;
import java.util.Date;

@Data
public class Teacher {

    private Integer id;
    private String phone;
    private String name;
    private String password;
    private String email;
    private Date createdAt;
    private Date updatedAt;
    private Integer status;

    // 非持久化字段，用于前端展示科目数
    private transient Integer subjectCount;
}