package cn.cheers.x.module.dynamicbusiness.service.entitytype;

import cn.cheers.x.module.dynamicbusiness.controller.admin.model.vo.ModelFieldAssignmentRespVO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entitytype.EntityTypeBaseFieldDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.field.FieldDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.model.ModelDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.model.ModelFieldAssignmentDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.model.ModelFieldAssignmentMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.model.ModelMapper;
import cn.cheers.x.module.dynamicbusiness.service.model.ModelFieldGroupService;
import cn.iocoder.yudao.framework.tenant.core.context.TenantContextHolder;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class BaseFieldLibrarySyncServiceImpl implements BaseFieldLibrarySyncService {

    @Resource
    private ModelMapper modelMapper;
    @Resource
    private ModelFieldAssignmentMapper modelFieldAssignmentMapper;
    @Resource
    private ModelFieldGroupService modelFieldGroupService;

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

        Long tenantId = TenantContextHolder.getTenantId();
        boolean required = Boolean.TRUE.equals(baseField.getRequired());
        boolean searchable = baseField.getIsSearchable() == null || Boolean.TRUE.equals(baseField.getIsSearchable());
        boolean filterable = baseField.getIsFilterable() == null || Boolean.TRUE.equals(baseField.getIsFilterable());
        boolean sortable = baseField.getIsSortable() == null || Boolean.TRUE.equals(baseField.getIsSortable());

        for (ModelDO model : models) {
            ModelFieldAssignmentDO exist = modelFieldAssignmentMapper.selectByModelCodeAndFieldCode(
                    model.getCode(), libraryField.getCode());
            if (exist != null) {
                exist.setFieldId(libraryField.getId());
                exist.setModelId(model.getId());
                exist.setRequired(required);
                exist.setIsSearchable(searchable);
                exist.setIsFilterable(filterable);
                exist.setIsSortable(sortable);
                exist.setSort(baseField.getSortOrder());
                exist.setFieldSource(ModelFieldAssignmentRespVO.FIELD_SOURCE_BASE);
                modelFieldAssignmentMapper.updateById(exist);
                continue;
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
            assignment.setTenantId(tenantId);
            modelFieldAssignmentMapper.insert(assignment);
        }

        log.info("[assignLibraryFieldToAllModels] entityType={}, libraryCode={}, models={}",
                entityTypeCode, libraryField.getCode(), models.size());
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
