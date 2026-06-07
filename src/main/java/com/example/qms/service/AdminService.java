package com.example.qms.service;

import com.example.qms.entity.Admin;

import java.util.List;
import java.util.Map;

public interface AdminService {

    Admin login(String username, String password);

    int getTotalQueryCount();

    List<Map<String, Object>> getNoScoreSubjects();

    List<Map<String, Object>> getAbnormalScores();

    List<Map<String, Object>> getInactiveStudents();

    List<Map<String, Object>> getGradeDistribution();

    List<Map<String, Object>> getUploadTrend();

    List<Map<String, Object>> getTeacherWorkload();

    // 新增：获取教师上传进度（真实数据）
    List<Map<String, Object>> getTeacherProgress();
}