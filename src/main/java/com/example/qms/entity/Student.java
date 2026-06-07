package com.example.qms.entity;

import lombok.Data;
import java.util.Date;

@Data
public class Student {
    private Integer id;
    private String studentId;
    private String name;
    private String className;
    private String grade;
    private Date createdAt;
}