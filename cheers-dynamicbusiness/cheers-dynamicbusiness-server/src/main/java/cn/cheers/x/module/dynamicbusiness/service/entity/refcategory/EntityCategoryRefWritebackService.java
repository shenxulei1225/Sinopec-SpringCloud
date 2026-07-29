package cn.cheers.x.module.dynamicbusiness.service.entity.refcategory;

/**
 * 分类–实体关联变更后的 REF 回写（分类即实体 + 唯一匹配 REF）。
 * <p>
 * 挂上：单选写成该目标；MultiRef 加入该目标（单归属时 MultiRef 只留这一个）。
 * 解绑：单选若仍指向该目标则清空；MultiRef 只删对应那一条。
 * 与 {@link EntityRefCategoryProjectionService} 方向相反（投影是 REF→分类）。
 */
public interface EntityCategoryRefWritebackService {

    /** 关联成功后：按分类种类挂靠模式回写 REF。 */
    void afterAssociated(Long entityId, Long categoryId, String entityTypeCode);

    /**
     * 关联成功后回写 REF。
     *
     * @param associationModeOverride 分类列配置的 SINGLE/MULTI；null 时回退种类配置
     */
    default void afterAssociated(Long entityId, Long categoryId, String entityTypeCode,
                                 String associationModeOverride) {
        afterAssociated(entityId, categoryId, entityTypeCode);
    }

    /** 解绑成功后：若当前 REF 仍指向该节点绑定实体，则清空。 */
    void afterDisassociated(Long entityId, Long categoryId, String entityTypeCode);
}
