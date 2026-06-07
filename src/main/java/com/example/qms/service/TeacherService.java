package com.example.qms.service;

import com.example.qms.dto.RegisterDto;
import com.example.qms.entity.Teacher;

import java.util.List;

public interface TeacherService {

    void register(RegisterDto dto);

    Teacher login(String phone, String password);

    List<Teacher> listAll();

    void addTeacher(Teacher teacher);

    void updateTeacher(Teacher teacher);

    void deleteTeacher(Integer id);

    void resetPassword(Integer id);

    Teacher findById(Integer id);

    boolean changePassword(Integer teacherId, String oldPassword, String newPassword);

    // 新增：获取所有教师并附带科目数
    List<Teacher> listAllWithSubjectCount();
}