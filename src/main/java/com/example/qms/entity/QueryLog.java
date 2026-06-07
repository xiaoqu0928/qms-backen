package com.example.qms.entity;

import lombok.Data;
import java.util.Date;

@Data
public class QueryLog {
    private Integer id;
    private String studentId;
    private String studentName;
    private Date queryTime;
    private String ipAddress;
    private String userAgent;
}