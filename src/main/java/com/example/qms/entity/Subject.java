package com.example.qms.entity;

import lombok.Data;
import java.util.Date;

@Data
public class Subject {
    private Integer id;
    private Integer teacherId;
    private String name;
    private String semester;
    private String robotWebhook;
    private Date createdAt;
    private Date updatedAt;

    // 非持久化字段，用于前端展示学生数
    private transient Integer studentCount;
}