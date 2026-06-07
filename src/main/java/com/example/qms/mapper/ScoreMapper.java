package com.example.qms.mapper;

import com.example.qms.entity.Score;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface ScoreMapper {

    List<Score> findBySubjectId(@Param("subjectId") Integer subjectId);

    Score findBySubjectIdAndStudentId(@Param("subjectId") Integer subjectId, @Param("studentId") String studentId);

    Score findById(@Param("id") Integer id);

    int insert(Score score);

    int updateById(Score score);

    int deleteBySubjectId(@Param("subjectId") Integer subjectId);

    List<Score> findAll();

    // 原有新增方法
    List<Map<String, Object>> findAbnormalScores();

    List<Map<String, Object>> getGradeDistribution();

    List<Map<String, Object>> getUploadTrend();

    // ========== 新增方法 ==========
    List<Map<String, Object>> findByStudentId(@Param("studentId") String studentId);

    List<Map<String, Object>> getScoresByClassAndSubject(@Param("className") String className, @Param("subjectId") Integer subjectId);

    List<Map<String, Object>> getScoresByGradeAndSubject(@Param("grade") String grade, @Param("subjectId") Integer subjectId);

    Map<String, Object> findByStudentIdAndSubjectId(@Param("studentId") String studentId, @Param("subjectId") Integer subjectId);
}