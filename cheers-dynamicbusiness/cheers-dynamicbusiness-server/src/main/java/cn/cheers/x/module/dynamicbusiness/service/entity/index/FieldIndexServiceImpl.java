package cn.cheers.x.module.dynamicbusiness.service.entity.index;

import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entity.EntityDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entity.EntityFieldIndexDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.field.FieldDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.model.ModelDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.model.ModelFieldAssignmentDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.entity.EntityFieldIndexMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.field.FieldMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.model.ModelFieldAssignmentMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.model.ModelMapper;
import cn.cheers.x.module.dynamicbusiness.dal.repository.entity.EntityRepository;
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

import static cn.cheers.x.module.dynamicbusiness.service.entity.index.EntityFieldIndexRecordBuilder.INDEX_INSERT_BATCH_SIZE;

/**
 * 字段索引管理服务实现
 *
 * <p>负责管理扩展字段的索引配置，包括获取可查询字段列表、
 * 处理字段可查询标记变更、管理字段索引的创建和清理。</p>
 *
 * <p>进索引表认可搜索 / 可筛选 / 可排序；建索引按批插入，不清整实体再逐条同步。</p>
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
    private final EntityFieldIndexMapper entityFieldIndexMapper;

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
                    ModelFieldAssignmentDO assignment = assignmentMap.get(field.getId());
                    Boolean configured = assignment != null ? assignment.getIsSearchable() : null;
                    return ExtensionFieldIndexEligibility.resolveSearchable(configured, field.getType());
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
                    ModelFieldAssignmentDO assignment = assignmentMap.get(field.getId());
                    Boolean configured = assignment != null ? assignment.getIsSortable() : null;
                    return ExtensionFieldIndexEligibility.resolveSortable(configured, field.getType());
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

        return ExtensionFieldIndexEligibility.resolveSearchable(
                assignment.getIsSearchable(), field.getType());
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

        return ExtensionFieldIndexEligibility.resolveSortable(
                assignment.getIsSortable(), field.getType());
    }

    @Override
    public boolean isFieldFilterable(Long modelId, String fieldCode) {
        if (modelId == null || fieldCode == null || fieldCode.isEmpty()) {
            return false;
        }
        FieldDO field = fieldMapper.selectByCode(fieldCode);
        if (field == null) {
            return false;
        }
        ModelFieldAssignmentDO assignment =
                modelFieldAssignmentMapper.selectByModelIdAndFieldId(modelId, field.getId());
        if (assignment == null) {
            return false;
        }
        return ExtensionFieldIndexEligibility.resolveFilterable(assignment.getIsFilterable(), field.getType());
    }

    @Override
    @Async("entitySyncExecutor")
    @Transactional(rollbackFor = Exception.class)
    public void onIndexMembershipChanged(Long fieldId, Long modelId, String fieldCode, boolean shouldIndex) {
        log.info("扩展字段索引资格变更: fieldId={}, modelId={}, fieldCode={}, shouldIndex={}",
                fieldId, modelId, fieldCode, shouldIndex);
        if (shouldIndex) {
            FieldDO field = fieldMapper.selectById(fieldId);
            if (field != null) {
                createFieldIndex(modelId, fieldCode, field.getType());
            }
        } else {
            removeFieldIndex(modelId, fieldCode);
        }
    }

    /**
     * 只重建这一个字段的索引行：先按型号+字段编码整批删除，再按批插入。
     * 禁止对每条实体再跑一遍整实体同步。
     */
    @Override
    @Async("entitySyncExecutor")
    public void createFieldIndex(Long modelId, String fieldCode, String fieldType) {
        log.info("开始为字段批量写入索引: modelId={}, fieldCode={}, fieldType={}", modelId, fieldCode, fieldType);
        try {
            ModelDO model = modelMapper.selectById(modelId);
            if (model == null) {
                log.warn("Model 不存在，跳过索引创建: modelId={}", modelId);
                return;
            }
            FieldDO field = fieldMapper.selectByCode(fieldCode);
            if (field == null) {
                log.warn("字段不存在，跳过索引创建: fieldCode={}", fieldCode);
                return;
            }
            List<EntityDO> entities = entityRepository.findByModelId(modelId, model.getEntityTypeCode());
            entityFieldIndexMapper.deleteByModelIdAndFieldCode(modelId, fieldCode);
            if (entities == null || entities.isEmpty()) {
                log.info("Model 下没有 Entity，已清空该字段索引: modelId={}, fieldCode={}", modelId, fieldCode);
                return;
            }
            List<EntityFieldIndexDO> records = new ArrayList<>();
            for (EntityDO entity : entities) {
                Map<String, Object> customFields = entity.getCustomFields() != null
                        ? entity.getCustomFields() : Map.of();
                EntityFieldIndexDO record = EntityFieldIndexRecordBuilder.tryBuild(entity, field, customFields);
                if (record != null) {
                    records.add(record);
                }
            }
            if (!records.isEmpty()) {
                entityFieldIndexMapper.insertBatch(records, INDEX_INSERT_BATCH_SIZE);
            }
            log.info("字段索引批量写入完成: modelId={}, fieldCode={}, entityCount={}, indexedRows={}",
                    modelId, fieldCode, entities.size(), records.size());
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
