package cn.cheers.x.module.dynamicbusiness.service.useraccount;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.framework.security.core.util.SecurityFrameworkUtils;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entitytype.vo.EntityTypeBaseFieldSaveReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entitytype.vo.EntityTypeCreateReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.model.vo.ModelCreateReqVO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entitytype.EntityTypeDO;
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
import cn.cheers.x.module.dynamicbusiness.service.entitytype.EntityTypeBaseFieldService;
import cn.cheers.x.module.dynamicbusiness.service.entitytype.EntityTypeService;
import cn.cheers.x.module.dynamicbusiness.service.model.ModelService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 用户目录还没有时，补上目录、型号和创建账号要用的字段。
 * 不管什么：不在服务启动时调用。已有目录的范围、已有人员行、已有口令都不改。
 * 口令不写在用户行上。创建时由认证服务按当次提交的口令写入登录凭证。
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
    private ModelFieldAssignmentService modelFieldAssignmentService;
    @Resource
    private ModelFieldAssignmentMapper modelFieldAssignmentMapper;
    @Resource
    private BusinessCapabilityService businessCapabilityService;

    /**
     * 目录或型号还没有时补齐，然后返回型号编号。
     * 目录已经在时直接用现有范围，不改成站场级，也不给已有人员补所属场站。
     */
    @Transactional(rollbackFor = Exception.class)
    public Long ensureModelId() {
        ensureCatalog();
        ensureTextField(UserAccountCodes.MOBILE, "手机");
        ensureTextField(UserAccountCodes.VISIT_TENANT_IDS, "可进入的业务租户");
        ensureEntityRefField(UserAccountCodes.DEPARTMENT, "所属部门", UserAccountCodes.DEPARTMENT_TARGET);
        attachBaseField(UserAccountCodes.MOBILE, "手机", false);
        attachBaseField(UserAccountCodes.DEPARTMENT, "所属部门", false);
        attachBaseField(UserAccountCodes.VISIT_TENANT_IDS, "可进入的业务租户", false);
        Long modelId = ensureModel();
        ensurePasswordField(modelId);
        return modelId;
    }

    /** 没有用户目录才创建。已经有的目录保持原范围，禁止再改回站场级。 */
    private void ensureCatalog() {
        EntityTypeDO existing = entityTypeMapper.selectByCode(UserAccountCodes.ENTITY_TYPE_CODE);
        if (existing != null) {
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
        request.setWorkScope(EntityTypeDO.WORK_SCOPE_NETWORK);
        request.setGroupName("用户");
        request.setDescription("登录账号按租户分开。不按场站拆开。");
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
        ModelFieldAssignmentDO existing = modelFieldAssignmentMapper.selectByModelIdAndFieldId(modelId, field.getId());
        if (existing != null) {
            return;
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
