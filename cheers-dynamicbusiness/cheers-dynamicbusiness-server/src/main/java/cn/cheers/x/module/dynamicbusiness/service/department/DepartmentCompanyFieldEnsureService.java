package cn.cheers.x.module.dynamicbusiness.service.department;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.framework.security.core.util.SecurityFrameworkUtils;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entitytype.vo.EntityTypeBaseFieldSaveReqVO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entitytype.EntityTypeBaseFieldDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.field.FieldDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.model.ModelDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.model.ModelFieldAssignmentDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.entitytype.EntityTypeBaseFieldMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.field.FieldMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.model.ModelFieldAssignmentMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.model.ModelMapper;
import cn.cheers.x.module.dynamicbusiness.framework.facility.FacilityOwningFieldCodes;
import cn.cheers.x.module.dynamicbusiness.service.entitytype.EntityTypeBaseFieldService;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 部门目录挂上「所属公司」，并让所属场站可以空着。
 * 两处只填一处由保存部门时校验。这里不改其它目录的所属场站。
 */
@Service
public class DepartmentCompanyFieldEnsureService {

    @Resource
    private FieldMapper fieldMapper;
    @Resource
    private EntityTypeBaseFieldMapper baseFieldMapper;
    @Resource
    @Lazy
    private EntityTypeBaseFieldService entityTypeBaseFieldService;
    @Resource
    private ModelMapper modelMapper;
    @Resource
    private ModelFieldAssignmentMapper modelFieldAssignmentMapper;

    @Transactional(rollbackFor = Exception.class)
    public void ensure() {
        FieldDO libraryField = ensureLibraryField();
        EntityTypeBaseFieldDO existing = baseFieldMapper.selectByEntityTypeCodeAndFieldCode(
                DepartmentCompanyCodes.ENTITY_TYPE, DepartmentCompanyCodes.FIELD_CODE);
        if (existing == null) {
            EntityTypeBaseFieldSaveReqVO request = new EntityTypeBaseFieldSaveReqVO();
            request.setEntityTypeCode(DepartmentCompanyCodes.ENTITY_TYPE);
            request.setLibraryFieldId(libraryField.getId());
            request.setRequired(false);
            request.setIsSearchable(false);
            request.setIsFilterable(true);
            request.setIsSortable(false);
            request.setFieldName(DepartmentCompanyCodes.DISPLAY_NAME);
            request.setDescription("挂在公司或分公司上的部门写这里，所属场站空着");
            entityTypeBaseFieldService.createBaseField(request);
        }
        relaxFacilityRequired();
    }

    private FieldDO ensureLibraryField() {
        FieldDO existing = fieldMapper.selectByCode(DepartmentCompanyCodes.FIELD_CODE);
        if (existing != null) {
            if (!"ENTITY_REF".equalsIgnoreCase(existing.getType())) {
                throw new ServiceException(400, "字段 company_id 已经存在，但不是关联");
            }
            return existing;
        }
        Long currentUserId = SecurityFrameworkUtils.getLoginUserId();
        if (currentUserId == null) {
            throw new ServiceException(401, "未获取到当前登录用户");
        }
        FieldDO field = new FieldDO();
        field.setCode(DepartmentCompanyCodes.FIELD_CODE);
        field.setName(DepartmentCompanyCodes.DISPLAY_NAME);
        field.setType("ENTITY_REF");
        field.setProviderCode("dynamic-entity:" + DepartmentCompanyCodes.TARGET);
        field.setSource("SYSTEM");
        field.setStatus(1);
        field.setGovernanceStatus("COMPANY");
        field.setCreatorUserId(currentUserId);
        field.setDescription(DepartmentCompanyCodes.DISPLAY_NAME);
        fieldMapper.insert(field);
        return field;
    }

    /** 部门目录的所属场站改为可空。保存时仍要求场站和公司只填一处。 */
    private void relaxFacilityRequired() {
        EntityTypeBaseFieldDO facility = baseFieldMapper.selectByEntityTypeCodeAndFieldCode(
                DepartmentCompanyCodes.ENTITY_TYPE, FacilityOwningFieldCodes.FIELD_CODE);
        if (facility != null && Boolean.TRUE.equals(facility.getRequired())) {
            facility.setRequired(false);
            baseFieldMapper.updateById(facility);
        }
        List<ModelDO> models = modelMapper.selectByEntityTypeCode(DepartmentCompanyCodes.ENTITY_TYPE);
        if (models == null) {
            return;
        }
        for (ModelDO model : models) {
            if (model == null || model.getId() == null) {
                continue;
            }
            List<ModelFieldAssignmentDO> assignments = modelFieldAssignmentMapper.selectByModelId(model.getId());
            if (assignments == null) {
                continue;
            }
            for (ModelFieldAssignmentDO assignment : assignments) {
                if (assignment == null || !FacilityOwningFieldCodes.FIELD_CODE.equals(assignment.getFieldCode())) {
                    continue;
                }
                if (Boolean.TRUE.equals(assignment.getRequired())) {
                    assignment.setRequired(false);
                    modelFieldAssignmentMapper.updateById(assignment);
                }
            }
        }
    }
}
