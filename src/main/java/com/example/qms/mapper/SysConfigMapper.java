package com.example.qms.mapper;

import com.example.qms.entity.SysConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface SysConfigMapper {
    SysConfig findByKey(@Param("configKey") String configKey);
    int updateByKey(SysConfig config);
}