package cn.cheers.x.module.dynamicbusiness.service.model.governance;

import cn.cheers.x.module.dynamicbusiness.dal.dataobject.model.ModelDO;

/**
 * 型号治理命令服务。
 *
 * <p>本服务是型号治理身份与删除权限的写入权威。
 * 普通型号更新不得改写治理身份。</p>
 *
 * <p>删除口径：删除即软删（{@code deleted=true}）。公司规格与本地型号都可删；
 * 有实体占用则拒绝。禁止把「删除」实现成停用（{@code status=0}）。</p>
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
     * 删除型号（软删），并清理模型字段分配与分类关联。
     *
     * <ul>
     *   <li>本地型号：仅创建人在发起站场可删；须传有效站场</li>
     *   <li>公司规格（或无发起站场的种子）：须具备全网型号治理能力</li>
     *   <li>任一身份：仍有实体占用则拒绝删除</li>
     * </ul>
     */
    void deleteModel(Long modelId, Long effectiveFacilityId, Long currentUserId);

    /**
     * @deprecated 停用已不再作为删除的替代；请走 {@link #deleteModel}。保留方法签名以免旧调用方编译失败，实现将直接拒绝。
     */
    @Deprecated
    void deactivateCompany(Long modelId);

    /**
     * 校验普通更新没有改变治理身份，也没有借普通更新改公司规格 status。
     */
    void validateRegularUpdate(ModelDO existingModel, String requestedGovernanceStatus, Integer requestedStatus);
}
