package cn.cheers.x.module.dynamicbusiness.service.useraccount;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.framework.security.core.util.SecurityFrameworkUtils;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entitytype.vo.EntityTypeBaseFieldSaveReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entitytype.vo.EntityTypeCreateReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.model.vo.ModelCreateReqVO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.field.FieldDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.model.ModelDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.model.ModelFieldAssignmentDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.entitytype.EntityTypeBaseFieldMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.entitytype.EntityTypeMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.field.FieldMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.model.ModelFieldAssignmentMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.model.ModelMapper;
import cn.cheers.x.module.dynamicbusiness.service.capability.BusinessCapabilityService;
import cn.cheers.x.module.dynamicbusiness.service.model.ModelFieldAssignmentService;
import cn.cheers.x.module.dynamicbusiness.service.entitytype.BaseFieldLibrarySyncService;
import cn.cheers.x.module.dynamicbusiness.service.entitytype.EntityTypeBaseFieldService;
import cn.cheers.x.module.dynamicbusiness.service.entitytype.EntityTypeService;
import cn.cheers.x.module.dynamicbusiness.service.model.ModelService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 准备用户目录：手机、所属场站、所属部门、可进入的业务租户，以及名为「用户」的型号。
 * 只在明确准备目录时写入。打开页面、保存实体都不调用。
 * 目录作用域是全网。所属场站不由站场级类型强制必填。
 * 密码是调用服务字段，只出现在创建，不写成用户表上的一列。
 */
@Service
public class UserAccountCatalogEnsure {

    @Resource
    private EntityTypeMapper entityTypeMapper;
    @Resource
    private EntityTypeService entityTypeService;
    @Resource
    private FieldMapper fieldMapper;
    @Resource
    private EntityTypeBaseFieldMapper baseFieldMapper;
    @Resource
    private EntityTypeBaseFieldService entityTypeBaseFieldService;
    @Resource
    private ModelMapper modelMapper;
    @Resource
    private ModelService modelService;
    @Resource
    private BaseFieldLibrarySyncService baseFieldLibrarySyncService;
    @Resource
    private ModelFieldAssignmentService modelFieldAssignmentService;
    @Resource
    private ModelFieldAssignmentMapper modelFieldAssignmentMapper;
    @Resource
    private BusinessCapabilityService businessCapabilityService;

    @Transactional(rollbackFor = Exception.class)
    public Long ensureModelId() {
        ensureCatalog();
        ensureTextField(UserAccountCodes.MOBILE, "手机");
        ensureTextField(UserAccountCodes.VISIT_TENANT_IDS, "可进入的业务租户");
        ensureEntityRefField(UserAccountCodes.STATION, "所属场站", UserAccountCodes.STATION_TARGET);
        ensureEntityRefField(UserAccountCodes.DEPARTMENT, "所属部门", UserAccountCodes.DEPARTMENT_TARGET);
        // 已有人员可能还没有手机。空着就空着。新建时页面仍要求填写。
        attachBaseField(UserAccountCodes.MOBILE, "手机", false);
        attachBaseField(UserAccountCodes.STATION, "所属场站", false);
        attachBaseField(UserAccountCodes.DEPARTMENT, "所属部门", false);
        attachBaseField(UserAccountCodes.VISIT_TENANT_IDS, "可进入的业务租户", false);
        Long modelId = ensureModel();
        ensurePasswordField(modelId);
        return modelId;
    }

    private void ensureCatalog() {
        if (entityTypeMapper.selectByCode(UserAccountCodes.ENTITY_TYPE_CODE) != null) {
            return;
        }
        EntityTypeCreateReqVO request = new EntityTypeCreateReqVO();
        request.setSort(80);
        request.setName("用户");
        request.setCode(UserAccountCodes.ENTITY_TYPE_CODE);
        request.setAlias("用户");
        request.setStatus("active");
        request.setStorageType("DEDICATED");
        request.setEntryKind("NATIVE");
        request.setWorkScope("NETWORK");
        request.setGroupName("用户");
        request.setDescription("全系统只在这里维护用户");
        request.setAssociationFields("{}");
        entityTypeService.create(request);
    }

    private void ensureTextField(String code, String name) {
        FieldDO existing = fieldMapper.selectByCode(code);
        if (existing != null) {
            if (!"TEXT".equalsIgnoreCase(existing.getType())) {
                throw new ServiceException(400, "字段 " + code + " 已经存在，但不是文本");
            }
            return;
        }
        Long currentUserId = SecurityFrameworkUtils.getLoginUserId();
        if (currentUserId == null) {
            throw new ServiceException(401, "未获取到当前登录用户");
        }
        FieldDO field = new FieldDO();
        field.setCode(code);
        field.setName(name);
        field.setType("TEXT");
        field.setSource("SYSTEM");
        field.setStatus(1);
        field.setGovernanceStatus("COMPANY");
        field.setCreatorUserId(currentUserId);
        field.setDescription(name);
        fieldMapper.insert(field);
    }

