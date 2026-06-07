package com.example.qms.entity;

import lombok.Data;
import java.util.Date;

@Data
public class OperationLog {
    private Integer id;
    private Integer operatorId;
    private String operatorType;
    private String operation;
    private String content;
    private String ipAddress;
    private Date createdAt;
}