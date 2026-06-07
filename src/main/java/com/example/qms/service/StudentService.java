package com.example.qms.service;

import com.example.qms.entity.Student;

import java.util.List;
import java.util.Map;

public interface StudentService {

    Student findByStudentId(String studentId);

    List<Student> listAll();

    int countStudents();

    // 新增：成绩对比数据
    Map<String, Object> getComparison(String studentId, Integer subjectId);

    // 新增：班级成绩分布
    List<Map<String, Object>> getClassDistribution(String studentId, Integer subjectId);
}