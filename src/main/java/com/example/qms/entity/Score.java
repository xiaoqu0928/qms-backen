package com.example.qms.entity;

import lombok.Data;
import java.util.Date;

@Data
public class Score {
    private Integer id;
    private Integer subjectId;
    private String studentId;
    private String studentName;
    private String score;
    private Date updatedAt;
}