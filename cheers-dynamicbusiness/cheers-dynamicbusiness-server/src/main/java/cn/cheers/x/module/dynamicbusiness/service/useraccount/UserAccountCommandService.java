package cn.cheers.x.module.dynamicbusiness.service.useraccount;

import cn.cheers.x.framework.common.enums.UserTypeEnum;
import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityCreateReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityDeleteReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityUpdateReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.useraccount.vo.UserAccountCreateReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.useraccount.vo.UserAccountProfileUpdateReqVO;
import cn.cheers.x.module.dynamicbusiness.framework.facility.FacilityOwningFieldCodes;
import cn.cheers.x.module.dynamicbusiness.service.category.CategoryUserRelationService;
import cn.cheers.x.module.dynamicbusiness.service.entity.EntityService;
import cn.cheers.x.system.service.auth.LoginCredentialService;
import cn.cheers.x.system.service.oauth2.OAuth2TokenService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 新建用户。用户目录写下资料。口令摘要在认证服务里。
 * 认证服务跑在系统进程，本进程启动时不创建它。调用时再找，找不到就报错，不在这里补一条口令。
 * 口令没写成，或部门没挂上时，删掉刚写下的用户，不留下没有口令的人。
 * 不写系统部门，不写角色。
 */
@Service
public class UserAccountCommandService {

    @Resource
    private UserAccountCatalogEnsure userAccountCatalogEnsure;
    @Resource
    private EntityService entityService;
    @Resource
    private ObjectProvider<LoginCredentialService> loginCredentialService;
    @Resource
    private CategoryUserRelationService categoryUserRelationService;
    @Resource
    private UserAccountQueryService userAccountQueryService;
    @Resource
    private ObjectProvider<OAuth2TokenService> oauth2TokenService;

    public Long create(UserAccountCreateReqVO request) {
        UserAccountCreateRules.check(request);
        Long modelId = userAccountCatalogEnsure.ensureModelId();
        Long entityId = null;
        try {
            entityId = entityService.create(toEntityCreate(request, modelId));
            credentials().saveNew(entityId, request.getPassword());
            if (request.getDepartmentCategoryId() != null) {
                categoryUserRelationService.bindUsers(
                        request.getDepartmentCategoryId(), List.of(entityId));
            }
            return entityId;
        } catch (RuntimeException ex) {
            removeIncompleteUser(entityId, ex);
            throw ex;
        }
    }

    /**
     * 改昵称、手机、状态。不改账号，不改部门，不改口令。
     * 改成停用时作废这个编号上已经发出的登录令牌。
     */
    public void updateProfile(UserAccountProfileUpdateReqVO request) {
        if (request == null || request.getId() == null) {
            throw new ServiceException(400, "用户编号不能为空");
        }
        String nickname = request.getNickname() == null ? "" : request.getNickname().trim();
        String mobile = request.getMobile() == null ? "" : request.getMobile().trim();
        UserAccountCreateRules.checkProfile(nickname, mobile, request.getStatus());
        EntityRespVO current = userAccountQueryService.requireCatalogUser(request.getId());
        Map<String, Object> baseFields = new LinkedHashMap<>();
        if (current.getBaseFields() != null) {
            baseFields.putAll(current.getBaseFields());
        }
        baseFields.put("entityTypeCode", UserAccountCodes.ENTITY_TYPE_CODE);
        baseFields.put("modelId", current.getModelId());
        baseFields.put("name", nickname);
        baseFields.put(UserAccountCodes.MOBILE, mobile);
        baseFields.put("status", request.getStatus());
        EntityUpdateReqVO update = new EntityUpdateReqVO();
        update.setId(request.getId());
        update.setRegistryCode(UserAccountCodes.ENTITY_TYPE_CODE);
        update.setBaseFields(baseFields);
        if (current.getCustomFields() != null) {
            update.setCustomFields(new LinkedHashMap<>(current.getCustomFields()));
        }
        entityService.update(update);
        if (request.getStatus() == UserAccountCodes.DISABLED
                && !Integer.valueOf(UserAccountCodes.DISABLED).equals(current.getStatus())) {
            tokens().removeAccessToken(request.getId(), UserTypeEnum.ADMIN.getValue());
        }
    }

    /** 只改口令摘要。没有凭证时拒绝，不另建一条用户。 */
    public void resetPassword(Long id, String password) {
        userAccountQueryService.requireCatalogUser(id);
        UserAccountCreateRules.checkPassword(password);
        credentials().replace(id, password);
    }

    /** 认证服务不在本进程时直接报错，不在这里补一条口令。 */
    private LoginCredentialService credentials() {
        LoginCredentialService service = loginCredentialService.getIfAvailable();
        if (service == null) {
            throw new ServiceException(500, "口令写在认证服务里，这个进程没有认证服务");
        }
        return service;
    }

    private OAuth2TokenService tokens() {
        OAuth2TokenService service = oauth2TokenService.getIfAvailable();
        if (service == null) {
            throw new ServiceException(500, "登录令牌在认证服务里，这个进程没有认证服务");
        }
        return service;
    }

    private void removeIncompleteUser(Long entityId, RuntimeException original) {
        if (entityId == null) {
            return;
        }
        try {
            LoginCredentialService service = loginCredentialService.getIfAvailable();
            if (service != null) {
                service.deleteByUserId(entityId);
            }
        } catch (RuntimeException cleanup) {
            original.addSuppressed(cleanup);
        }
        try {
            EntityDeleteReqVO delete = new EntityDeleteReqVO();
            delete.setId(entityId);
            delete.setEntityTypeCode(UserAccountCodes.ENTITY_TYPE_CODE);
            delete.setForceDelete(true);
            entityService.delete(delete);
        } catch (RuntimeException cleanup) {
            original.addSuppressed(cleanup);
        }
    }

    private static EntityCreateReqVO toEntityCreate(UserAccountCreateReqVO request, Long modelId) {
        Map<String, Object> baseFields = new LinkedHashMap<>();
        baseFields.put("entityTypeCode", UserAccountCodes.ENTITY_TYPE_CODE);
        baseFields.put("modelId", modelId);
        baseFields.put("name", request.getNickname());
        baseFields.put("code", request.getAccount());
        baseFields.put("status", request.getStatus());
        baseFields.put(UserAccountCodes.MOBILE, request.getMobile());
        if (request.getFacilityId() != null) {
            baseFields.put(FacilityOwningFieldCodes.FIELD_CODE,
                    FacilityOwningFieldCodes.toApiRef(request.getFacilityId()));
        }
        if (!request.getVisitTenantIds().isEmpty()) {
            baseFields.put(UserAccountCodes.VISIT_TENANT_IDS, joinTenants(request.getVisitTenantIds()));
        }
        EntityCreateReqVO create = new EntityCreateReqVO();
        create.setRegistryCode(UserAccountCodes.ENTITY_TYPE_CODE);
        create.setBaseFields(baseFields);
        return create;
    }

    private static String joinTenants(List<Long> tenantIds) {
        StringBuilder text = new StringBuilder();
        for (Long tenantId : tenantIds) {
            if (!text.isEmpty()) {
                text.append(',');
            }
            text.append(tenantId);
        }
        return text.toString();
    }
}
