package com.example.qms.mapper;

import com.example.qms.entity.OperationLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface OperationLogMapper {
    int insert(OperationLog log);
    List<OperationLog> findByOperator(@Param("operatorId") Integer operatorId, @Param("operatorType") String operatorType);
}