package com.example.qms.controller;

import com.example.qms.dto.*;
import com.example.qms.entity.Subject;
import com.example.qms.entity.Teacher;
import com.example.qms.service.SubjectService;
import com.example.qms.service.TeacherService;
import com.example.qms.util.JwtUtil;
import com.example.qms.util.WechatRobotUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/teacher")
public class TeacherController {

    @Autowired
    private TeacherService teacherService;

    @Autowired
    private SubjectService subjectService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private WechatRobotUtil wechatRobotUtil;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    /**
     * 教师注册
     */
    @PostMapping("/register")
    public Result<String> register(@RequestBody RegisterDto dto) {
        teacherService.register(dto);
        return Result.success("注册成功");
    }

    /**
     * 教师登录
     */
    @PostMapping("/login")
    public Result<Map<String, String>> login(@RequestBody LoginDto dto) {
        Teacher teacher = teacherService.login(dto.getPhone(), dto.getPassword());
        if (teacher != null) {
            String token = jwtUtil.generateToken(teacher.getId(), "teacher");
            return Result.success(Map.of("token", token));
        }
        return Result.error("手机号或密码错误");
    }

    /**
     * 获取当前教师个人信息
     */
    @GetMapping("/profile")
    public Result<Teacher> getProfile(HttpServletRequest request) {
        Integer teacherId = (Integer) request.getAttribute("userId");
        return Result.success(teacherService.findById(teacherId));
    }

    /**
     * 获取当前教师的所有科目
     */
    @GetMapping("/subjects")
    public Result<List<Subject>> getSubjects(HttpServletRequest request) {
        Integer teacherId = (Integer) request.getAttribute("userId");
        List<Subject> subjects = subjectService.findByTeacherId(teacherId);
        subjectService.fillStudentCount(subjects); // 填充学生数
        return Result.success(subjects);
    }

    /**
     * 创建科目
     */
    @PostMapping("/subject")
    public Result<String> createSubject(@RequestBody SubjectCreateDto dto, HttpServletRequest request) {
        Integer teacherId = (Integer) request.getAttribute("userId");
        subjectService.create(dto, teacherId);
        return Result.success("创建成功");
    }

    /**
     * 删除科目
     */
    @DeleteMapping("/subject/{id}")
    public Result<String> deleteSubject(@PathVariable Integer id) {
        subjectService.delete(id);
        return Result.success("删除成功");
    }

    /**
     * 上传成绩 CSV 文件
     */
    @PostMapping("/subject/{id}/upload")
    public Result<Map<String, Object>> uploadScores(@PathVariable Integer id,
                                                    @RequestParam("file") MultipartFile file) {
        try {
            Map<String, Object> stats = subjectService.uploadScores(id, file);
            Subject subject = subjectService.findById(id);
            if (subject.getRobotWebhook() != null && !subject.getRobotWebhook().isEmpty()) {
                String content = "【成绩通知】" + subject.getName() + " 成绩已可查询，请访问 " + frontendUrl + " 输入学号查看。";
                wechatRobotUtil.sendMessage(subject.getRobotWebhook(), content);
            }
            return Result.success(stats);
        } catch (IOException e) {
            return Result.error("文件处理失败：" + e.getMessage());
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取科目下的所有成绩（含学生信息）
     */
    @GetMapping("/subject/{id}/scores")
    public Result<List<Map<String, Object>>> getScores(@PathVariable Integer id) {
        return Result.success(subjectService.getScoresWithStudentInfo(id));
    }

    /**
     * 编辑单个成绩
     */
    @PutMapping("/score/{id}")
    public Result<String> updateScore(@PathVariable Integer id, @RequestBody Map<String, String> body) {
        String newScore = body.get("score");
        if (newScore == null) {
            return Result.error("成绩不能为空");
        }
        subjectService.updateScore(id, newScore);
        return Result.success("更新成功");
    }

    /**
     * 手动发送查询链接（通过配置的 webhook）
     */
    @PostMapping("/subject/{id}/send-link")
    public Result<String> sendQueryLink(@PathVariable Integer id) {
        Subject subject = subjectService.findById(id);
        if (subject.getRobotWebhook() == null) {
            return Result.error("未配置机器人Webhook");
        }
        String content = "【成绩查询】" + subject.getName() + " 成绩已可查询，请访问 " + frontendUrl + " 输入学号查看。";
        try {
            wechatRobotUtil.sendMessage(subject.getRobotWebhook(), content);
            return Result.success("发送成功");
        } catch (IOException e) {
            return Result.error("发送失败：" + e.getMessage());
        }
    }

    /**
     * 测试 Webhook 连通性
     */
    @PostMapping("/subject/{id}/test-webhook")
    public Result<String> testWebhook(@PathVariable Integer id) {
        Subject subject = subjectService.findById(id);
        if (subject.getRobotWebhook() == null) {
            return Result.error("未配置机器人Webhook");
        }
        try {
            wechatRobotUtil.sendMessage(subject.getRobotWebhook(), "【测试消息】机器人配置成功！");
            return Result.success("测试消息发送成功");
        } catch (IOException e) {
            return Result.error("发送失败：" + e.getMessage());
        }
    }

    /**
     * 修改教师密码
     */
    @PostMapping("/password")
    public Result<String> changePassword(@RequestBody Map<String, String> body, HttpServletRequest request) {
        Integer teacherId = (Integer) request.getAttribute("userId");
        String oldPassword = body.get("oldPassword");
        String newPassword = body.get("newPassword");
        if (oldPassword == null || newPassword == null) {
            return Result.error("旧密码和新密码不能为空");
        }
        boolean success = teacherService.changePassword(teacherId, oldPassword, newPassword);
        return success ? Result.success("密码修改成功") : Result.error("旧密码错误");
    }
}