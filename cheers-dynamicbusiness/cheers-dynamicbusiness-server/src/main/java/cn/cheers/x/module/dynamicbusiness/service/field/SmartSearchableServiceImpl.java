package cn.cheers.x.module.dynamicbusiness.service.field;

import cn.cheers.x.module.dynamicbusiness.dal.dataobject.field.FieldDO;
import cn.cheers.x.module.dynamicbusiness.enums.field.FieldTypeEnum;
import cn.cheers.x.module.dynamicbusiness.service.field.vo.SmartDefaultConfigVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.util.*;

/**
 * 智能默认可查询服务实现
 * 
 * <p>根据字段类型自动设置可查询属性和索引策略，实现零代码平台的智能默认功能。</p>
 * 
 * <h3>智能默认规则（需求：FR-BDA-080~083）</h3>
 * <ul>
 *   <li>常用字段类型（文本、数字、日期、布尔、单选、关联）默认可查询</li>
 *   <li>大文本字段（LONG_TEXT）默认不可查询，但支持手动开启</li>
 *   <li>文件类字段（FILE、IMAGE）默认不可查询</li>
 *   <li>系统根据字段类型自动选择合适的索引策略</li>
 * </ul>
 * 
 * <h3>业务规则（BR-BDA-060~063）</h3>
 * <ul>
 *   <li>BR-BDA-060: 常用可查询 - 文本、数字、日期、布尔、单选、关联字段默认可查询</li>
 *   <li>BR-BDA-061: 大文本不索引 - LONG_TEXT 类型默认不可查询，避免性能问题</li>
 *   <li>BR-BDA-062: 文件不索引 - FILE、IMAGE 类型不可查询</li>
 *   <li>BR-BDA-063: 智能索引 - 系统根据字段类型自动选择索引策略</li>
 * </ul>
 * 
 * @author yudao
 * @since 2026-01-07
 */
@Slf4j
@Service
public class SmartSearchableServiceImpl implements SmartSearchableService {

    /**
     * 智能默认配置缓存
     * key: 字段类型编码（大写）
     * value: 智能默认配置
     */
    private final Map<String, SmartDefaultConfigVO> configCache = new LinkedHashMap<>();

    /**
     * 默认配置（用于未知类型）
     */
    private SmartDefaultConfigVO defaultConfig;

    /**
     * 初始化智能默认配置
     */
    @PostConstruct
    public void init() {
        log.info("初始化智能默认可查询配置...");
        
        // 初始化各字段类型的智能默认配置
        initConfigs();
        
        log.info("智能默认可查询配置初始化完成，共 {} 种字段类型", configCache.size());
    }

    /**
     * 初始化所有字段类型的智能默认配置
     */
    private void initConfigs() {
        // ========== 常用类型 - 默认可查询 ==========
        
        // TEXT - 单行文本
        addConfig(FieldTypeEnum.TEXT.getCode(), FieldTypeEnum.TEXT.getName(),
                true, true, IndexStrategy.GIN, true,
                "单行文本字段，使用 GIN 索引支持等值和模糊查询");
        
        // NUMBER - 数字（小数）
        addConfig(FieldTypeEnum.NUMBER.getCode(), FieldTypeEnum.NUMBER.getName(),
                true, true, IndexStrategy.BTREE, true,
                "数字字段，使用 B-Tree 索引支持范围查询和排序");
        
        // INTEGER - 整数
        addConfig(FieldTypeEnum.INTEGER.getCode(), FieldTypeEnum.INTEGER.getName(),
                true, true, IndexStrategy.BTREE, true,
                "整数字段，使用 B-Tree 索引支持范围查询和排序");
        
        // DATE - 日期
        addConfig(FieldTypeEnum.DATE.getCode(), FieldTypeEnum.DATE.getName(),
                true, true, IndexStrategy.BTREE, true,
                "日期字段，使用 B-Tree 索引支持范围查询和排序");
        
        // DATETIME - 日期时间
        addConfig(FieldTypeEnum.DATETIME.getCode(), FieldTypeEnum.DATETIME.getName(),
                true, true, IndexStrategy.BTREE, true,
                "日期时间字段，使用 B-Tree 索引支持范围查询和排序");
        
        // BOOLEAN - 布尔
        addConfig(FieldTypeEnum.BOOLEAN.getCode(), FieldTypeEnum.BOOLEAN.getName(),
                true, false, IndexStrategy.GIN, true,
                "布尔字段，使用 GIN 索引支持等值查询");
        
        // ENUM - 枚举（单选/多选）
        addConfig(FieldTypeEnum.ENUM.getCode(), FieldTypeEnum.ENUM.getName(),
                true, false, IndexStrategy.GIN, true,
                "枚举字段，使用 GIN 索引支持等值和包含查询");
        
        // ENTITY_REF - 实体引用（单选关联字段）
        addConfig(FieldTypeEnum.ENTITY_REF.getCode(), FieldTypeEnum.ENTITY_REF.getName(),
                true, false, IndexStrategy.GIN, true,
                "单选关联字段，使用 GIN 索引支持等值查询");
        
        // ENTITY_REF_MULTI - 多选实体引用（多选关联字段）
        addConfig(FieldTypeEnum.ENTITY_REF_MULTI.getCode(), FieldTypeEnum.ENTITY_REF_MULTI.getName(),
                true, false, IndexStrategy.GIN, true,
                "多选关联字段，使用 GIN 索引支持包含查询");
        
        // REFERENCE - 系统引用
        addConfig(FieldTypeEnum.REFERENCE.getCode(), FieldTypeEnum.REFERENCE.getName(),
                true, false, IndexStrategy.GIN, true,
                "系统引用字段，使用 GIN 索引支持等值查询");
        
        // ========== 大文本/复杂类型 - 默认不可查询 ==========
        
        // LONG_TEXT - 长文本/多行文本
        addConfig(FieldTypeEnum.LONG_TEXT.getCode(), FieldTypeEnum.LONG_TEXT.getName(),
                false, false, IndexStrategy.NONE, true,
                "长文本字段，默认不索引以避免性能问题，支持手动开启");
        
        // JSON - JSON 复杂结构
        addConfig(FieldTypeEnum.JSON.getCode(), FieldTypeEnum.JSON.getName(),
                false, false, IndexStrategy.NONE, false,
                "JSON 字段，复杂结构不适合索引");
        
        // ========== 文件类型 - 不可查询 ==========
        // 注意：FILE 和 IMAGE 类型在当前枚举中未定义，预留配置
        addConfig("FILE", "文件",
                false, false, IndexStrategy.NONE, false,
                "文件字段，不支持查询");
        
        addConfig("IMAGE", "图片",
                false, false, IndexStrategy.NONE, false,
                "图片字段，不支持查询");
        
        // ========== 默认配置（用于未知类型）==========
        defaultConfig = SmartDefaultConfigVO.of(
                "UNKNOWN", "未知类型",
                false, false, IndexStrategy.NONE, false,
                "未知字段类型，默认不索引");
    }

