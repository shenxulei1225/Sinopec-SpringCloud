package cn.cheers.x.module.dynamicbusiness.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entitytype.PhysicalColumnConfig;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 * 物理列映射工具类
 * 
 * <p>提供物理列映射配置的解析、序列化和验证功能。</p>
 * 
 * <h3>主要功能</h3>
 * <ul>
 *   <li>解析 JSON 字符串为物理列映射配置</li>
 *   <li>将物理列映射配置序列化为 JSON 字符串</li>
 *   <li>验证物理列映射配置的有效性</li>
 *   <li>提供便捷的配置构建方法</li>
 * </ul>
 * 
 * <h3>JSON 格式示例</h3>
 * <pre>
 * {
 *   "manufacturer": {"column": "manufacturer", "type": "VARCHAR", "length": 200},
 *   "power": {"column": "power", "type": "DECIMAL", "precision": 18, "scale": 4},
 *   "install_date": {"column": "install_date", "type": "DATE"}
 * }
 * </pre>
 * 
 * @author 基础服务模块
 * @see PhysicalColumnConfig
 */
@Slf4j
public final class PhysicalColumnMappingUtils {

    private PhysicalColumnMappingUtils() {
        // 工具类，禁止实例化
    }

    // ==================== 解析方法 ====================

    /**
     * 解析 JSON 字符串为物理列映射配置
     * 
     * <p>将 JSON 格式的配置字符串解析为 Map，key 为字段编码，value 为列配置。</p>
     * 
     * @param json JSON 字符串
     * @return 物理列映射配置，如果输入为空或解析失败返回空 Map
     */
    public static Map<String, PhysicalColumnConfig> parseMapping(String json) {
        if (StrUtil.isBlank(json)) {
            return Collections.emptyMap();
        }
        
        try {
            JSONObject jsonObject = JSONUtil.parseObj(json);
            Map<String, PhysicalColumnConfig> result = new LinkedHashMap<>();
            
            for (String fieldCode : jsonObject.keySet()) {
                JSONObject configObj = jsonObject.getJSONObject(fieldCode);
                if (configObj == null) {
                    log.warn("字段 {} 的配置为空，跳过", fieldCode);
                    continue;
                }
                
                PhysicalColumnConfig config = parseColumnConfig(configObj);
                if (config != null) {
                    result.put(fieldCode, config);
                } else {
                    log.warn("字段 {} 的配置解析失败，跳过", fieldCode);
                }
            }
            
            return result;
        } catch (Exception e) {
            log.error("解析物理列映射配置失败: {}", json, e);
            return Collections.emptyMap();
        }
    }

    /**
     * 解析 DO 中已反序列化的物理列映射（String 或 Map）
     */
    public static Map<String, PhysicalColumnConfig> parseMapping(Object value) {
        if (value == null) {
            return Collections.emptyMap();
        }
        if (value instanceof String text) {
            return parseMapping(text);
        }
        return parseMapping(JSONUtil.toJsonStr(value));
    }

    /**
     * 解析单个列配置
     * 
     * @param configObj JSON 对象
     * @return 列配置，解析失败返回 null
     */
    private static PhysicalColumnConfig parseColumnConfig(JSONObject configObj) {
        try {
            PhysicalColumnConfig config = new PhysicalColumnConfig();
            config.setColumn(configObj.getStr("column"));
            config.setType(configObj.getStr("type"));
            config.setLength(configObj.getInt("length"));
            config.setPrecision(configObj.getInt("precision"));
            config.setScale(configObj.getInt("scale"));
            return config;
        } catch (Exception e) {
            log.error("解析列配置失败: {}", configObj, e);
            return null;
        }
    }

    // ==================== 序列化方法 ====================

    /**
     * 将物理列映射配置序列化为 JSON 字符串
     * 
     * @param mapping 物理列映射配置
     * @return JSON 字符串，如果输入为空返回 null
     */
    public static String toJson(Map<String, PhysicalColumnConfig> mapping) {
        if (CollUtil.isEmpty(mapping)) {
            return null;
        }
        
        try {
            JSONObject result = new JSONObject(true); // 保持插入顺序
            
            for (Map.Entry<String, PhysicalColumnConfig> entry : mapping.entrySet()) {
                String fieldCode = entry.getKey();
                PhysicalColumnConfig config = entry.getValue();
                
                if (config == null) {
                    continue;
                }
                
                JSONObject configObj = new JSONObject(true);
                configObj.set("column", config.getColumn());
                configObj.set("type", config.getType());
                
                // 只输出有值的可选字段
                if (config.getLength() != null) {
                    configObj.set("length", config.getLength());
                }
                if (config.getPrecision() != null) {
                    configObj.set("precision", config.getPrecision());
                }
                if (config.getScale() != null) {
                    configObj.set("scale", config.getScale());
                }
                
                result.set(fieldCode, configObj);
            }
            
            return result.toString();
        } catch (Exception e) {
            log.error("序列化物理列映射配置失败", e);
            return null;
        }
    }

