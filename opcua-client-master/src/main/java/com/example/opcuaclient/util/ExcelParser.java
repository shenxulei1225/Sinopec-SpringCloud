package com.example.opcuaclient.util;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.alibaba.excel.util.ListUtils;
import com.example.opcuaclient.entity.Tag;
import com.example.opcuaclient.entity.Plc;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

public class ExcelParser {
    
    public static List<Plc> parsePlcExcel(MultipartFile file, String areaCode) throws Exception {
        try (InputStream inputStream = file.getInputStream()) {
            // 创建监听器
            PlcDataListener listener = new PlcDataListener(areaCode);
            
            // 读取Excel文件
            EasyExcel.read(inputStream, PlcExcelData.class, listener).sheet().doRead();
            
            // 返回解析后的PLC列表
            return listener.getPlcs();
        }
    }
    
    /**
     * 解析Tag Excel文件，并同时解析PLC数据
     * @param file Excel文件
     * @param areaCode 区域代码
     * @return Tag列表
     * @throws Exception 文件读取异常
     */
    public static List<Tag> parseTagExcel(MultipartFile file, String areaCode) throws Exception {
        try (InputStream inputStream = file.getInputStream()) {
            // 创建监听器
            TagDataListener listener = new TagDataListener(areaCode);
            
            // 读取Excel文件
            EasyExcel.read(inputStream, TagExcelData.class, listener).sheet().doRead();
            
            // 返回解析后的Tag列表
            return listener.getTags();
        }
    }
    
    /**
     * 获取解析Tag Excel文件时同时解析出的PLC数据
     * @param file Excel文件
     * @param areaCode 区域代码
     * @return PLC列表
     * @throws Exception 文件读取异常
     */
    public static List<Plc> parsePlcFromTagExcel(MultipartFile file, String areaCode) throws Exception {
        try (InputStream inputStream = file.getInputStream()) {
            // 创建监听器
            TagDataListener listener = new TagDataListener(areaCode);
            
            // 读取Excel文件
            EasyExcel.read(inputStream, TagExcelData.class, listener).sheet().doRead();
            
            // 返回解析后的PLC列表
            return listener.getPlcs();
        }
    }
    
    /**
     * 从多个Tag Excel文件中解析出PLC数据
     * @param files Excel文件数组
     * @param areaCode 区域代码
     * @return PLC集合
     * @throws Exception 文件读取异常
     */
    public static Set<Plc> parsePlcFromTagExcel(MultipartFile[] files, String areaCode) throws Exception {
        Set<Plc> allPlcs = new HashSet<>();
        
        // 遍历所有文件
        for (MultipartFile file : files) {
            try (InputStream inputStream = file.getInputStream()) {
                // 创建监听器
                TagDataListener listener = new TagDataListener(areaCode);
                
                // 读取Excel文件
                EasyExcel.read(inputStream, TagExcelData.class, listener).sheet().doRead();
                
                // 将解析出的PLC数据添加到集合中
                allPlcs.addAll(listener.getPlcs());
            }
        }
        
        return allPlcs;
    }
    
    /**
     * 解析PLC Excel文件
     * @param file Excel文件
     * @param areaCode 区域代码
     * @return PLC列表
     * @throws Exception 文件读取异常
     */
    public static List<Plc> parsePlcExcelFromFile(MultipartFile file, String areaCode) throws Exception {
        try (InputStream inputStream = file.getInputStream()) {
            // 创建PLC监听器
            PlcDataListener plcListener = new PlcDataListener(areaCode);
            
            // 读取Excel文件
            EasyExcel.read(inputStream, PlcExcelData.class, plcListener).sheet().doRead();
            
            // 返回解析后的PLC列表
            return plcListener.getPlcs();
        }
    }
    
    /**
     * PLC Excel数据类，用于EasyExcel映射
     * 映射关系：
     * Excel列 -> PLC对象属性
     * "分区" -> plcPartition
     * "PLC地址" -> ip
     */
    public static class PlcExcelData {
        @com.alibaba.excel.annotation.ExcelProperty("分区")
        private String plcPartition;
        
        @com.alibaba.excel.annotation.ExcelProperty("PLC地址")
        private String ip;
        
        // Getters and setters
        public String getPlcPartition() { return plcPartition; }
        public void setPlcPartition(String plcPartition) { this.plcPartition = plcPartition; }
        
        public String getIp() { return ip; }
        public void setIp(String ip) { this.ip = ip; }
    }
    
    /**
     * Tag Excel数据类，用于EasyExcel映射
     * 映射关系：
     * Excel列 -> Tag对象属性
     * "分区" -> plcPartition
     * "标签名" -> tagName
     * "描述" -> tagDescription
     * "读写方式" -> readWriteMode
     */
    public static class TagExcelData {
        @com.alibaba.excel.annotation.ExcelProperty("分区")
        private String plcPartition;
        
        @com.alibaba.excel.annotation.ExcelProperty("标签名")
        private String tagName;
        
