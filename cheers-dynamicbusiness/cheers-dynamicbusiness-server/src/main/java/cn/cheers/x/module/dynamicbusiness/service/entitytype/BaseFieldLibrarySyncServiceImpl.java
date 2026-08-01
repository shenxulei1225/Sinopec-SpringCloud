package cn.cheers.x.module.dynamicbusiness.service.entitytype;

import cn.cheers.x.module.dynamicbusiness.controller.admin.model.vo.ModelFieldAssignmentRespVO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entitytype.EntityTypeBaseFieldDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.field.FieldDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.model.ModelDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.model.ModelFieldAssignmentDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.entitytype.EntityTypeBaseFieldMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.field.FieldMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.model.ModelFieldAssignmentMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.model.ModelMapper;
import cn.cheers.x.module.dynamicbusiness.service.model.ModelFieldGroupService;
import cn.cheers.x.framework.tenant.core.context.TenantContextHolder;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
public class BaseFieldLibrarySyncServiceImpl implements BaseFieldLibrarySyncService {

    @Resource
    private ModelMapper modelMapper;
    @Resource
    private ModelFieldAssignmentMapper modelFieldAssignmentMapper;
    @Resource
    private ModelFieldGroupService modelFieldGroupService;
    @Resource
    private EntityTypeBaseFieldMapper entityTypeBaseFieldMapper;
    @Resource
    private FieldMapper fieldMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignLibraryFieldToAllModels(
            String entityTypeCode,
            FieldDO libraryField,
            EntityTypeBaseFieldDO baseField) {
        if (entityTypeCode == null || libraryField == null || baseField == null) {
            return;
        }
        List<ModelDO> models = modelMapper.selectByEntityTypeCode(entityTypeCode);
        if (models.isEmpty()) {
            return;
        }

        for (ModelDO model : models) {
            upsertBaseAssignment(model, libraryField, baseField, true);
        }

        log.info("[assignLibraryFieldToAllModels] entityType={}, libraryCode={}, models={}",
                entityTypeCode, libraryField.getCode(), models.size());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int assignAllBaseFieldsToModel(Long modelId) {
        if (modelId == null) {
            return 0;
        }
        ModelDO model = modelMapper.selectById(modelId);
        if (model == null || !StringUtils.hasText(model.getEntityTypeCode())) {
            log.warn("[assignAllBaseFieldsToModel] model missing or no entityTypeCode, modelId={}", modelId);
            return 0;
        }
        List<EntityTypeBaseFieldDO> baseFields =
                entityTypeBaseFieldMapper.selectByEntityTypeCode(model.getEntityTypeCode());
        Set<Long> liveLibraryIds = new HashSet<>();
        int inserted = 0;
        for (EntityTypeBaseFieldDO baseField : baseFields) {
            if (baseField == null) {
                continue;
            }
            FieldDO libraryField = resolveLibraryField(baseField);
            if (libraryField == null) {
                log.warn("[assignAllBaseFieldsToModel] base field has no library field, modelId={}, baseFieldId={}, fieldCode={}",
                        modelId, baseField.getId(), baseField.getFieldCode());
                continue;
            }
            liveLibraryIds.add(libraryField.getId());
            // 仅补缺：已有分配（含克隆带来的型号级规则）不覆盖
            if (upsertBaseAssignment(model, libraryField, baseField, false)) {
                inserted++;
            }
        }
        int removedOrphans = removeOrphanBaseAssignments(model, liveLibraryIds);
        log.info("[assignAllBaseFieldsToModel] modelId={}, entityType={}, baseFields={}, inserted={}, removedOrphans={}",
                modelId, model.getEntityTypeCode(), baseFields.size(), inserted, removedOrphans);
        return inserted;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int syncAllBaseFieldsForEntityType(String entityTypeCode) {
        if (!StringUtils.hasText(entityTypeCode)) {
            return 0;
        }
        List<ModelDO> models = modelMapper.selectByEntityTypeCode(entityTypeCode.trim());
        int total = 0;
        for (ModelDO model : models) {
            if (model == null || model.getId() == null) {
                continue;
            }
            total += assignAllBaseFieldsToModel(model.getId());
        }
        log.info("[syncAllBaseFieldsForEntityType] entityType={}, models={}, inserted={}",
                entityTypeCode, models.size(), total);
        return total;
    }

    /**
     * 清理型号上已失效的 BASE 分配：字段库已删，或不在当前类型启用基础字段集合中。
     */
    private int removeOrphanBaseAssignments(ModelDO model, Set<Long> liveLibraryIds) {
        if (model == null || model.getId() == null) {
            return 0;
        }
        List<ModelFieldAssignmentDO> assignments = modelFieldAssignmentMapper.selectByModelId(model.getId());
        if (assignments == null || assignments.isEmpty()) {
            return 0;
        }
        int removed = 0;
        for (ModelFieldAssignmentDO assignment : assignments) {
            if (assignment == null || assignment.getId() == null) {
                continue;
            }
            String source = assignment.getFieldSource() == null ? "" : assignment.getFieldSource().trim();
            boolean isBase = ModelFieldAssignmentRespVO.FIELD_SOURCE_BASE.equalsIgnoreCase(source)
                    || "LIBRARY".equalsIgnoreCase(source);
            if (!isBase) {
                continue;
            }
            Long fieldId = assignment.getFieldId();
            FieldDO libraryField = fieldId != null ? fieldMapper.selectById(fieldId) : null;
            boolean orphan = libraryField == null
                    || (liveLibraryIds != null && !liveLibraryIds.isEmpty() && !liveLibraryIds.contains(fieldId));
            if (!orphan) {
                continue;
            }
            modelFieldAssignmentMapper.deleteById(assignment.getId());
            if (fieldId != null) {
                modelFieldGroupService.unassignFieldFromGroup(model.getId(), fieldId);
            }
            removed++;
        }
        return removed;
    }

    /**
     * @param updateExisting true=基础字段变更后同步到已有分配；false=只插入缺失行
     * @return true 表示新插入
     */
    private boolean upsertBaseAssignment(
            ModelDO model, FieldDO libraryField, EntityTypeBaseFieldDO baseField, boolean updateExisting) {
        Long tenantId = TenantContextHolder.getTenantId();
        boolean required = Boolean.TRUE.equals(baseField.getRequired());
        boolean searchable = baseField.getIsSearchable() == null || Boolean.TRUE.equals(baseField.getIsSearchable());
        boolean filterable = baseField.getIsFilterable() == null || Boolean.TRUE.equals(baseField.getIsFilterable());
        boolean sortable = baseField.getIsSortable() == null || Boolean.TRUE.equals(baseField.getIsSortable());

        ModelFieldAssignmentDO exist = modelFieldAssignmentMapper.selectByModelIdAndFieldId(
                model.getId(), libraryField.getId());
        if (exist == null) {
            exist = modelFieldAssignmentMapper.selectByModelCodeAndFieldCode(
                    model.getCode(), libraryField.getCode());
        }
        if (exist != null) {
            if (!updateExisting) {
                return false;
            }
            exist.setFieldId(libraryField.getId());
            exist.setModelId(model.getId());
            exist.setModelCode(model.getCode());
            if (!StringUtils.hasText(exist.getFieldCode())) {
                exist.setFieldCode(libraryField.getCode());
            }
            exist.setRequired(required);
            exist.setIsSearchable(searchable);
            exist.setIsFilterable(filterable);
            exist.setIsSortable(sortable);
            exist.setSort(baseField.getSortOrder());
            exist.setFieldSource(ModelFieldAssignmentRespVO.FIELD_SOURCE_BASE);
            modelFieldAssignmentMapper.updateById(exist);
            return false;
        }

        ModelFieldAssignmentDO assignment = new ModelFieldAssignmentDO();
        assignment.setModelId(model.getId());
        assignment.setModelCode(model.getCode());
        assignment.setFieldId(libraryField.getId());
        assignment.setFieldCode(libraryField.getCode());
        assignment.setRequired(required);
        assignment.setIsSearchable(searchable);
        assignment.setIsFilterable(filterable);
        assignment.setIsSortable(sortable);
        assignment.setSort(baseField.getSortOrder());
        assignment.setFieldSource(ModelFieldAssignmentRespVO.FIELD_SOURCE_BASE);
        assignment.setTenantId(tenantId != null ? tenantId : model.getTenantId());
        modelFieldAssignmentMapper.insert(assignment);
        return true;
    }

    private FieldDO resolveLibraryField(EntityTypeBaseFieldDO baseField) {
        if (baseField.getLibraryFieldId() != null) {
            FieldDO byId = fieldMapper.selectById(baseField.getLibraryFieldId());
            if (byId != null) {
                return byId;
            }
        }
        if (StringUtils.hasText(baseField.getFieldCode())) {
            return fieldMapper.selectByCode(baseField.getFieldCode());
        }
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeLibraryFieldFromAllModels(String entityTypeCode, Long libraryFieldId) {
        if (entityTypeCode == null || entityTypeCode.isBlank() || libraryFieldId == null) {
            return;
        }
        List<ModelDO> models = modelMapper.selectByEntityTypeCode(entityTypeCode);
        if (models.isEmpty()) {
            return;
        }
        for (ModelDO model : models) {
            ModelFieldAssignmentDO assignment =
                    modelFieldAssignmentMapper.selectByModelIdAndFieldId(model.getId(), libraryFieldId);
            if (assignment != null) {
                modelFieldAssignmentMapper.deleteById(assignment.getId());
            }
            modelFieldGroupService.unassignFieldFromGroup(model.getId(), libraryFieldId);
        }
        log.info("[removeLibraryFieldFromAllModels] entityType={}, libraryFieldId={}, models={}",
                entityTypeCode, libraryFieldId, models.size());
    }
}