    /**
     * 将物理列映射配置序列化为格式化的 JSON 字符串
     * 
     * @param mapping 物理列映射配置
     * @return 格式化的 JSON 字符串，如果输入为空返回 null
     */
    public static String toJsonPretty(Map<String, PhysicalColumnConfig> mapping) {
        if (CollUtil.isEmpty(mapping)) {
            return null;
        }
        
        try {
            JSONObject result = new JSONObject(true);
            
            for (Map.Entry<String, PhysicalColumnConfig> entry : mapping.entrySet()) {
                String fieldCode = entry.getKey();
                PhysicalColumnConfig config = entry.getValue();
                
                if (config == null) {
                    continue;
                }
                
                JSONObject configObj = new JSONObject(true);
                configObj.set("column", config.getColumn());
                configObj.set("type", config.getType());
                
                if (config.getLength() != null) {
                    configObj.set("length", config.getLength());
                }
                if (config.getPrecision() != null) {
                    configObj.set("precision", config.getPrecision());
                }
                if (config.getScale() != null) {
                    configObj.set("scale", config.getScale());
                }
                
                result.set(fieldCode, configObj);
            }
            
            return result.toStringPretty();
        } catch (Exception e) {
            log.error("序列化物理列映射配置失败", e);
            return null;
        }
    }

    // ==================== 验证方法 ====================

    /**
     * 验证物理列映射配置的有效性
     * 
     * <p>检查所有配置项是否有效，包括：</p>
     * <ul>
     *   <li>字段编码不能为空</li>
     *   <li>列名不能为空且格式正确</li>
     *   <li>数据类型必须支持</li>
     *   <li>VARCHAR 长度有效</li>
     *   <li>DECIMAL 精度和小数位有效</li>
     *   <li>列名不能重复</li>
     * </ul>
     * 
     * @param mapping 物理列映射配置
     * @return 验证结果，包含所有错误信息
     */
    public static ValidationResult validateMapping(Map<String, PhysicalColumnConfig> mapping) {
        ValidationResult result = new ValidationResult();
        
        if (CollUtil.isEmpty(mapping)) {
            // 空配置是有效的（表示不使用物理列）
            return result;
        }
        
        Set<String> usedColumnNames = new HashSet<>();
        
        for (Map.Entry<String, PhysicalColumnConfig> entry : mapping.entrySet()) {
            String fieldCode = entry.getKey();
            PhysicalColumnConfig config = entry.getValue();
            
            // 检查字段编码
            if (StrUtil.isBlank(fieldCode)) {
                result.addError("字段编码不能为空");
                continue;
            }
            
            // 检查字段编码格式
            if (!fieldCode.matches("^[a-zA-Z][a-zA-Z0-9_]*$")) {
                result.addError(String.format("字段编码格式无效: %s", fieldCode));
            }
            
            // 检查配置对象
            if (config == null) {
                result.addError(String.format("字段 %s 的配置为空", fieldCode));
                continue;
            }
            
            // 验证配置
            String configError = config.validate();
            if (configError != null) {
                result.addError(String.format("字段 %s: %s", fieldCode, configError));
            }
            
            // 检查列名重复
            if (config.getColumn() != null) {
                String columnLower = config.getColumn().toLowerCase();
                if (usedColumnNames.contains(columnLower)) {
                    result.addError(String.format("列名重复: %s", config.getColumn()));
                } else {
                    usedColumnNames.add(columnLower);
                }
            }
        }
        
        return result;
    }

    /**
     * 快速验证物理列映射配置是否有效
     * 
     * @param mapping 物理列映射配置
     * @return true 如果配置有效
     */
    public static boolean isValid(Map<String, PhysicalColumnConfig> mapping) {
        return validateMapping(mapping).isValid();
    }

    /**
     * 验证 JSON 字符串格式的配置是否有效
     * 
     * @param json JSON 字符串
     * @return 验证结果
     */
    public static ValidationResult validateJson(String json) {
        if (StrUtil.isBlank(json)) {
            return new ValidationResult(); // 空配置有效
        }
        
        Map<String, PhysicalColumnConfig> mapping = parseMapping(json);
        if (mapping.isEmpty() && StrUtil.isNotBlank(json)) {
            ValidationResult result = new ValidationResult();
            result.addError("JSON 格式无效或解析失败");
            return result;
        }
        
        return validateMapping(mapping);
    }