    /**
     * 添加配置到缓存
     */
    private void addConfig(String fieldType, String fieldTypeName,
                          boolean defaultSearchable, boolean defaultSortable,
                          IndexStrategy indexStrategy, boolean canManuallyEnableSearchable,
                          String description) {
        SmartDefaultConfigVO config = SmartDefaultConfigVO.of(
                fieldType, fieldTypeName,
                defaultSearchable, defaultSortable,
                indexStrategy, canManuallyEnableSearchable,
                description);
        configCache.put(fieldType.toUpperCase(), config);
    }

    @Override
    public boolean getDefaultSearchable(String fieldType) {
        SmartDefaultConfigVO config = getConfigInternal(fieldType);
        return config.getDefaultSearchable();
    }

    @Override
    public boolean getDefaultSortable(String fieldType) {
        SmartDefaultConfigVO config = getConfigInternal(fieldType);
        return config.getDefaultSortable();
    }

    @Override
    public IndexStrategy getDefaultIndexStrategy(String fieldType) {
        SmartDefaultConfigVO config = getConfigInternal(fieldType);
        return config.getIndexStrategy();
    }

    @Override
    public void applySmartDefaults(FieldDO field) {
        if (field == null) {
            return;
        }
        
        String fieldType = field.getType();
        SmartDefaultConfigVO config = getConfigInternal(fieldType);
        
        // 注意：isSearchable 和 isSortable 已移至 ModelFieldAssignmentDO 中配置
        // 字段创建时不再设置这些属性，这些属性在模型字段时根据字段类型应用智能默认值
        
        // 如果用户未显式设置 indexStrategy，则应用智能默认值
        if (field.getIndexStrategy() == null || field.getIndexStrategy().isEmpty()) {
            field.setIndexStrategy(config.getIndexStrategy().getCode());
            log.debug("字段 [{}] 类型 [{}] 应用智能默认索引策略: {}", 
                    field.getName(), fieldType, config.getIndexStrategy().getCode());
        }
    }

    @Override
    public void applySmartDefaults(List<FieldDO> fields) {
        if (fields == null || fields.isEmpty()) {
            return;
        }
        
        for (FieldDO field : fields) {
            applySmartDefaults(field);
        }
    }

    @Override
    public Map<String, SmartDefaultConfigVO> getSmartDefaultConfigs() {
        // 返回不可修改的副本
        return Collections.unmodifiableMap(new LinkedHashMap<>(configCache));
    }

    @Override
    public SmartDefaultConfigVO getSmartDefaultConfig(String fieldType) {
        return getConfigInternal(fieldType);
    }

    @Override
    public boolean canManuallyEnableSearchable(String fieldType) {
        SmartDefaultConfigVO config = getConfigInternal(fieldType);
        return config.getCanManuallyEnableSearchable();
    }

    /**
     * 获取配置（内部方法）
     * 
     * @param fieldType 字段类型编码
     * @return 智能默认配置，如果类型不存在则返回默认配置
     */
    private SmartDefaultConfigVO getConfigInternal(String fieldType) {
        if (fieldType == null || fieldType.isEmpty()) {
            return defaultConfig;
        }
        
        SmartDefaultConfigVO config = configCache.get(fieldType.toUpperCase());
        return config != null ? config : defaultConfig;
    }
}
