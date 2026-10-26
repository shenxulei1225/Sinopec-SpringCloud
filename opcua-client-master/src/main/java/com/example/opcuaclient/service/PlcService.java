package com.example.opcuaclient.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.opcuaclient.entity.Plc;
import com.example.opcuaclient.repository.PlcRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

/**
 * PLC服务类
 */
@Service
public class PlcService extends ServiceImpl<PlcRepository, Plc> {
    
    /**
     * 批量保存PLC记录（带事务）
     * @param plcs PLC记录列表
     * @return 是否保存成功
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean savePlcsBatch(List<Plc> plcs) {
        return this.saveBatch(plcs);
    }
}