    // ==================== 便捷构建方法 ====================

    /**
     * 创建物理列映射构建器
     * 
     * @return 构建器
     */
    public static MappingBuilder builder() {
        return new MappingBuilder();
    }

    /**
     * 从现有配置创建构建器
     * 
     * @param existing 现有配置
     * @return 构建器
     */
    public static MappingBuilder builder(Map<String, PhysicalColumnConfig> existing) {
        MappingBuilder builder = new MappingBuilder();
        if (CollUtil.isNotEmpty(existing)) {
            builder.mapping.putAll(existing);
        }
        return builder;
    }

    // ==================== 辅助方法 ====================

    /**
     * 获取所有物理列名
     * 
     * @param mapping 物理列映射配置
     * @return 列名列表
     */
    public static List<String> getColumnNames(Map<String, PhysicalColumnConfig> mapping) {
        if (CollUtil.isEmpty(mapping)) {
            return Collections.emptyList();
        }
        
        List<String> columns = new ArrayList<>();
        for (PhysicalColumnConfig config : mapping.values()) {
            if (config != null && StrUtil.isNotBlank(config.getColumn())) {
                columns.add(config.getColumn());
            }
        }
        return columns;
    }

    /**
     * 根据列名获取字段编码
     * 
     * @param mapping 物理列映射配置
     * @param columnName 列名
     * @return 字段编码，未找到返回 null
     */
    public static String getFieldCodeByColumn(Map<String, PhysicalColumnConfig> mapping, String columnName) {
        if (CollUtil.isEmpty(mapping) || StrUtil.isBlank(columnName)) {
            return null;
        }
        
        for (Map.Entry<String, PhysicalColumnConfig> entry : mapping.entrySet()) {
            PhysicalColumnConfig config = entry.getValue();
            if (config != null && columnName.equalsIgnoreCase(config.getColumn())) {
                return entry.getKey();
            }
        }
        return null;
    }

    /**
     * 判断字段是否配置了物理列
     * 
     * @param mapping 物理列映射配置
     * @param fieldCode 字段编码
     * @return true 如果字段配置了物理列
     */
    public static boolean isPhysicalColumn(Map<String, PhysicalColumnConfig> mapping, String fieldCode) {
        if (CollUtil.isEmpty(mapping) || StrUtil.isBlank(fieldCode)) {
            return false;
        }
        return mapping.containsKey(fieldCode);
    }

    /**
     * 获取字段的物理列配置
     * 
     * @param mapping 物理列映射配置
     * @param fieldCode 字段编码
     * @return 列配置，未找到返回 null
     */
    public static PhysicalColumnConfig getConfig(Map<String, PhysicalColumnConfig> mapping, String fieldCode) {
        if (CollUtil.isEmpty(mapping) || StrUtil.isBlank(fieldCode)) {
            return null;
        }
        return mapping.get(fieldCode);
    }

    /**
     * 合并两个物理列映射配置
     * 
     * <p>如果存在相同的字段编码，后者覆盖前者。</p>
     * 
     * @param base 基础配置
     * @param override 覆盖配置
     * @return 合并后的配置
     */
    public static Map<String, PhysicalColumnConfig> merge(
            Map<String, PhysicalColumnConfig> base,
            Map<String, PhysicalColumnConfig> override) {
        Map<String, PhysicalColumnConfig> result = new LinkedHashMap<>();
        
        if (CollUtil.isNotEmpty(base)) {
            result.putAll(base);
        }
        if (CollUtil.isNotEmpty(override)) {
            result.putAll(override);
        }
        
        return result;
    }

    // ==================== 内部类 ====================

    /**
     * 验证结果
     */
    public static class ValidationResult {
        private final List<String> errors = new ArrayList<>();

        public void addError(String error) {
            if (StrUtil.isNotBlank(error)) {
                errors.add(error);
            }
        }

        public boolean isValid() {
            return errors.isEmpty();
        }

        public List<String> getErrors() {
            return Collections.unmodifiableList(errors);
        }

        public String getErrorMessage() {
            if (errors.isEmpty()) {
                return null;
            }
            return String.join("; ", errors);
        }

        @Override
        public String toString() {
            return isValid() ? "Valid" : "Invalid: " + getErrorMessage();
        }
    }

