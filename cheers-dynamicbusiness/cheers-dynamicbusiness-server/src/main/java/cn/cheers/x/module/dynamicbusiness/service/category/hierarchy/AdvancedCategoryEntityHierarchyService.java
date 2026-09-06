package cn.cheers.x.module.dynamicbusiness.service.category.hierarchy;

/**
 * 高级分类（分类即实体）组织层级：实体权威，分类树投影对齐。
 *
 * <p>管什么：拖分类 / 移实体时的双向对齐、创建时写入实体 parentId、存量分类→实体回填。</p>
 * <p>不负责：简单分类树、跨类型 ENTITY_REF、字段库配置。</p>
 * <p>禁止：在分类拖拽里只改分类不改实体；在实体移动后静默跳过分类对齐。</p>
 */
public interface AdvancedCategoryEntityHierarchyService {

    /**
     * 高级分类节点改父：先移实体（权威），再移分类（投影）。
     * 无实体绑定的结构根 / 简单分类调用方不应进来；本方法对无 link 节点仅移分类。
     *
     * @param categoryId          被移分类 id
     * @param newParentCategoryId 新父分类 id；null 表示挂到种类顶层结构根之下时由调用方已解析
     * @param categoryTypeCode    分类种类编码
     */
    void moveCategoryKeepingEntityAuthority(Long categoryId, Long newParentCategoryId, String categoryTypeCode);

    /**
     * 实体改父后：若存在 1:1 分类绑定，把分类挂到「父实体对应分类」（无父则挂种类顶层）。
     */
    void syncCategoryAfterEntityMove(Long entityId, String entityTypeCode, Long newParentEntityId);

    /**
     * 由父分类解析应写入实体的 parentId（结构根 / 无 link → null）。
     */
    Long resolveParentEntityIdFromCategory(Long parentCategoryId, String categoryTypeCode);

    /**
     * 存量回填：按分类树父子把已绑定实体的 parentId / treePath 对齐（自上而下，递归路径）。
     *
     * @return 成功更新的实体条数
     */
    int backfillEntityHierarchyFromCategoryTree(String categoryTypeCode);
}
