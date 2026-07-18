package cn.cheers.x.module.dynamicbusiness.service.relation;

/**
 * 关联字段库与运行时 {@code FieldDO} / {@code EntityDO.customFields} / {@code dynamic_entity_relation.field_code} 的编码约定。
 *
 * <p><b>链路（权威）</b>：</p>
 * <ol>
 *   <li>关联字段库 {@code RelationFieldLibraryDO.fieldCode}：全局业务语义编码（如 {@code safety_manager}）。</li>
 *   <li>模型选用后在 {@code FieldDO.code} 上增加前缀 {@link #LIBRARY_REF_PREFIX}，与 JSON 键、关系表字段编码一致。</li>
 *   <li>实体侧值写在 {@code EntityDO.customFields} 的同名键下；{@code EntityRelationSyncServiceImpl} 按该键同步
 *       {@code EntityRelationDO.fieldCode}。</li>
 * </ol>
 */
public final class RelationFieldCodes {

    /**
     * 由关联字段库落到模型字段定义时的编码前缀，避免与普通动态字段混淆。
     */
    public static final String LIBRARY_REF_PREFIX = "REF-";

    private RelationFieldCodes() {
    }

    /**
     * 库字段编码 → 模型/实体侧字段编码（{@code REF-}{@code libraryFieldCode}）。
     */
    public static String toModelFieldCode(String libraryFieldCode) {
        if (libraryFieldCode == null || libraryFieldCode.isBlank()) {
            return LIBRARY_REF_PREFIX;
        }
        return LIBRARY_REF_PREFIX + libraryFieldCode.trim();
    }

    /**
     * 是否为库驱动的关联字段编码。
     */
    public static boolean isLibraryRefFieldCode(String fieldCode) {
        return fieldCode != null && fieldCode.startsWith(LIBRARY_REF_PREFIX);
    }

    /**
     * 去掉 {@link #LIBRARY_REF_PREFIX}，得到库内 {@code fieldCode}；若无前缀则原样返回。
     */
    public static String stripPrefixIfPresent(String fieldCode) {
        if (fieldCode != null && fieldCode.startsWith(LIBRARY_REF_PREFIX)) {
            return fieldCode.substring(LIBRARY_REF_PREFIX.length());
        }
        return fieldCode;
    }
}
