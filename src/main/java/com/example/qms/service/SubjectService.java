package com.example.qms.service;

import com.example.qms.dto.SubjectCreateDto;
import com.example.qms.entity.Subject;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface SubjectService {
    List<Subject> findByTeacherId(Integer teacherId);
    Subject findById(Integer id);
    void create(SubjectCreateDto dto, Integer teacherId);
    void delete(Integer id);
    Map<String, Object> uploadScores(Integer subjectId, MultipartFile file) throws IOException;
    List<Map<String, Object>> getScoresWithStudentInfo(Integer subjectId);
    void updateScore(Integer scoreId, String newScore);
    int countSubjects();

    // 新增
    void fillStudentCount(List<Subject> subjects);
}