package com.example.qms.service.impl;

import com.example.qms.entity.Student;
import com.example.qms.entity.Subject;
import com.example.qms.mapper.QueryLogMapper;
import com.example.qms.mapper.ScoreMapper;
import com.example.qms.mapper.StudentMapper;
import com.example.qms.mapper.SubjectMapper;
import com.example.qms.service.ScoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ScoreServiceImpl implements ScoreService {

    @Autowired
    private ScoreMapper scoreMapper;

    @Autowired
    private StudentMapper studentMapper;

    @Autowired
    private SubjectMapper subjectMapper;

    @Autowired
    private QueryLogMapper queryLogMapper;

    @Override
    public List<Map<String, Object>> queryByStudentId(String studentId) {
        // 1. 验证学生是否存在
        Student student = studentMapper.findByStudentId(studentId);
        if (student == null) {
            return Collections.emptyList();
        }

        // 2. 查询该学生的所有成绩（含科目ID）
        List<Map<String, Object>> scoreRows = scoreMapper.findByStudentId(studentId);
        if (scoreRows.isEmpty()) {
            return Collections.emptyList();
        }

        // 3. 收集科目ID并批量查询科目名称（避免循环查询）
        Set<Integer> subjectIds = scoreRows.stream()
                .map(row -> (Integer) row.get("subject_id"))
                .collect(Collectors.toSet());
        List<Subject> subjects = subjectMapper.findByIds(new ArrayList<>(subjectIds));
        Map<Integer, String> subjectNameMap = subjects.stream()
                .collect(Collectors.toMap(Subject::getId, Subject::getName));

        // 4. 组装返回结果
        return scoreRows.stream().map(row -> {
            Map<String, Object> map = new HashMap<>();
            map.put("subjectName", subjectNameMap.get(row.get("subject_id")));
            map.put("score", row.get("score"));
            map.put("studentName", student.getName());
            map.put("studentId", student.getStudentId());
            return map;
        }).collect(Collectors.toList());
    }

    @Override
    public void recordQueryLog(String studentId, String studentName, String ip, String userAgent) {
        // 可在此记录查询日志，若需要则实现
        // 本方法留空，可在 StudentController 中调用
    }
}