    private void ensureEntityRefField(String code, String name, String targetEntityType) {
        FieldDO existing = fieldMapper.selectByCode(code);
        if (existing != null) {
            if (!"ENTITY_REF".equalsIgnoreCase(existing.getType())) {
                throw new ServiceException(400, "字段 " + code + " 已经存在，但不是关联");
            }
            return;
        }
        Long currentUserId = SecurityFrameworkUtils.getLoginUserId();
        if (currentUserId == null) {
            throw new ServiceException(401, "未获取到当前登录用户");
        }
        FieldDO field = new FieldDO();
        field.setCode(code);
        field.setName(name);
        field.setType("ENTITY_REF");
        field.setProviderCode("dynamic-entity:" + targetEntityType);
        field.setSource("SYSTEM");
        field.setStatus(1);
        field.setGovernanceStatus("COMPANY");
        field.setCreatorUserId(currentUserId);
        field.setDescription(name);
        fieldMapper.insert(field);
    }

    private void ensurePasswordField(Long modelId) {
        FieldDO field = fieldMapper.selectByCode(UserAccountCodes.PASSWORD);
        if (field == null) {
            Long currentUserId = SecurityFrameworkUtils.getLoginUserId();
            if (currentUserId == null) {
                throw new ServiceException(401, "未获取到当前登录用户");
            }
            field = new FieldDO();
            field.setCode(UserAccountCodes.PASSWORD);
            field.setName("密码");
            field.setType("SERVICE_CALL");
            field.setOptions("{\"serviceCode\":\"auth\",\"operationCode\":\"savePassword\"}");
            field.setSource("SYSTEM");
            field.setStatus(1);
            field.setGovernanceStatus("COMPANY");
            field.setCreatorUserId(currentUserId);
            field.setDescription("创建时交给认证保存，不写入用户记录");
            fieldMapper.insert(field);
        }
        modelFieldAssignmentService.assignFieldToModel(
                modelId, field.getId(), false, false, false, false, null, null);
        ModelFieldAssignmentDO assignment = modelFieldAssignmentMapper.selectByModelIdAndFieldId(modelId, field.getId());
        if (assignment == null) {
            throw new ServiceException(500, "密码字段没有挂到用户型号上");
        }
        assignment.setCreateVisible(true);
        assignment.setEditVisible(false);
        assignment.setDetailVisible(false);
        modelFieldAssignmentMapper.updateById(assignment);
        businessCapabilityService.refreshModelCrudFormDefinition(modelId);
    }

    private void attachBaseField(String code, String name, boolean required) {
        if (baseFieldMapper.selectByEntityTypeCodeAndFieldCode(UserAccountCodes.ENTITY_TYPE_CODE, code) != null) {
            return;
        }
        FieldDO libraryField = fieldMapper.selectByCode(code);
        if (libraryField == null || libraryField.getId() == null) {
            throw new ServiceException(400, "字段库缺少 " + code);
        }
        EntityTypeBaseFieldSaveReqVO request = new EntityTypeBaseFieldSaveReqVO();
        request.setEntityTypeCode(UserAccountCodes.ENTITY_TYPE_CODE);
        request.setLibraryFieldId(libraryField.getId());
        request.setRequired(required);
        request.setIsSearchable(false);
        request.setIsFilterable(true);
        request.setIsSortable(false);
        request.setFieldName(name);
        request.setDescription(name);
        entityTypeBaseFieldService.createBaseField(request);
    }

    private Long ensureModel() {
        ModelDO named = modelMapper.selectByNameAndEntityTypeCode(
                UserAccountCodes.MODEL_NAME, UserAccountCodes.ENTITY_TYPE_CODE);
        if (named != null) {
            baseFieldLibrarySyncService.assignAllBaseFieldsToModel(named.getId());
            return named.getId();
        }
        if (modelMapper.selectCountByEntityTypeCode(UserAccountCodes.ENTITY_TYPE_CODE) > 0) {
            throw new ServiceException(400, "用户目录里没有名为「用户」的型号");
        }
        ModelCreateReqVO request = new ModelCreateReqVO();
        request.setName(UserAccountCodes.MODEL_NAME);
        request.setEntityTypeCode(UserAccountCodes.ENTITY_TYPE_CODE);
        request.setStatus(1);
        request.setGovernanceStatus("COMPANY");
        request.setDescription("用户目录上的人");
        return modelService.createModel(request);
    }
}
