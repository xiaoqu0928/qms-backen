package com.example.qms.service.impl;

import com.example.qms.entity.Student;
import com.example.qms.mapper.ScoreMapper;
import com.example.qms.mapper.StudentMapper;
import com.example.qms.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class StudentServiceImpl implements StudentService {

    @Autowired
    private StudentMapper studentMapper;

    @Autowired
    private ScoreMapper scoreMapper;

    @Override
    public Student findByStudentId(String studentId) {
        return studentMapper.findByStudentId(studentId);
    }

    @Override
    public List<Student> listAll() {
        return studentMapper.findAll();
    }

    @Override
    public int countStudents() {
        return studentMapper.countStudents();
    }

    @Override
    public Map<String, Object> getComparison(String studentId, Integer subjectId) {
        // 获取学生信息
        Student student = studentMapper.findByStudentId(studentId);
        if (student == null) return null;

        String className = student.getClassName();
        String grade = student.getGrade();

        // 查询该学生成绩
        Map<String, Object> studentScore = scoreMapper.findByStudentIdAndSubjectId(studentId, subjectId);
        if (studentScore == null) return null;
        Double myScore = ((Number) studentScore.get("score")).doubleValue();

        // 班级平均分、班级最高分、班级排名（同一班级、同一科目）
        List<Map<String, Object>> classScores = scoreMapper.getScoresByClassAndSubject(className, subjectId);
        double classAvg = classScores.stream().mapToDouble(m -> ((Number) m.get("score")).doubleValue()).average().orElse(0);
        double classMax = classScores.stream().mapToDouble(m -> ((Number) m.get("score")).doubleValue()).max().orElse(0);
        int rank = (int) classScores.stream().filter(m -> ((Number) m.get("score")).doubleValue() > myScore).count() + 1;

        // 年级平均分
        List<Map<String, Object>> gradeScores = scoreMapper.getScoresByGradeAndSubject(grade, subjectId);
        double gradeAvg = gradeScores.stream().mapToDouble(m -> ((Number) m.get("score")).doubleValue()).average().orElse(0);

        Map<String, Object> result = new HashMap<>();
        result.put("classAvg", Math.round(classAvg * 100) / 100.0);
        result.put("gradeAvg", Math.round(gradeAvg * 100) / 100.0);
        result.put("classRank", rank);
        result.put("classMax", Math.round(classMax * 100) / 100.0);
        return result;
    }

    @Override
    public List<Map<String, Object>> getClassDistribution(String studentId, Integer subjectId) {
        Student student = studentMapper.findByStudentId(studentId);
        if (student == null) return null;

        String className = student.getClassName();
        List<Map<String, Object>> scores = scoreMapper.getScoresByClassAndSubject(className, subjectId);
        if (scores.isEmpty()) return new ArrayList<>();

        // 统计各分数段人数
        Map<String, Integer> dist = new LinkedHashMap<>();
        dist.put("优秀", 0); // >=90
        dist.put("良好", 0); // 80-89
        dist.put("及格", 0); // 60-79
        dist.put("不及格", 0); // <60

        for (Map<String, Object> row : scores) {
            double score = ((Number) row.get("score")).doubleValue();
            if (score >= 90) dist.put("优秀", dist.get("优秀") + 1);
            else if (score >= 80) dist.put("良好", dist.get("良好") + 1);
            else if (score >= 60) dist.put("及格", dist.get("及格") + 1);
            else dist.put("不及格", dist.get("不及格") + 1);
        }

        // 转为前端 ECharts 需要的格式
        List<Map<String, Object>> result = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : dist.entrySet()) {
            Map<String, Object> item = new HashMap<>();
            item.put("name", entry.getKey());
            item.put("value", entry.getValue());
            result.add(item);
        }
        return result;
    }
}