package com.example.qms.service;

import java.util.List;
import java.util.Map;

public interface ScoreService {
    List<Map<String, Object>> queryByStudentId(String studentId);
    void recordQueryLog(String studentId, String studentName, String ip, String userAgent);
}