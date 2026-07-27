package cn.cheers.x.module.dynamicbusiness.service.entity.categoryviaref;

/**
 * 经 REF 分类反查时，如何从「维度分类树节点」解析出「目标实体 id 集合」。
 * <p>
 * 命名使用 {@code CATEGORY_ENTITY_*} 前缀，避免与 {@code EntityRelation}、
 * {@code EntityCategoryRelation} 等类型名裸词混淆。
 */
public enum TargetEntityResolveMode {

    /**
     * 简单分类（SIMPLE）：目标实体通过 N:N 的「分类–实体关联」挂在分类节点下。
     * 查询期只读 {@code dynamic_entity_category_relation}（及分类子树展开规则），不写库。
     */
    CATEGORY_ENTITY_RELATION,

    /**
     * 高级分类（ADVANCED）：分类节点 1:1 绑定目标实体。
     * 查询期只读 {@code dynamic_category_entity_link}（及分类子树展开规则），不写库。
     */
    CATEGORY_ENTITY_LINK
}
