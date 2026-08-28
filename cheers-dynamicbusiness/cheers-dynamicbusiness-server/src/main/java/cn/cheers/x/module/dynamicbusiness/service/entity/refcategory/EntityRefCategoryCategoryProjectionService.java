package cn.cheers.x.module.dynamicbusiness.service.entity.refcategory;

/**
 * REF 投影第④步：在组合 3（宿主分类即实体 + 成员纯分类）下，把成员侧纯分类节点挂到宿主分类（分类—分类）。
 *
 * <p>与 {@link EntityRefCategoryProjectionService} 分工：后者只 orchestrate 分类—实体（②③ 由
 * {@code EntityCategoryRelationService} 完成）；本服务只写分类—分类，且<strong>不解绑</strong>。</p>
 *
 * <p>权威文档：{@code docs/ecs-react/数据管理/关联/查数规则表.md}「REF 投影展开与分类—分类裁树」。</p>
 */
public interface EntityRefCategoryCategoryProjectionService {

    /**
     * REF 挂上分类即实体目标并完成分类—实体投影后调用：按主体型号在成员纯分类—型号上的节点，
     * upsert 宿主分类—成员分类（分类—分类）。
     *
     * <p>仅在宿主种类为 ADVANCED、成员种类为 SIMPLE 且跨种类时执行；REF 解绑时不调用本方法。</p>
     *
     * @param subjectModelId      主体实体型号；无型号则跳过
     * @param subjectEntityTypeCode 主体数据类型编码（入口码可传，内部归一存储类型）
     * @param hostCategoryId      REF 目标实体 link 上的宿主分类节点 id
     * @param hostCategoryTypeCode REF 目标实体类型（通常与宿主分类种类一致，如 region）
     */
    void syncCategoryCategoryOnRefAssociate(
            Long subjectModelId,
            String subjectEntityTypeCode,
            Long hostCategoryId,
            String hostCategoryTypeCode);
}
