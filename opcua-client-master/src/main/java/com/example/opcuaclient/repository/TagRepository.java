package com.example.opcuaclient.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.opcuaclient.entity.Tag;
import org.apache.ibatis.annotations.Mapper;

/**
 * Tag Mapper接口
 */
@Mapper
public interface TagRepository extends BaseMapper<Tag> {
}