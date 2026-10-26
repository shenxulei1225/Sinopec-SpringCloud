package com.example.opcuaclient.controller;

import com.example.opcuaclient.entity.Area;
import com.example.opcuaclient.service.AreaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 区域管理控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/area")
public class AreaController {
    
    @Autowired
    private AreaService areaService;
    
    /**
     * 创建或更新区域
     * @param area 区域对象
     * @return 区域信息
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createOrUpdateArea(@RequestBody Area area) {
        Map<String, Object> response = new HashMap<>();
        try {
            Area savedArea = areaService.saveArea(area);
            response.put("success", true);
            response.put("data", savedArea);
            response.put("message", "区域保存成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("保存区域失败: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", "保存区域失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * 批量创建或更新区域
     * @param areas 区域对象列表
     * @return 区域信息列表
     */
    @PostMapping("/batch")
    public ResponseEntity<Map<String, Object>> createOrUpdateAreas(@RequestBody List<Area> areas) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Area> savedAreas = areaService.saveAreas(areas);
            response.put("success", true);
            response.put("data", savedAreas);
            response.put("message", "区域批量保存成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("批量保存区域失败: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", "批量保存区域失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * 根据ID获取区域
     * @param id 区域ID
     * @return 区域信息
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getAreaById(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            Area area = areaService.findAreaById(id);
            if (area != null) {
                response.put("success", true);
                response.put("data", area);
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "未找到指定区域");
                return ResponseEntity.status(404).body(response);
            }
        } catch (Exception e) {
            log.error("获取区域失败: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", "获取区域失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * 获取所有区域
     * @return 区域列表
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllAreas() {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Area> areas = areaService.findAllAreas();
            response.put("success", true);
            response.put("data", areas);
            response.put("total", areas.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("获取区域列表失败: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", "获取区域列表失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * 删除区域
     * @param id 区域ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteArea(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            areaService.deleteArea(id);
            response.put("success", true);
            response.put("message", "区域删除成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("删除区域失败: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", "删除区域失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
}