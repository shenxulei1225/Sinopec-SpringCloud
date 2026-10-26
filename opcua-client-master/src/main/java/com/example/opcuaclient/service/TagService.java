package com.example.opcuaclient.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.opcuaclient.entity.Tag;
import com.example.opcuaclient.repository.TagRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

/**
 * Tag服务类
 */
@Service
public class TagService extends ServiceImpl<TagRepository, Tag> {
    
    /**
     * 批量保存Tag记录（带事务）
     * @param tags Tag记录列表
     * @return 是否保存成功
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean saveTagsBatch(List<Tag> tags) {
        return this.saveBatch(tags);
    }
}