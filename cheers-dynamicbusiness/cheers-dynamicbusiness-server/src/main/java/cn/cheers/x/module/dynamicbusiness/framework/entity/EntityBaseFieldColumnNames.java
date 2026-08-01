package cn.cheers.x.module.dynamicbusiness.framework.entity;

/**
 * 专用表基础字段固定列命名：与 {@code DynamicTableService#addColumn} 一致。
 *
 * <p>列名<strong>仅</strong>由字段编码生成：小写，{@code -} 换成 {@code _}（编码本身已是短名或 {@code *_id}/{@code *_ids}）。</p>
 *
 * <p>单选关联编码约定 {@code region_id}；多选 {@code region_ids}。是否 REF 由 {@code data_type} 表达，不靠名字前缀。</p>
 */
public final class EntityBaseFieldColumnNames {

    private EntityBaseFieldColumnNames() {
    }

    /**
     * 字段编码 → 专用表物理列名（编码 = 列名语义）。
     */
    public static String toColumnName(String fieldCode) {
        if (fieldCode == null || fieldCode.isBlank()) {
            return null;
        }
        return fieldCode.trim().toLowerCase().replace('-', '_');
    }

    /**
     * 从关联字段编码推断目标业务类型：{@code region_id}/{@code region_ids} → {@code region}。
     */
    public static String inferRefTargetEntityType(String fieldCode) {
        if (fieldCode == null || fieldCode.isBlank()) {
            return null;
        }
        String col = toColumnName(fieldCode);
        if (col == null) {
            return null;
        }
        if (col.endsWith("_ids") && col.length() > 4) {
            return col.substring(0, col.length() - 4);
        }
        if (col.endsWith("_id") && col.length() > 3) {
            return col.substring(0, col.length() - 3);
        }
        return null;
    }

    /**
     * 字段库未填 semantic_type 时的推导：关联字段取目标类型；其余用规范化后的字段编码本身。
     */
    public static String inferSemanticType(String fieldCode) {
        String fromRef = inferRefTargetEntityType(fieldCode);
        if (fromRef != null) {
            return fromRef;
        }
        return toColumnName(fieldCode);
    }
}
