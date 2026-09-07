package cn.cheers.x.module.dynamicbusiness.service.entity.index;

import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entity.EntityDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.field.FieldDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.model.ModelDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.model.ModelFieldAssignmentDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.entity.EntityFieldIndexMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.field.FieldMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.model.ModelFieldAssignmentMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.model.ModelMapper;
import cn.cheers.x.module.dynamicbusiness.dal.repository.entity.EntityRepository;
import cn.cheers.x.module.dynamicbusiness.service.entity.sync.EntitySyncService;
import cn.cheers.x.module.dynamicbusiness.service.field.SmartSearchableService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 字段索引管理服务实现
 *
 * <p>负责管理扩展字段的索引配置，包括获取可查询字段列表、
 * 处理字段可查询标记变更、管理字段索引的创建和清理。</p>
 *
 * <h3>业务规则</h3>
 * <ul>
 *   <li>FR-019: 系统必须支持标记字段为"可查询"（is_searchable）</li>
 *   <li>FR-020: 系统必须在字段标记为可查询时自动创建索引</li>
 *   <li>FR-021: 系统必须在字段取消可查询标记时清理索引</li>
 *   <li>FR-022: 系统必须支持查询某个 Model 的所有可查询字段列表</li>
 * </ul>
 *
 * @author 扩展字段查询服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FieldIndexServiceImpl implements FieldIndexService {

    private final FieldMapper fieldMapper;
    private final ModelMapper modelMapper;
    private final ModelFieldAssignmentMapper modelFieldAssignmentMapper;
    private final EntityRepository entityRepository;
    private final SmartSearchableService smartSearchableService;
    private final EntityFieldIndexMapper entityFieldIndexMapper;
    private final EntitySyncService entitySyncService;

    // ==================== 默认可查询的字段类型 ====================

    private static final Set<String> DEFAULT_SEARCHABLE_TYPES = Set.of(
            "STRING", "TEXT_SHORT", "INTEGER", "NUMBER", "DECIMAL",
            "DATE", "DATETIME", "BOOLEAN", "ENUM", "SELECT",
            "MULTI_SELECT", "ENTITY_REF"
    );

    private static final Set<String> DEFAULT_NOT_SEARCHABLE_TYPES = Set.of(
            "TEXT", "FILE", "IMAGE", "RICH_TEXT", "JSON"
    );

    // ==================== 查询可查询字段 ====================

    @Override
    public List<FieldDO> getSearchableFields(Long modelId) {
        if (modelId == null) {
            return new ArrayList<>();
        }

        // 1. 获取 Model 关联的字段 ID 列表
        List<Long> fieldIds = modelFieldAssignmentMapper.selectFieldIdsByModelId(modelId);
        if (fieldIds.isEmpty()) {
            return new ArrayList<>();
        }

        // 2. 查询字段分配信息（优先使用模型字段中的配置）
        List<ModelFieldAssignmentDO> assignments = modelFieldAssignmentMapper.selectByModelId(modelId);
        Map<Long, ModelFieldAssignmentDO> assignmentMap = assignments.stream()
                .collect(Collectors.toMap(ModelFieldAssignmentDO::getFieldId, a -> a));

        // 3. 查询字段详情，过滤出可查询的字段（优先使用模型字段中的配置）
        return fieldIds.stream()
                .map(fieldMapper::selectById)
                .filter(field -> {
                    if (field == null) {
                        return false;
                    }
                    // 优先使用模型字段中的配置，如果为 null 则使用智能默认值
                    ModelFieldAssignmentDO assignment = assignmentMap.get(field.getId());
                    Boolean isSearchable;
                    if (assignment != null && assignment.getIsSearchable() != null) {
                        isSearchable = assignment.getIsSearchable();
                    } else {
                        // 使用智能默认值服务获取字段类型的默认可查询属性
                        isSearchable = smartSearchableService.getDefaultSearchable(field.getType());
                    }
                    return Boolean.TRUE.equals(isSearchable);
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<FieldDO> getSearchableFieldsByModelCode(String modelCode) {
        if (modelCode == null || modelCode.isEmpty()) {
            return new ArrayList<>();
        }

        // 1. 根据 Model 编码查询 Model
        ModelDO model = modelMapper.selectByCode(modelCode);
        if (model == null) {
            log.warn("Model 不存在: modelCode={}", modelCode);
            return new ArrayList<>();
        }

        // 2. 获取可查询字段
        return getSearchableFields(model.getId());
    }

    @Override
    public List<FieldDO> getSortableFields(Long modelId) {
        if (modelId == null) {
            return new ArrayList<>();
        }

        // 1. 获取 Model 关联的字段 ID 列表
        List<Long> fieldIds = modelFieldAssignmentMapper.selectFieldIdsByModelId(modelId);
        if (fieldIds.isEmpty()) {
            return new ArrayList<>();
        }

        // 2. 查询字段分配信息（优先使用模型字段中的配置）
        List<ModelFieldAssignmentDO> assignments = modelFieldAssignmentMapper.selectByModelId(modelId);
        Map<Long, ModelFieldAssignmentDO> assignmentMap = assignments.stream()
                .collect(Collectors.toMap(ModelFieldAssignmentDO::getFieldId, a -> a));

        // 3. 查询字段详情，过滤出可排序的字段（优先使用模型字段中的配置）
        return fieldIds.stream()
                .map(fieldMapper::selectById)
                .filter(field -> {
                    if (field == null) {
                        return false;
                    }
                    // 优先使用模型字段中的配置，如果为 null 则使用智能默认值
                    ModelFieldAssignmentDO assignment = assignmentMap.get(field.getId());
                    Boolean isSortable;
                    if (assignment != null && assignment.getIsSortable() != null) {
                        isSortable = assignment.getIsSortable();
                    } else {
                        // 使用智能默认值服务获取字段类型的默认可排序属性
                        isSortable = smartSearchableService.getDefaultSortable(field.getType());
                    }
                    return Boolean.TRUE.equals(isSortable);
                })
                .collect(Collectors.toList());
    }

    @Override
    public boolean isFieldSearchable(Long modelId, String fieldCode) {
        if (fieldCode == null || fieldCode.isEmpty()) {
            return false;
        }
        // 核心列始终可按名称等检索，不依赖模型字段上的可搜索开关
        if (isCoreQueryableField(fieldCode)) {
            return true;
        }
        if (modelId == null) {
            return false;
        }

        // 1. 根据字段编码查询字段
        FieldDO field = fieldMapper.selectByCode(fieldCode);
        if (field == null) {
            return false;
        }

        // 2. 检查字段是否关联到该 Model，并获取字段分配信息
        ModelFieldAssignmentDO assignment = modelFieldAssignmentMapper.selectByModelIdAndFieldId(modelId, field.getId());
        if (assignment == null) {
            return false;
        }

        // 3. 检查是否可查询（优先使用模型字段中的配置，如果为 null 则使用智能默认值）
        Boolean isSearchable;
        if (assignment.getIsSearchable() != null) {
            isSearchable = assignment.getIsSearchable();
        } else {
            // 使用智能默认值服务获取字段类型的默认可查询属性
            isSearchable = smartSearchableService.getDefaultSearchable(field.getType());
        }
        return Boolean.TRUE.equals(isSearchable);
    }

    /**
     * 实体核心列：不走扩展字段索引，但仍允许按字段查（如列表 keyword 匹配名称）。
     */
    private static boolean isCoreQueryableField(String fieldCode) {
        String code = fieldCode.trim();
        return "id".equals(code)
                || "name".equals(code)
                || "code".equals(code)
                || "status".equals(code)
                || "modelId".equals(code)
                || "model_id".equals(code)
                || "parentId".equals(code)
                || "parent_id".equals(code)
                || "entityTypeCode".equals(code)
                || "entity_type_code".equals(code);
    }

    @Override
    public boolean isFieldSortable(Long modelId, String fieldCode) {
        if (modelId == null || fieldCode == null || fieldCode.isEmpty()) {
            return false;
        }

        // 1. 根据字段编码查询字段
        FieldDO field = fieldMapper.selectByCode(fieldCode);
        if (field == null) {
            return false;
        }

        // 2. 检查字段是否关联到该 Model，并获取字段分配信息
        ModelFieldAssignmentDO assignment = modelFieldAssignmentMapper.selectByModelIdAndFieldId(modelId, field.getId());
        if (assignment == null) {
            return false;
        }

        // 3. 检查是否可排序（优先使用模型字段中的配置，如果为 null 则使用智能默认值）
        Boolean isSortable;
        if (assignment.getIsSortable() != null) {
            isSortable = assignment.getIsSortable();
        } else {
            // 使用智能默认值服务获取字段类型的默认可排序属性
            isSortable = smartSearchableService.getDefaultSortable(field.getType());
        }
        return Boolean.TRUE.equals(isSortable);
    }

    // ==================== 处理字段可查询标记变更 ====================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void onSearchableChanged(Long fieldId, Long modelId, String fieldCode, boolean newSearchable) {
        log.info("字段可查询标记变更: fieldId={}, modelId={}, fieldCode={}, newSearchable={}", 
                fieldId, modelId, fieldCode, newSearchable);

        if (newSearchable) {
            // 从 false 变为 true：创建索引
            FieldDO field = fieldMapper.selectById(fieldId);
            if (field != null) {
                createFieldIndex(modelId, fieldCode, field.getType());
            }
        } else {
            // 从 true 变为 false：清理索引
            removeFieldIndex(modelId, fieldCode);
        }
    }

    @Override
    @Async("entitySyncExecutor")
    public void createFieldIndex(Long modelId, String fieldCode, String fieldType) {
        log.info("开始为字段创建索引: modelId={}, fieldCode={}, fieldType={}", modelId, fieldCode, fieldType);

        try {
            // 1. 获取 Model 的业务类型
            ModelDO model = modelMapper.selectById(modelId);
            if (model == null) {
                log.warn("Model 不存在，跳过索引创建: modelId={}", modelId);
                return;
            }
            String entityTypeCode = model.getEntityTypeCode();

            // 2. 使用 Repository 获取该 Model 下的所有 Entity
            List<EntityDO> entities = entityRepository.findByModelId(modelId, entityTypeCode);
            if (entities.isEmpty()) {
                log.info("Model 下没有 Entity，跳过索引创建: modelId={}", modelId);
                return;
            }

            // 4. 批量同步 Entity 到索引表
            int successCount = 0;
            int failCount = 0;

            for (EntityDO entity : entities) {
                try {
                    // 使用同步服务同步单个 Entity
                    entitySyncService.syncEntity(entity);
                    successCount++;
                } catch (Exception e) {
                    failCount++;
                    log.error("同步 Entity 到索引表失败: entityId={}, error={}", entity.getId(), e.getMessage());
                }
            }

            log.info("字段索引创建完成: modelId={}, fieldCode={}, total={}, success={}, fail={}", 
                    modelId, fieldCode, entities.size(), successCount, failCount);

        } catch (Exception e) {
            log.error("创建字段索引失败: modelId={}, fieldCode={}, error={}", modelId, fieldCode, e.getMessage(), e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeFieldIndex(Long modelId, String fieldCode) {
        log.info("开始清理字段索引: modelId={}, fieldCode={}", modelId, fieldCode);

        try {
            // 删除索引表中该字段的所有数据
            int deletedCount = entityFieldIndexMapper.deleteByModelIdAndFieldCode(modelId, fieldCode);
            log.info("字段索引清理完成: modelId={}, fieldCode={}, deletedCount={}", modelId, fieldCode, deletedCount);

        } catch (Exception e) {
            log.error("清理字段索引失败: modelId={}, fieldCode={}, error={}", modelId, fieldCode, e.getMessage(), e);
            throw e;
        }
    }

    // ==================== 索引策略 ====================

    @Override
    public IndexStrategy getIndexStrategy(String fieldType) {
        if (fieldType == null || fieldType.isEmpty()) {
            return IndexStrategy.NONE;
        }

        String upperType = fieldType.toUpperCase();

        // 字符串类型、布尔类型、选择类型、引用类型使用 GIN 索引
        if (Set.of("STRING", "BOOLEAN", "ENUM", "SELECT", "MULTI_SELECT", "ENTITY_REF").contains(upperType)) {
            return IndexStrategy.GIN_INDEX;
        }

        // 数值类型、日期类型使用索引表（支持范围查询和排序）
        if (Set.of("INTEGER", "NUMBER", "DECIMAL", "DATE", "DATETIME").contains(upperType)) {
            return IndexStrategy.FIELD_INDEX_TABLE;
        }

        // 其他类型不索引
        return IndexStrategy.NONE;
    }

    @Override
    public boolean isDefaultSearchable(String fieldType) {
        if (fieldType == null || fieldType.isEmpty()) {
            return false;
        }

        String upperType = fieldType.toUpperCase();

        // 明确不可查询的类型
        if (DEFAULT_NOT_SEARCHABLE_TYPES.contains(upperType)) {
            return false;
        }

        // 默认可查询的类型
        return DEFAULT_SEARCHABLE_TYPES.contains(upperType);
    }
}
