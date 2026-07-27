package cn.cheers.x.module.dynamicbusiness.service.entity.categoryviaref;

/**
 * 一条经 REF 的分类反查白名单路径：维度分类种类 → 目标实体 → 主体实体上的 REF 字段。
 * <p>
 * 登记表 {@link CategoryViaRefQueryPathRegistry} 在启动时加载全部路径；运行时禁止任意连边。
 *
 * @param pathCode                   路径编码，对外传入 {@code categoryViaRefPathCode}
 * @param dimensionCategoryTypeCode  维度侧分类种类编码（库列 {@code category_type_code}，如 equipment、region）
 * @param targetResolveMode          目标实体 id 如何从分类节点解析（relation 或 link）
 * @param targetEntityTypeCode       REF 指向的目标实体类型编码（{@code entity_type_code}）
 * @param refFieldCode               主体实体上 REF 字段编码（{@code dynamic_field.code}）
 * @param subjectEntityTypeCode      被反查列出的主体实体类型编码（{@code entity_type_code}）
 */
public record CategoryViaRefQueryPath(
        String pathCode,
        String dimensionCategoryTypeCode,
        TargetEntityResolveMode targetResolveMode,
        String targetEntityTypeCode,
        String refFieldCode,
        String subjectEntityTypeCode
) {
}
