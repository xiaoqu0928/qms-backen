package com.example.qms.mapper;

import com.example.qms.entity.QueryLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface QueryLogMapper {
    int insert(QueryLog log);
    List<QueryLog> findByStudentId(@Param("studentId") String studentId);

    // 新增
    int countAll();
}