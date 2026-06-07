package com.example.qms.entity;

import lombok.Data;
import java.util.Date;

@Data
public class SysConfig {
    private Integer id;
    private String configKey;
    private String configValue;
    private String description;
    private Date updatedAt;
}