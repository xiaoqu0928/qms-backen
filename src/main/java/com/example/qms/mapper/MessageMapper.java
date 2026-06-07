package com.example.qms.mapper;

import com.example.qms.entity.Message;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface MessageMapper {
    int insert(Message message);
    int updateById(Message message);
    Message findById(@Param("id") Integer id);
    List<Message> findByTeacherId(@Param("teacherId") Integer teacherId);
    List<Message> findByStudentId(@Param("studentId") String studentId);
}