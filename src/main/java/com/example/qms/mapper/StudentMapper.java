package com.example.qms.mapper;

import com.example.qms.entity.Student;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface StudentMapper {

    Student findByStudentId(@Param("studentId") String studentId);

    List<Student> findAll();

    int countStudents();

    List<Map<String, Object>> findNeverQueriedStudents();

    // ========== 新增方法 ==========
    /**
     * 插入学生记录
     */
    int insert(Student student);

    /**
     * 根据 ID 更新学生信息（主要用于更新姓名）
     */
    int updateById(Student student);
}