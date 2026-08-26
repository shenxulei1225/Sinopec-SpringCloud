package cn.cheers.x.module.dynamicbusiness.service.model.governance;

import cn.cheers.x.module.dynamicbusiness.dal.dataobject.model.ModelDO;

/**
 * 型号治理命令服务。
 *
 * <p>本服务是型号治理身份、硬删除权限和公司规格停用的写入权威。
 * 普通型号更新不得改写治理身份，也不得绕过本服务停用公司规格。</p>
 */
public interface ModelGovernanceCommandService {

    /**
     * 在型号首次写入前定稿治理身份。
     *
     * @param model 待创建型号；本方法只写治理字段，不落库
     * @param requestedGovernanceStatus 客户端期望身份；无公司规格能力时强制按本地型号创建
     * @param effectiveFacilityId 当前有效站场；创建本地型号时必填
     * @param currentUserId 当前登录用户；创建时必填
     */
    void prepareForCreate(ModelDO model, String requestedGovernanceStatus,
                          Long effectiveFacilityId, Long currentUserId);

    /**
     * 硬删除当前用户在当前站场创建且尚未被实体占用的本地型号。
     *
     * <p>公司规格始终拒绝硬删除；本方法负责清理型号字段分配、分类关系和型号本体。</p>
     */
    void deleteOwnLocal(Long modelId, Long effectiveFacilityId, Long currentUserId);

    /**
     * 停用公司规格。只有具备公司规格停用能力的调用方可以执行。
     */
    void deactivateCompany(Long modelId);

    /**
     * 校验普通更新没有改变治理身份，也没有借普通更新停用公司规格。
     */
    void validateRegularUpdate(ModelDO existingModel, String requestedGovernanceStatus, Integer requestedStatus);
}
