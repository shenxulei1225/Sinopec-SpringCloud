package cn.cheers.x.module.dynamicbusiness.service.entitytype;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.cheers.x.framework.tenant.core.context.TenantContextHolder;
import cn.cheers.x.framework.tenant.core.util.TenantUtils;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entitytype.vo.EntityTypeBaseFieldBatchSaveReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entitytype.vo.EntityTypeBaseFieldRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entitytype.vo.EntityTypeBaseFieldSaveReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entitytype.vo.EntityTypePlatformFieldRespVO;
import cn.cheers.x.module.dynamicbusiness.convert.entitytype.EntityTypeBaseFieldConvert;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entitytype.EntityTypeBaseFieldDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entitytype.EntityTypeDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.field.FieldDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.entitytype.EntityTypeBaseFieldMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.entitytype.EntityTypeMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.field.FieldMapper;
import cn.cheers.x.module.dynamicbusiness.framework.field.EntityTypeFieldLabelHelper;
import cn.cheers.x.module.dynamicbusiness.enums.entitytype.StorageTypeEnum;
import cn.cheers.x.module.dynamicbusiness.framework.entity.EntityBaseFieldColumnNames;
import cn.cheers.x.module.dynamicbusiness.service.capability.BusinessCapabilityService;
import cn.cheers.x.module.dynamicbusiness.service.dynamictable.DynamicTableService;
import cn.hutool.core.util.StrUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executor;

import static cn.cheers.x.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.cheers.x.module.dynamicbusiness.enums.ErrorCodeConstants.*;

@Service
@Validated
@Slf4j
public class EntityTypeBaseFieldServiceImpl implements EntityTypeBaseFieldService {

    @Resource
    private EntityTypeBaseFieldMapper baseFieldMapper;
    @Resource
    private EntityTypeMapper entityTypeMapper;
    @Resource
    private FieldMapper fieldMapper;
    @Resource
    private BaseFieldLibrarySyncService baseFieldLibrarySyncService;
    @Resource
    @Lazy
    private BusinessCapabilityService businessCapabilityService;
    @Resource
    private DynamicTableService dynamicTableService;
    @Resource
    @Qualifier("systemAsyncExecutor")
    private Executor systemAsyncExecutor;

