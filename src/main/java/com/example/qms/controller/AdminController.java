package com.example.qms.controller;

import com.example.qms.dto.LoginDto;
import com.example.qms.dto.Result;
import com.example.qms.entity.Teacher;
import com.example.qms.service.AdminService;
import com.example.qms.service.StudentService;
import com.example.qms.service.SubjectService;
import com.example.qms.service.TeacherService;
import com.example.qms.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private TeacherService teacherService;

    @Autowired
    private StudentService studentService;

    @Autowired
    private SubjectService subjectService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public Result<Map<String, String>> login(@RequestBody LoginDto dto) {
        if (adminService.login(dto.getPhone(), dto.getPassword()) != null) {
            String token = jwtUtil.generateToken(1, "admin");
            Map<String, String> map = new HashMap<>();
            map.put("token", token);
            return Result.success(map);
        }
        return Result.error("用户名或密码错误");
    }

    // 修改：教师列表，增加科目数
    @GetMapping("/teachers")
    public Result<List<Teacher>> listTeachers() {
        List<Teacher> teachers = teacherService.listAllWithSubjectCount();
        return Result.success(teachers);
    }

    @PostMapping("/teacher")
    public Result<String> addTeacher(@RequestBody Teacher teacher) {
        teacherService.addTeacher(teacher);
        return Result.success("添加成功");
    }

    @PutMapping("/teacher/{id}")
    public Result<String> updateTeacher(@PathVariable Integer id, @RequestBody Teacher teacher) {
        teacher.setId(id);
        teacherService.updateTeacher(teacher);
        return Result.success("修改成功");
    }

    @DeleteMapping("/teacher/{id}")
    public Result<String> deleteTeacher(@PathVariable Integer id) {
        teacherService.deleteTeacher(id);
        return Result.success("删除成功");
    }

    @PostMapping("/teacher/{id}/reset-password")
    public Result<String> resetPassword(@PathVariable Integer id) {
        teacherService.resetPassword(id);
        return Result.success("密码重置成功");
    }

    @GetMapping("/dashboard/stats")
    public Result<Map<String, Object>> dashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("teacherCount", teacherService.listAll().size());
        stats.put("studentCount", studentService.countStudents());
        stats.put("subjectCount", subjectService.countSubjects());
        stats.put("queryCount", adminService.getTotalQueryCount());
        stats.put("completedSubjectCount", 0);
        stats.put("pendingSubjectCount", 0);
        return Result.success(stats);
    }

    // 修改：教师上传进度（真实数据）
    @GetMapping("/teachers/progress")
    public Result<List<Map<String, Object>>> teacherProgress() {
        List<Map<String, Object>> progress = adminService.getTeacherProgress();
        return Result.success(progress);
    }

    // 修改：预警信息（包含从未查询学生TOP5）
    @GetMapping("/warnings")
    public Result<Map<String, List<Map<String, Object>>>> getWarnings() {
        Map<String, List<Map<String, Object>>> result = new HashMap<>();
        result.put("noScoreSubjects", adminService.getNoScoreSubjects());
        result.put("abnormalScores", adminService.getAbnormalScores());
        result.put("inactiveStudents", adminService.getInactiveStudents());
        return Result.success(result);
    }

    @GetMapping("/inactive-students")
    public Result<List<Map<String, Object>>> getInactiveStudents() {
        return Result.success(adminService.getInactiveStudents());
    }

    @GetMapping("/charts/grade-distribution")
    public Result<List<Map<String, Object>>> getGradeDistribution() {
        return Result.success(adminService.getGradeDistribution());
    }

    @GetMapping("/charts/upload-trend")
    public Result<List<Map<String, Object>>> getUploadTrend() {
        return Result.success(adminService.getUploadTrend());
    }

    @GetMapping("/charts/teacher-workload")
    public Result<List<Map<String, Object>>> getTeacherWorkload() {
        return Result.success(adminService.getTeacherWorkload());
    }
}