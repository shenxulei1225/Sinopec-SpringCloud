package cn.cheers.x.module.dynamicbusiness.service.entity.refcategory;

/**
 * 分类–实体关联变更后，反方向回写主体上的引用，使「挂在哪个分类」与引用字段一致。
 *
 * <p>与 {@link EntityRefCategoryProjectionService} 对向：那边是改引用 → 投影分类–实体；
 * 这边是树上挂/卸分类 → 回写引用。</p>
 *
 * <p>现网已实现：分类节点绑了台账（高级分类）且主体有唯一匹配该台账类型的引用时，写成 / 清成该绑定实体。
 * 扩展预留：主体若有「引用普通分类」字段（目标即 categoryId），挂/卸时同样应对齐该字段——
 * 不得用「简单分类一律不回写」堵死这条产品路径；无任何可对齐引用时才跳过。</p>
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