    /**
     * 固定列变更后刷新能力投影 / 全型号 CRUD 表单。
     * 设备等类型下型号很多时同步重建可达数十秒，会拖垮 save-batch / delete 直至代理超时；
     * 因此事务提交后再异步执行，HTTP 先返回保存成功。
     */
    private void notifyEntityTypeFieldDefinitionChanged(String entityTypeCode) {
        if (entityTypeCode == null || entityTypeCode.isBlank()) {
            return;
        }
        String code = entityTypeCode.trim();
        Long tenantId = TenantContextHolder.getTenantId();
        Runnable refresh = () -> {
            try {
                Runnable work = () -> businessCapabilityService
                        .refreshAfterEntityTypeFieldDefinitionChanged(code);
                if (tenantId != null) {
                    TenantUtils.execute(tenantId, work);
                } else {
                    work.run();
                }
                log.info("[notifyEntityTypeFieldDefinitionChanged] 异步刷新完成 entityTypeCode={}", code);
            } catch (Exception e) {
                log.error("[notifyEntityTypeFieldDefinitionChanged] 异步刷新失败 entityTypeCode={}", code, e);
            }
        };
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    systemAsyncExecutor.execute(refresh);
                }
            });
        } else {
            systemAsyncExecutor.execute(refresh);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createBaseField(EntityTypeBaseFieldSaveReqVO reqVO) {
        Long id = createBaseFieldWithoutNotify(reqVO);
        notifyEntityTypeFieldDefinitionChanged(reqVO.getEntityTypeCode());
        return id;
    }

    private Long createBaseFieldWithoutNotify(EntityTypeBaseFieldSaveReqVO reqVO) {
        if (reqVO.getId() != null) {
            throw new ServiceException(400, "创建固定列字段时不能携带 id");
        }
        if (reqVO.getLibraryFieldId() == null) {
            throw exception(BASE_FIELD_LIBRARY_ID_REQUIRED);
        }
        if (entityTypeMapper.selectByCode(reqVO.getEntityTypeCode()) == null) {
            throw new ServiceException(404, "业务类型不存在");
        }

        FieldDO libraryField = requireLibraryField(reqVO.getLibraryFieldId());
        if (existsByLibraryField(reqVO.getEntityTypeCode(), libraryField.getId(), null)) {
            throw new ServiceException(400, "该字段已加入当前业务类型的基础字段");
        }

        EntityTypeBaseFieldDO field = buildBaseFieldFromLibrary(reqVO, libraryField);
        if (StringUtils.hasText(reqVO.getFieldName())) {
            field.setFieldName(reqVO.getFieldName().trim());
        }
        if (reqVO.getRequired() != null) {
            field.setRequired(reqVO.getRequired());
        }
        if (reqVO.getIsSearchable() != null) {
            field.setIsSearchable(reqVO.getIsSearchable());
        }
        if (reqVO.getIsFilterable() != null) {
            field.setIsFilterable(reqVO.getIsFilterable());
        }
        if (reqVO.getIsSortable() != null) {
            field.setIsSortable(reqVO.getIsSortable());
        }
        if (field.getStatus() == null) {
            field.setStatus(1);
        }
        if (field.getSortOrder() == null) {
            field.setSortOrder(baseFieldMapper.selectMaxSortOrder(reqVO.getEntityTypeCode()) + 1);
        }
        baseFieldMapper.insert(field);

        syncDedicatedTableAddColumn(field);

        baseFieldLibrarySyncService.assignLibraryFieldToAllModels(
                reqVO.getEntityTypeCode(), libraryField, field);
        return field.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateBaseField(EntityTypeBaseFieldSaveReqVO reqVO) {
        applyBaseFieldUpdate(reqVO);
    }

    private void applyBaseFieldUpdate(EntityTypeBaseFieldSaveReqVO reqVO) {
        EntityTypeBaseFieldDO field = baseFieldMapper.selectById(reqVO.getId());
        if (field == null) {
            throw new ServiceException(404, "固定列字段不存在");
        }
        if (!Objects.equals(field.getEntityTypeCode(), reqVO.getEntityTypeCode())
                && entityTypeMapper.selectByCode(reqVO.getEntityTypeCode()) == null) {
            throw new ServiceException(404, "业务类型不存在");
        }

        FieldDO libraryField = resolveLibraryFieldForUpdate(field, reqVO.getLibraryFieldId());
        assertMetadataReadonly(reqVO, libraryField, field);

        String entityTypeCode = reqVO.getEntityTypeCode();
        if (libraryField != null) {
            syncLibraryIdentity(field, libraryField);
        }
        if (reqVO.getFieldName() != null) {
            if (StringUtils.hasText(reqVO.getFieldName())) {
                field.setFieldName(reqVO.getFieldName().trim());
            } else if (libraryField != null) {
                field.setFieldName(libraryField.getName());
            }
        }
        if (reqVO.getRequired() != null) {
            field.setRequired(reqVO.getRequired());
        }
        if (reqVO.getIsSearchable() != null) {
            field.setIsSearchable(reqVO.getIsSearchable());
        }
        if (reqVO.getIsFilterable() != null) {
            field.setIsFilterable(reqVO.getIsFilterable());
        }
        if (reqVO.getIsSortable() != null) {
            field.setIsSortable(reqVO.getIsSortable());
        }
        if (reqVO.getDefaultValue() != null) {
            field.setDefaultValue(reqVO.getDefaultValue());
        }
        if (reqVO.getDescription() != null) {
            field.setDescription(reqVO.getDescription());
        }
        if (reqVO.getSortOrder() != null) {
            field.setSortOrder(reqVO.getSortOrder());
        }
        if (reqVO.getStatus() != null) {
            field.setStatus(reqVO.getStatus());
        }
        baseFieldMapper.updateById(field);

        if (libraryField != null) {
            baseFieldLibrarySyncService.assignLibraryFieldToAllModels(
                    entityTypeCode, libraryField, field);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveBaseFieldBatch(EntityTypeBaseFieldBatchSaveReqVO reqVO) {
        String entityTypeCode = requireEntityType(reqVO.getEntityTypeCode()).getCode();
        boolean changed = false;
        if (reqVO.getDeleteIds() != null) {
            for (Long id : reqVO.getDeleteIds()) {
                if (id != null) {
                    deleteBaseFieldWithoutNotify(id);
                    changed = true;
                }
            }
        }
        if (reqVO.getCreates() != null) {
            for (EntityTypeBaseFieldSaveReqVO create : reqVO.getCreates()) {
                if (create != null) {
                    createBaseFieldWithoutNotify(create);
                    changed = true;
                }
            }
        }
        if (reqVO.getUpdates() != null) {
            for (EntityTypeBaseFieldSaveReqVO update : reqVO.getUpdates()) {
                if (update != null) {
                    applyBaseFieldUpdate(update);
                    changed = true;
                }
            }
        }
        if (reqVO.getPlatformFieldLabels() != null) {
            for (EntityTypeBaseFieldBatchSaveReqVO.PlatformFieldLabelSaveItem item : reqVO.getPlatformFieldLabels()) {
                if (item != null && StringUtils.hasText(item.getFieldCode())) {
                    applyPlatformFieldLabel(entityTypeCode, item.getFieldCode().trim(), item.getLabel());
                    changed = true;
                }
            }
        }
        if (changed) {
            notifyEntityTypeFieldDefinitionChanged(entityTypeCode);
        }
    }

    private void deleteBaseFieldWithoutNotify(Long id) {
        EntityTypeBaseFieldDO field = baseFieldMapper.selectById(id);
        if (field == null) {
            return;
        }
        String entityTypeCode = field.getEntityTypeCode();
        Long libraryFieldId = field.getLibraryFieldId();

        syncDedicatedTableDeprecateColumn(field);

        baseFieldMapper.deleteById(id);

        if (libraryFieldId != null) {
            baseFieldLibrarySyncService.removeLibraryFieldFromAllModels(entityTypeCode, libraryFieldId);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteBaseField(Long id) {
        EntityTypeBaseFieldDO field = baseFieldMapper.selectById(id);
        if (field == null) {
            return;
        }
        deleteBaseFieldWithoutNotify(id);
        notifyEntityTypeFieldDefinitionChanged(field.getEntityTypeCode());
    }

    @Override
    public EntityTypeBaseFieldDO getBaseField(Long id) {
        return baseFieldMapper.selectById(id);
    }

    @Override
    public EntityTypeBaseFieldRespVO getBaseFieldRespVO(Long id) {
        return enrichLibraryInfo(EntityTypeBaseFieldConvert.INSTANCE.convert(baseFieldMapper.selectById(id)));
    }

    @Override
    public List<EntityTypeBaseFieldRespVO> listByEntityTypeCode(String entityTypeCode) {
        return baseFieldMapper.selectByEntityTypeCode(entityTypeCode).stream()
                .map(EntityTypeBaseFieldConvert.INSTANCE::convert)
                .map(this::enrichLibraryInfo)
                .toList();
    }

    @Override
    public List<EntityTypeBaseFieldRespVO> listAllByEntityTypeCode(String entityTypeCode) {
        return baseFieldMapper.selectAllByEntityTypeCode(entityTypeCode).stream()
                .map(EntityTypeBaseFieldConvert.INSTANCE::convert)
                .map(this::enrichLibraryInfo)
                .toList();
    }

    @Override
    public List<EntityTypeBaseFieldDO> getBaseFieldsByEntityTypeCode(String entityTypeCode) {
        return baseFieldMapper.selectAllByEntityTypeCode(entityTypeCode);
    }

    @Override
    public EntityTypeBaseFieldDO getBaseFieldByCode(String entityTypeCode, String fieldCode) {
        return baseFieldMapper.selectByEntityTypeCodeAndFieldCode(entityTypeCode, fieldCode);
    }

    @Override
    public boolean existsFieldCode(String entityTypeCode, String fieldCode) {
        return baseFieldMapper.existsByFieldCode(entityTypeCode, fieldCode, null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateBaseFieldStatus(Long id, Integer status) {
        EntityTypeBaseFieldDO field = baseFieldMapper.selectById(id);
        if (field == null) {
            throw new ServiceException(404, "固定列字段不存在");
        }
        Integer previous = field.getStatus();
        field.setStatus(status);
        baseFieldMapper.updateById(field);
        if (status != null && status == 0) {
            syncDedicatedTableDeprecateColumn(field);
        } else if (status != null && status == 1 && (previous == null || previous != 1)) {
            syncDedicatedTableAddColumn(field);
        }
        notifyEntityTypeFieldDefinitionChanged(field.getEntityTypeCode());
    }

    /** 专用表：基础字段新增 → 固定列（列名=字段编码规范化）。 */
    private void syncDedicatedTableAddColumn(EntityTypeBaseFieldDO field) {
        if (field == null) {
            return;
        }
        String dt = field.getDataType() == null ? "" : field.getDataType().trim().toUpperCase().replace('-', '_');
        if ("REF_MULTI".equals(dt) || "ENTITY_REF_MULTI".equals(dt) || "BATCH_ENTITY_REF".equals(dt)) {
            return;
        }
        String tableName = resolveDedicatedTableName(field.getEntityTypeCode());
        if (tableName == null) {
            return;
        }
        dynamicTableService.addColumn(tableName, field);
    }

    /** 专用表：基础字段删除/停用 → 废弃列（_deprecated_ 前缀）。 */
    private void syncDedicatedTableDeprecateColumn(EntityTypeBaseFieldDO field) {
        String tableName = resolveDedicatedTableName(field.getEntityTypeCode());
        if (tableName == null || field == null || StrUtil.isBlank(field.getFieldCode())) {
            return;
        }
        String column = EntityBaseFieldColumnNames.toColumnName(field.getFieldCode());
        dynamicTableService.deprecateColumn(tableName, column);
    }

    private String resolveDedicatedTableName(String entityTypeCode) {
        if (StrUtil.isBlank(entityTypeCode)) {
            return null;
        }
        EntityTypeDO entityType = entityTypeMapper.selectByCode(entityTypeCode.trim());
        if (entityType == null) {
            return null;
        }
        StorageTypeEnum storage = StorageTypeEnum.getByCode(entityType.getStorageType());
        if (storage == null || !storage.isDedicated()) {
            return null;
        }
        String table;
        if (StrUtil.isNotBlank(entityType.getDedicatedTableName())) {
            table = cn.cheers.x.module.dynamicbusiness.framework.tenant.TenantPhysicalTableNames
                    .ensureTenantSuffix(entityType.getDedicatedTableName().trim());
        } else {
            String code = StrUtil.isNotBlank(entityType.getBaseEntityTypeCode())
                    ? entityType.getBaseEntityTypeCode()
                    : entityType.getCode();
            table = cn.cheers.x.module.dynamicbusiness.framework.tenant.TenantPhysicalTableNames
                    .entityPhysicalTable(code);
        }
        if (!dynamicTableService.tableExists(table)) {
            log.warn("专用表 {} 不存在，跳过基础字段改表 entityType={}", table, entityTypeCode);
            return null;
        }
        return table;
    }

    @Override
    public Long countByEntityTypeCode(String entityTypeCode) {
        return baseFieldMapper.countByEntityTypeCode(entityTypeCode);
    }

    @Override
    public List<String> getFieldCodes(String entityTypeCode) {
        return baseFieldMapper.selectList(new LambdaQueryWrapperX<EntityTypeBaseFieldDO>()
                .select(EntityTypeBaseFieldDO::getFieldCode)
                .eq(EntityTypeBaseFieldDO::getEntityTypeCode, entityTypeCode)
                .eq(EntityTypeBaseFieldDO::getStatus, 1))
                .stream().map(EntityTypeBaseFieldDO::getFieldCode).toList();
    }

    @Override
    public String validateFieldValue(String entityTypeCode, String fieldCode, Object value) {
        EntityTypeBaseFieldDO field = getBaseFieldByCode(entityTypeCode, fieldCode);
        if (field == null) {
            return "字段不存在";
        }
        if (!field.isEnabled()) {
            return "字段已禁用";
        }
        return null;
    }

    @Override
    public List<EntityTypePlatformFieldRespVO> listPlatformFields(String entityTypeCode) {
        EntityTypeDO entityType = requireEntityType(entityTypeCode);
        return List.of(
                buildPlatformField(entityType, "name", "名称", "TEXT"),
                buildPlatformField(entityType, "status", "状态", "NUMBER"));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePlatformFieldLabel(String entityTypeCode, String fieldCode, String label) {
        applyPlatformFieldLabel(entityTypeCode, fieldCode, label);
        notifyEntityTypeFieldDefinitionChanged(entityTypeCode);
    }

    private void applyPlatformFieldLabel(String entityTypeCode, String fieldCode, String label) {
        EntityTypeDO entityType = requireEntityType(entityTypeCode);
        if (!isSupportedPlatformField(fieldCode)) {
            throw new ServiceException(400, "不支持的系统字段: " + fieldCode);
        }
        EntityTypeFieldLabelHelper.writeLabel(entityType, fieldCode, label);
        entityTypeMapper.updateById(entityType);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteBaseFieldByAssignment(String entityTypeCode, Long libraryFieldId, String fieldCode) {
        requireEntityType(entityTypeCode);
        EntityTypeBaseFieldDO registered = resolveRegisteredBaseField(entityTypeCode, libraryFieldId, fieldCode);
        if (registered != null) {
            deleteBaseField(registered.getId());
            return;
        }
        Long resolvedLibraryFieldId = resolveLibraryFieldId(libraryFieldId, fieldCode);
        if (resolvedLibraryFieldId == null) {
            throw new ServiceException(404, "未找到该基础字段配置");
        }
        baseFieldLibrarySyncService.removeLibraryFieldFromAllModels(entityTypeCode, resolvedLibraryFieldId);
        notifyEntityTypeFieldDefinitionChanged(entityTypeCode);
    }

    private FieldDO requireLibraryField(Long libraryFieldId) {
        FieldDO libraryField = fieldMapper.selectById(libraryFieldId);
        if (libraryField == null) {
            throw exception(BASE_FIELD_LIBRARY_NOT_FOUND);
        }
        return libraryField;
    }

    private FieldDO resolveLibraryFieldForUpdate(EntityTypeBaseFieldDO existing, Long libraryFieldId) {
        if (libraryFieldId != null) {
            return requireLibraryField(libraryFieldId);
        }
        if (existing.getLibraryFieldId() != null) {
            return fieldMapper.selectById(existing.getLibraryFieldId());
        }
        if (!StringUtils.hasText(existing.getFieldCode())) {
            return null;
        }
        return fieldMapper.selectByCode(existing.getFieldCode().trim());
    }

    private boolean existsByLibraryField(String entityTypeCode, Long libraryFieldId, Long excludeId) {
        if (libraryFieldId == null) {
            return false;
        }
        return baseFieldMapper.selectCount(new LambdaQueryWrapperX<EntityTypeBaseFieldDO>()
                .eq(EntityTypeBaseFieldDO::getEntityTypeCode, entityTypeCode)
                .eq(EntityTypeBaseFieldDO::getLibraryFieldId, libraryFieldId)
                .neIfPresent(EntityTypeBaseFieldDO::getId, excludeId)) > 0;
    }

    private EntityTypeBaseFieldDO buildBaseFieldFromLibrary(
            EntityTypeBaseFieldSaveReqVO reqVO, FieldDO libraryField) {
        EntityTypeBaseFieldDO field = new EntityTypeBaseFieldDO();
        field.setEntityTypeCode(reqVO.getEntityTypeCode());
        applyLibraryMetadata(field, libraryField);
        field.setRequired(reqVO.getRequired());
        field.setIsSearchable(defaultRuleFlag(reqVO.getIsSearchable()));
        field.setIsFilterable(defaultRuleFlag(reqVO.getIsFilterable()));
        field.setIsSortable(defaultRuleFlag(reqVO.getIsSortable()));
        field.setDefaultValue(reqVO.getDefaultValue());
        field.setDescription(firstNonBlank(reqVO.getDescription(), libraryField.getDescription()));
        field.setSortOrder(reqVO.getSortOrder());
        field.setStatus(reqVO.getStatus());
        return field;
    }

    private Boolean defaultRuleFlag(Boolean value) {
        return value == null || Boolean.TRUE.equals(value);
    }

    private void applyLibraryMetadata(EntityTypeBaseFieldDO field, FieldDO libraryField) {
        syncLibraryIdentity(field, libraryField);
        field.setFieldName(libraryField.getName());
    }

    /** 更新时同步字段库身份，保留已有显示名称别名 */
    private void syncLibraryIdentity(EntityTypeBaseFieldDO field, FieldDO libraryField) {
        field.setLibraryFieldId(libraryField.getId());
        field.setFieldCode(libraryField.getCode());
        field.setDataType(toBaseDataType(libraryField.getType()));
        if (field.getDescription() == null) {
            field.setDescription(libraryField.getDescription());
        }
        field.setTypeConfig(libraryField.getOptions());
    }

    private void assertMetadataReadonly(
            EntityTypeBaseFieldSaveReqVO reqVO, FieldDO libraryField, EntityTypeBaseFieldDO existing) {
        if (libraryField == null) {
            return;
        }
        String expectedFieldCode = libraryField.getCode();
        String expectedDataType = toBaseDataType(libraryField.getType());
        boolean metadataChanged = (reqVO.getFieldCode() != null && !Objects.equals(reqVO.getFieldCode(), expectedFieldCode))
                || (reqVO.getDataType() != null && !Objects.equals(reqVO.getDataType(), expectedDataType))
                || (reqVO.getTypeConfig() != null && !Objects.equals(reqVO.getTypeConfig(), libraryField.getOptions()));
        if (metadataChanged) {
            throw exception(BASE_FIELD_LIBRARY_METADATA_READONLY);
        }
        if (reqVO.getFieldCode() != null && !Objects.equals(existing.getFieldCode(), expectedFieldCode)) {
            throw exception(BASE_FIELD_LIBRARY_METADATA_READONLY);
        }
    }

    /** 字段库类型 → 固定列 data_type（与 BaseFieldLibraryTypes 同口径，内联避免运行时缺类）。 */
    private static String toBaseDataType(String libraryType) {
        if (libraryType == null || libraryType.isBlank()) {
            return "TEXT";
        }
        return switch (libraryType.trim()) {
            case "STRING", "TEXT", "LONG_TEXT" -> "TEXT";
            case "NUMBER", "DECIMAL", "INTEGER" -> "NUMBER";
            case "DATE" -> "DATE";
            case "DATETIME" -> "DATETIME";
            case "BOOLEAN" -> "BOOLEAN";
            case "ENUM" -> "ENUM";
            case "JSON" -> "JSON";
            case "ENTITY_REF" -> "REF";
            case "ENTITY_REF_MULTI" -> "REF_Multi";
            default -> libraryType.trim();
        };
    }

    private EntityTypeBaseFieldRespVO enrichLibraryInfo(EntityTypeBaseFieldRespVO vo) {
        if (vo == null) {
            return null;
        }
        vo.setFieldSource("BASE");
        vo.setEditable(false);
        vo.setDeletable(false);
        FieldDO libraryField = vo.getLibraryFieldId() != null
                ? fieldMapper.selectById(vo.getLibraryFieldId())
                : (StringUtils.hasText(vo.getFieldCode()) ? fieldMapper.selectByCode(vo.getFieldCode().trim()) : null);
        if (libraryField != null) {
            vo.setLibraryFieldId(libraryField.getId());
            vo.setLibraryFieldCode(libraryField.getCode());
            vo.setLibraryFieldName(libraryField.getName());
        }
        return vo;
    }

    private EntityTypeDO requireEntityType(String entityTypeCode) {
        if (entityTypeCode == null || entityTypeCode.isBlank()) {
            throw new ServiceException(400, "业务类型编码不能为空");
        }
        EntityTypeDO entityType = entityTypeMapper.selectByCode(entityTypeCode.trim());
        if (entityType == null) {
            throw new ServiceException(404, "业务类型不存在");
        }
        return entityType;
    }

    private EntityTypePlatformFieldRespVO buildPlatformField(
            EntityTypeDO entityType, String fieldCode, String defaultLabel, String dataType) {
        EntityTypePlatformFieldRespVO vo = new EntityTypePlatformFieldRespVO();
        vo.setFieldCode(fieldCode);
        vo.setDefaultLabel(defaultLabel);
        vo.setFieldName(EntityTypeFieldLabelHelper.resolveLabel(entityType, fieldCode, defaultLabel));
        vo.setDataType(dataType);
        vo.setFieldSource("SYSTEM");
        vo.setDeletable(false);
        vo.setAliasEditable(true);
        applyPlatformFieldRules(vo, fieldCode);
        return vo;
    }

    private void applyPlatformFieldRules(EntityTypePlatformFieldRespVO vo, String fieldCode) {
        // 与 BusinessCapabilityServiceImpl 内置列能力对齐：name 可搜可排；status 可排可筛
        if ("name".equals(fieldCode)) {
            vo.setRequired(true);
            vo.setIsSearchable(true);
            vo.setIsSortable(true);
            vo.setIsFilterable(false);
            return;
        }
        if ("status".equals(fieldCode)) {
            vo.setRequired(true);
            vo.setIsSearchable(false);
            vo.setIsSortable(true);
            vo.setIsFilterable(true);
            return;
        }
        vo.setRequired(true);
        vo.setIsSearchable(true);
        vo.setIsSortable(true);
        vo.setIsFilterable(true);
    }

    private boolean isSupportedPlatformField(String fieldCode) {
        return "name".equals(fieldCode) || "status".equals(fieldCode) || "code".equals(fieldCode);
    }

    private EntityTypeBaseFieldDO resolveRegisteredBaseField(
            String entityTypeCode, Long libraryFieldId, String fieldCode) {
        if (libraryFieldId != null) {
            EntityTypeBaseFieldDO byLibraryId = baseFieldMapper.selectOne(
                    new LambdaQueryWrapperX<EntityTypeBaseFieldDO>()
                            .eq(EntityTypeBaseFieldDO::getEntityTypeCode, entityTypeCode)
                            .eq(EntityTypeBaseFieldDO::getLibraryFieldId, libraryFieldId));
            if (byLibraryId != null) {
                return byLibraryId;
            }
        }
        if (!StringUtils.hasText(fieldCode)) {
            return null;
        }
        return baseFieldMapper.selectByEntityTypeCodeAndFieldCode(entityTypeCode, fieldCode.trim());
    }

    private Long resolveLibraryFieldId(Long libraryFieldId, String fieldCode) {
        if (libraryFieldId != null) {
            return libraryFieldId;
        }
        if (!StringUtils.hasText(fieldCode)) {
            return null;
        }
        FieldDO libraryField = fieldMapper.selectByCode(fieldCode.trim());
        return libraryField != null ? libraryField.getId() : null;
    }

    private static String firstNonBlank(String primary, String fallback) {
        if (primary != null && !primary.isBlank()) {
            return primary;
        }
        return fallback;
    }
}
