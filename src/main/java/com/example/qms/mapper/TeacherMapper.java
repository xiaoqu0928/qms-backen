package com.example.qms.mapper;

import com.example.qms.entity.Teacher;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface TeacherMapper {

    Teacher findByPhone(@Param("phone") String phone);

    Teacher findById(@Param("id") Integer id);

    List<Teacher> findAll();

    int insert(Teacher teacher);

    int updateById(Teacher teacher);

    int deleteById(@Param("id") Integer id);

    int countTeachers();

    // 修改：返回教师ID和科目数，用于后续填充
    List<Map<String, Object>> getTeacherWorkload();
}