    /**
     * 物理列映射构建器
     */
    public static class MappingBuilder {
        private final Map<String, PhysicalColumnConfig> mapping = new LinkedHashMap<>();

        /**
         * 添加 VARCHAR 类型字段
         * 
         * @param fieldCode 字段编码
         * @param column 列名
         * @param length 长度
         * @return 构建器
         */
        public MappingBuilder varchar(String fieldCode, String column, int length) {
            mapping.put(fieldCode, PhysicalColumnConfig.varchar(column, length));
            return this;
        }

        /**
         * 添加 VARCHAR 类型字段（使用默认长度）
         * 
         * @param fieldCode 字段编码
         * @param column 列名
         * @return 构建器
         */
        public MappingBuilder varchar(String fieldCode, String column) {
            mapping.put(fieldCode, PhysicalColumnConfig.varchar(column));
            return this;
        }

        /**
         * 添加 INTEGER 类型字段
         * 
         * @param fieldCode 字段编码
         * @param column 列名
         * @return 构建器
         */
        public MappingBuilder integer(String fieldCode, String column) {
            mapping.put(fieldCode, PhysicalColumnConfig.integer(column));
            return this;
        }

        /**
         * 添加 BIGINT 类型字段
         * 
         * @param fieldCode 字段编码
         * @param column 列名
         * @return 构建器
         */
        public MappingBuilder bigint(String fieldCode, String column) {
            mapping.put(fieldCode, PhysicalColumnConfig.bigint(column));
            return this;
        }

        /**
         * 添加 DECIMAL 类型字段
         * 
         * @param fieldCode 字段编码
         * @param column 列名
         * @param precision 精度
         * @param scale 小数位
         * @return 构建器
         */
        public MappingBuilder decimal(String fieldCode, String column, int precision, int scale) {
            mapping.put(fieldCode, PhysicalColumnConfig.decimal(column, precision, scale));
            return this;
        }

        /**
         * 添加 DECIMAL 类型字段（使用默认精度）
         * 
         * @param fieldCode 字段编码
         * @param column 列名
         * @return 构建器
         */
        public MappingBuilder decimal(String fieldCode, String column) {
            mapping.put(fieldCode, PhysicalColumnConfig.decimal(column));
            return this;
        }

        /**
         * 添加 DATE 类型字段
         * 
         * @param fieldCode 字段编码
         * @param column 列名
         * @return 构建器
         */
        public MappingBuilder date(String fieldCode, String column) {
            mapping.put(fieldCode, PhysicalColumnConfig.date(column));
            return this;
        }

        /**
         * 添加 TIMESTAMP 类型字段
         * 
         * @param fieldCode 字段编码
         * @param column 列名
         * @return 构建器
         */
        public MappingBuilder timestamp(String fieldCode, String column) {
            mapping.put(fieldCode, PhysicalColumnConfig.timestamp(column));
            return this;
        }

        /**
         * 添加 BOOLEAN 类型字段
         * 
         * @param fieldCode 字段编码
         * @param column 列名
         * @return 构建器
         */
        public MappingBuilder bool(String fieldCode, String column) {
            mapping.put(fieldCode, PhysicalColumnConfig.bool(column));
            return this;
        }

        /**
         * 添加 TEXT 类型字段
         * 
         * @param fieldCode 字段编码
         * @param column 列名
         * @return 构建器
         */
        public MappingBuilder text(String fieldCode, String column) {
            mapping.put(fieldCode, PhysicalColumnConfig.text(column));
            return this;
        }

        /**
         * 添加自定义配置
         * 
         * @param fieldCode 字段编码
         * @param config 列配置
         * @return 构建器
         */
        public MappingBuilder add(String fieldCode, PhysicalColumnConfig config) {
            mapping.put(fieldCode, config);
            return this;
        }

        /**
         * 移除字段
         * 
         * @param fieldCode 字段编码
         * @return 构建器
         */
        public MappingBuilder remove(String fieldCode) {
            mapping.remove(fieldCode);
            return this;
        }

        /**
         * 构建物理列映射配置
         * 
         * @return 物理列映射配置
         */
        public Map<String, PhysicalColumnConfig> build() {
            return new LinkedHashMap<>(mapping);
        }

        /**
         * 构建并序列化为 JSON
         * 
         * @return JSON 字符串
         */
        public String toJson() {
            return PhysicalColumnMappingUtils.toJson(mapping);
        }

        /**
         * 构建并验证
         * 
         * @return 验证结果
         */
        public ValidationResult validate() {
            return PhysicalColumnMappingUtils.validateMapping(mapping);
        }
    }
}