        @com.alibaba.excel.annotation.ExcelProperty("描述")
        private String tagDescription;
        
        @com.alibaba.excel.annotation.ExcelProperty("读写方式")
        private String readWriteMode;
        
        // Getters and setters
        public String getPlcPartition() { return plcPartition; }
        public void setPlcPartition(String plcPartition) { this.plcPartition = plcPartition; }
        
        public String getTagName() { return tagName; }
        public void setTagName(String tagName) { this.tagName = tagName; }
        
        public String getTagDescription() { return tagDescription; }
        public void setTagDescription(String tagDescription) { this.tagDescription = tagDescription; }
        
        public String getReadWriteMode() { return readWriteMode; }
        public void setReadWriteMode(String readWriteMode) { this.readWriteMode = readWriteMode; }
    }
    
    /**
     * PLC数据监听器
     */
    public static class PlcDataListener implements ReadListener<PlcExcelData> {
        private final String areaCode;
        private final List<Plc> plcs = ListUtils.newArrayList();
        
        public PlcDataListener(String areaCode) {
            this.areaCode = areaCode;
        }
        
        @Override
        public void invoke(PlcExcelData data, AnalysisContext context) {
            // 过滤掉分区为空的数据
            if (data.getPlcPartition() == null || data.getPlcPartition().trim().isEmpty()) {
                return;
            }
            
            // 将Excel数据转换为Plc对象
            Plc plc = new Plc();
            plc.setAreaCode(areaCode);
            plc.setPlcPartition(data.getPlcPartition());
            // plc_code为分区与_ACU的拼接
            plc.setPlcCode(data.getPlcPartition() + "_ACU");
            plc.setIp(data.getIp());
            
            plcs.add(plc);
        }
        
        @Override
        public void doAfterAllAnalysed(AnalysisContext context) {
            // 所有数据解析完成后的操作
        }
        
        public List<Plc> getPlcs() {
            return plcs;
        }
    }
    
    /**
     * Tag数据监听器
     */
    public static class TagDataListener implements ReadListener<TagExcelData> {
        private final String areaCode;
        private final List<Tag> tags = ListUtils.newArrayList();
        private final List<Plc> plcs = ListUtils.newArrayList();
        // 用于跟踪已处理的分区，避免重复添加PLC数据
        private final java.util.Set<String> processedPartitions = new java.util.HashSet<>();
        
        public TagDataListener(String areaCode) {
            this.areaCode = areaCode;
        }
        
        @Override
        public void invoke(TagExcelData data, AnalysisContext context) {
            // 过滤掉标签名为空的数据
            if (data.getTagName() == null || data.getTagName().trim().isEmpty()) {
                return;
            }
            
            // 将Excel数据转换为Tag对象
            Tag tag = new Tag();
            tag.setAreaCode(areaCode);
            tag.setPlcPartition(data.getPlcPartition());
            // 使用分区作为PLC编码+_ACU的拼接
            tag.setPlcCode(data.getPlcPartition()+"_ACU");
            tag.setTagName(data.getTagName());
            tag.setTagDescription(data.getTagDescription());
            
            // 处理读写方式：如果Excel中未指定，则默认为0（读取）
            // 如果是字符串"read"或"write"，则转换为对应的数值
            if (data.getReadWriteMode() != null) {
                String mode = data.getReadWriteMode().toLowerCase().trim();
                if ("read".equals(mode)) {
                    tag.setReadWriteMode(1);
                } else if ("write".equals(mode)) {
                    tag.setReadWriteMode(2);
                } else {
                    // 尝试解析为整数
                    try {
                        tag.setReadWriteMode(Integer.parseInt(mode));
                    } catch (NumberFormatException e) {
                        // 如果无法解析，则默认为0（读取）
                        tag.setReadWriteMode(0);
                    }
                }
            } else {
                tag.setReadWriteMode(0); // 默认为读取
            }
            
            tags.add(tag);
            
            // 同时处理PLC数据
            // 检查分区是否已处理，避免重复添加
            if (data.getPlcPartition() != null && !data.getPlcPartition().trim().isEmpty() 
                && !processedPartitions.contains(data.getPlcPartition().trim())) {
                
                // 将Excel数据转换为Plc对象
                Plc plc = new Plc();
                plc.setAreaCode(areaCode);
                plc.setPlcPartition(data.getPlcPartition().trim());
                // plc_code为分区与_ACU的拼接
                plc.setPlcCode(data.getPlcPartition().trim() + "_ACU");
                // ip为分区（根据用户说明）
                plc.setIp(data.getPlcPartition().trim());
                
                plcs.add(plc);
                processedPartitions.add(data.getPlcPartition().trim());
            }
        }
        
        @Override
        public void doAfterAllAnalysed(AnalysisContext context) {
            // 所有数据解析完成后的操作
        }
        
        public List<Tag> getTags() {
            return tags;
        }
        
        public List<Plc> getPlcs() {
            return plcs;
        }
    }
}