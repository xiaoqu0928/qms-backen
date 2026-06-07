package com.example.qms.entity;

import lombok.Data;
import java.util.Date;

@Data
public class Message {
    private Integer id;
    private String studentId;
    private Integer teacherId;
    private String content;
    private String reply;
    private Integer status;
    private Date createdAt;
    private Date repliedAt;
}