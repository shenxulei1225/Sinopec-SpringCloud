package com.example.opcuaclient.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.opcuaclient.entity.Plc;
import org.apache.ibatis.annotations.Mapper;

/**
 * PLC Mapper接口
 */
@Mapper
public interface PlcRepository extends BaseMapper<Plc> {
}