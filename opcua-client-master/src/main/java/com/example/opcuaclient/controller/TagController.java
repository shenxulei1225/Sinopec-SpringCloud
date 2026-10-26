package com.example.opcuaclient.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.opcuaclient.entity.Plc;
import com.example.opcuaclient.entity.Tag;
import com.example.opcuaclient.service.PlcService;
import com.example.opcuaclient.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import org.springframework.web.multipart.MultipartFile;
import com.example.opcuaclient.util.ExcelParser;
import java.util.ArrayList;

/**
 * Tag控制器类
 */
@RestController
@RequestMapping("/api/tag")
public class TagController {
    
    @Autowired
    private TagService tagService;
    
    @Autowired
    private PlcService plcService;
    
    /**
     * 创建或更新Tag记录
     * @param tag Tag对象
     * @return 保存后的Tag对象
     */
    @PostMapping
    public Tag saveTag(@RequestBody Tag tag) {
        tagService.saveOrUpdate(tag);
        return tag;
    }
    
    /**
     * 根据ID获取Tag记录
     * @param id Tag ID
     * @return Tag对象
     */
    @GetMapping("/{id}")
    public Tag getTagById(@PathVariable Long id) {
        return tagService.getById(id);
    }
    
    /**
     * 获取所有Tag记录
     * @return Tag对象列表
     */
    @GetMapping
    public List<Tag> getAllTags() {
        return tagService.list();
    }
    
    /**
     * 分页获取Tag记录
     * @param page 页码
     * @param size 每页大小
     * @return 分页结果
     */
    @GetMapping("/page")
    public Page<Tag> getTagPage(@RequestParam(defaultValue = "1") Integer page, 
                               @RequestParam(defaultValue = "10") Integer size) {
        return tagService.page(new Page<>(page, size));
    }
    
    /**
     * 根据ID删除Tag记录
     * @param id Tag ID
     * @return 是否删除成功
     */
    @DeleteMapping("/{id}")
    public boolean deleteTag(@PathVariable Long id) {
        return tagService.removeById(id);
    }
    
    /**
     * 批量上传Excel文件创建Tag记录
     * @param areaCode 区域编码
     * @param files Excel文件列表
     * @return 是否上传成功
     */
    @PostMapping("/batch-upload")
    public boolean batchUploadTags(@RequestParam("areaCode") String areaCode,
                                  @RequestParam("files") MultipartFile[] files) {
        try {
            List<Tag> allTags = new ArrayList<>();
            
            // 解析所有上传的Excel文件
            for (MultipartFile file : files) {
                List<Tag> tags = ExcelParser.parseTagExcel(file, areaCode);
                allTags.addAll(tags);
            }
            
            // 批量保存Tag记录（使用事务）
            boolean tagSaved = tagService.saveTagsBatch(allTags);
            
            // 获取并保存PLC记录
            Set<Plc> allPlcs = ExcelParser.parsePlcFromTagExcel(files, areaCode);
            boolean plcSaved = plcService.savePlcsBatch(new ArrayList<>(allPlcs));
            
            return tagSaved && plcSaved;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}