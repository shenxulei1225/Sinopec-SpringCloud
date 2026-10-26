package com.example.opcuaclient.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.opcuaclient.entity.Area;
import org.springframework.stereotype.Repository;

/**
 * 区域数据访问接口
 */
@Repository
public interface AreaRepository extends BaseMapper<Area> {
}