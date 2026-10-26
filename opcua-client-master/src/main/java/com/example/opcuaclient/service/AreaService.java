package com.example.opcuaclient.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.opcuaclient.entity.Area;
import com.example.opcuaclient.repository.AreaRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 区域业务逻辑服务类
 */
@Slf4j
@Service
public class AreaService extends ServiceImpl<AreaRepository, Area> {
    
    /**
     * 保存区域信息
     * @param area 区域对象
     * @return 保存后的区域对象
     */
    public Area saveArea(Area area) {
        log.info("保存区域信息: {}", area.getAreaName());
        saveOrUpdate(area);
        return area;
    }
    
    /**
     * 批量保存区域信息
     * @param areas 区域对象列表
     * @return 保存后的区域对象列表
     */
    public List<Area> saveAreas(List<Area> areas) {
        log.info("批量保存区域信息，数量: {}", areas.size());
        saveOrUpdateBatch(areas);
        return areas;
    }
    
    /**
     * 根据ID查找区域
     * @param id 区域ID
     * @return 区域对象
     */
    public Area findAreaById(Long id) {
        log.info("根据ID查找区域: {}", id);
        return getById(id);
    }
    
    /**
     * 根据区域编号查找区域
     * @param areaCode 区域编号
     * @return 区域对象
     */
    public Area findAreaByCode(String areaCode) {
        log.info("根据区域编号查找区域: {}", areaCode);
        QueryWrapper<Area> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("area_code", areaCode);
        return getOne(queryWrapper);
    }
    
    /**
     * 获取所有区域
     * @return 区域列表
     */
    public List<Area> findAllAreas() {
        log.info("获取所有区域");
        return list();
    }
    
    /**
     * 删除区域
     * @param id 区域ID
     */
    public void deleteArea(Long id) {
        log.info("删除区域: {}", id);
        removeById(id);
    }
}