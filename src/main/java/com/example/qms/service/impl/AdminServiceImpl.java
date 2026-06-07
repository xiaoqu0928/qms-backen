package com.example.qms.service.impl;

import com.example.qms.entity.Admin;
import com.example.qms.mapper.*;
import com.example.qms.service.AdminService;
import com.example.qms.util.PasswordEncoderUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private AdminMapper adminMapper;

    @Autowired
    private QueryLogMapper queryLogMapper;

    @Autowired
    private SubjectMapper subjectMapper;

    @Autowired
    private ScoreMapper scoreMapper;

    @Autowired
    private StudentMapper studentMapper;

    @Autowired
    private TeacherMapper teacherMapper;

    @Override
    public Admin login(String username, String password) {
        Admin admin = adminMapper.findByUsername(username);
        if (admin != null && PasswordEncoderUtil.matches(password, admin.getPassword())) {
            return admin;
        }
        return null;
    }

    @Override
    public int getTotalQueryCount() {
        return queryLogMapper.countAll();
    }

    @Override
    public List<Map<String, Object>> getNoScoreSubjects() {
        return subjectMapper.findSubjectsWithoutScores();
    }

    @Override
    public List<Map<String, Object>> getAbnormalScores() {
        return scoreMapper.findAbnormalScores();
    }

    @Override
    public List<Map<String, Object>> getInactiveStudents() {
        return studentMapper.findNeverQueriedStudents();
    }

    @Override
    public List<Map<String, Object>> getGradeDistribution() {
        return scoreMapper.getGradeDistribution();
    }

    @Override
    public List<Map<String, Object>> getUploadTrend() {
        return scoreMapper.getUploadTrend();
    }

    @Override
    public List<Map<String, Object>> getTeacherWorkload() {
        return teacherMapper.getTeacherWorkload();
    }

    @Override
    public List<Map<String, Object>> getTeacherProgress() {
        // 获取所有教师及科目数
        List<Map<String, Object>> teachers = teacherMapper.getTeacherWorkload();
        // 获取每个教师有成绩的科目数（已上传成绩的科目）
        List<Map<String, Object>> uploadedSubjects = subjectMapper.countSubjectsWithScores();

        // 将已上传科目数转为 map
        Map<Integer, Integer> uploadedCountMap = uploadedSubjects.stream()
                .collect(Collectors.toMap(
                        m -> (Integer) m.get("teacherId"),
                        m -> ((Number) m.get("uploadedCount")).intValue()
                ));

        // 组装进度列表
        List<Map<String, Object>> progress = new ArrayList<>();
        for (Map<String, Object> teacher : teachers) {
            Integer teacherId = (Integer) teacher.get("teacherId");
            String teacherName = (String) teacher.get("teacherName");
            int totalSubjects = ((Number) teacher.get("subjectCount")).intValue();
            int uploaded = uploadedCountMap.getOrDefault(teacherId, 0);
            double progressPercent = totalSubjects == 0 ? 0 : (uploaded * 100.0 / totalSubjects);

            Map<String, Object> item = new HashMap<>();
            item.put("teacherName", teacherName);
            item.put("totalSubjects", totalSubjects);
            item.put("uploadedSubjects", uploaded);
            item.put("progress", Math.round(progressPercent * 10) / 10.0); // 保留一位小数
            progress.add(item);
        }
        return progress;
    }
}