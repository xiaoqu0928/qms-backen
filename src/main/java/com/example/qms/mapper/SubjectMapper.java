package com.example.qms.mapper;

import com.example.qms.entity.Subject;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface SubjectMapper {

    List<Subject> findByTeacherId(@Param("teacherId") Integer teacherId);

    Subject findById(@Param("id") Integer id);

    int insert(Subject subject);

    int updateById(Subject subject);

    int deleteById(@Param("id") Integer id);

    List<Subject> findAll();

    int countSubjects();

    // 原有新增方法
    List<Map<String, Object>> countStudentsBySubjectIds(@Param("subjectIds") List<Integer> subjectIds);

    List<Map<String, Object>> findSubjectsWithoutScores();

    // ========== 新增方法 ==========
    List<Subject> findByIds(@Param("ids") List<Integer> ids);

    List<Map<String, Object>> countSubjectsWithScores();
}