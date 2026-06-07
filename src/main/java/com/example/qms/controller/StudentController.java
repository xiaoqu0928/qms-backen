package com.example.qms.controller;

import com.example.qms.dto.Result;
import com.example.qms.entity.Student;
import com.example.qms.service.ScoreService;
import com.example.qms.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/student")
public class StudentController {

    @Autowired
    private ScoreService scoreService;

    @Autowired
    private StudentService studentService;

    @GetMapping("/query")
    public Result<List<Map<String, Object>>> query(@RequestParam String studentId) {
        List<Map<String, Object>> scores = scoreService.queryByStudentId(studentId);
        if (scores.isEmpty()) {
            return Result.error("学号不存在或无成绩");
        }
        return Result.success(scores);
    }

    @GetMapping("/info")
    public Result<Map<String, Object>> getStudentInfo(@RequestParam String studentId) {
        Student student = studentService.findByStudentId(studentId);
        if (student == null) {
            return Result.error("学生不存在");
        }
        Map<String, Object> info = new HashMap<>();
        info.put("name", student.getName());
        info.put("studentId", student.getStudentId());
        info.put("className", student.getClassName());
        info.put("grade", student.getGrade());
        info.put("teacherName", "");
        return Result.success(info);
    }

    // 新增：成绩对比
    @GetMapping("/compare")
    public Result<Map<String, Object>> compare(@RequestParam String studentId, @RequestParam Integer subjectId) {
        Map<String, Object> result = studentService.getComparison(studentId, subjectId);
        if (result == null) {
            return Result.error("学生或科目不存在");
        }
        return Result.success(result);
    }

    // 新增：班级成绩分布饼图数据
    @GetMapping("/class-distribution")
    public Result<List<Map<String, Object>>> classDistribution(@RequestParam String studentId, @RequestParam Integer subjectId) {
        List<Map<String, Object>> distribution = studentService.getClassDistribution(studentId, subjectId);
        if (distribution == null) {
            return Result.error("学生或科目不存在");
        }
        return Result.success(distribution);
    }
}