package com.example.qms.service.impl;

import com.example.qms.dto.RegisterDto;
import com.example.qms.entity.Teacher;
import com.example.qms.mapper.TeacherMapper;
import com.example.qms.service.TeacherService;
import com.example.qms.util.PasswordEncoderUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TeacherServiceImpl implements TeacherService {

    @Autowired
    private TeacherMapper teacherMapper;

    @Override
    @Transactional
    public void register(RegisterDto dto) {
        Teacher teacher = new Teacher();
        teacher.setPhone(dto.getPhone());
        teacher.setName(dto.getName());
        teacher.setPassword(PasswordEncoderUtil.encode(dto.getPassword()));
        teacher.setEmail(dto.getEmail());
        teacherMapper.insert(teacher);
    }

    @Override
    public Teacher login(String phone, String password) {
        Teacher teacher = teacherMapper.findByPhone(phone);
        if (teacher != null && PasswordEncoderUtil.matches(password, teacher.getPassword())) {
            return teacher;
        }
        return null;
    }

    @Override
    public List<Teacher> listAll() {
        return teacherMapper.findAll();
    }

    @Override
    public void addTeacher(Teacher teacher) {
        teacher.setPassword(PasswordEncoderUtil.encode(teacher.getPassword()));
        teacherMapper.insert(teacher);
    }

    @Override
    public void updateTeacher(Teacher teacher) {
        teacherMapper.updateById(teacher);
    }

    @Override
    public void deleteTeacher(Integer id) {
        teacherMapper.deleteById(id);
    }

    @Override
    public void resetPassword(Integer id) {
        Teacher teacher = teacherMapper.findById(id);
        if (teacher != null) {
            String newPassword = PasswordEncoderUtil.encode("123456");
            teacher.setPassword(newPassword);
            teacherMapper.updateById(teacher);
        }
    }

    @Override
    public Teacher findById(Integer id) {
        return teacherMapper.findById(id);
    }

    @Override
    public boolean changePassword(Integer teacherId, String oldPassword, String newPassword) {
        Teacher teacher = teacherMapper.findById(teacherId);
        if (teacher == null || !PasswordEncoderUtil.matches(oldPassword, teacher.getPassword())) {
            return false;
        }
        teacher.setPassword(PasswordEncoderUtil.encode(newPassword));
        teacherMapper.updateById(teacher);
        return true;
    }

    @Override
    public List<Teacher> listAllWithSubjectCount() {
        List<Teacher> teachers = teacherMapper.findAll();
        if (teachers.isEmpty()) {
            return teachers;
        }
        // 批量获取每位教师的科目数
        List<Map<String, Object>> workload = teacherMapper.getTeacherWorkload();
        Map<Integer, Integer> subjectCountMap = workload.stream()
                .collect(Collectors.toMap(
                        m -> (Integer) m.get("teacherId"),
                        m -> ((Number) m.get("subjectCount")).intValue()
                ));
        // 设置 transient 字段 subjectCount
        for (Teacher teacher : teachers) {
            teacher.setSubjectCount(subjectCountMap.getOrDefault(teacher.getId(), 0));
        }
        return teachers;
    }
}