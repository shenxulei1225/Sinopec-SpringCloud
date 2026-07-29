package cn.cheers.x.module.dynamicbusiness.framework.entity;

/**
 * 专用表基础字段固定列命名：与 {@code DynamicTableService#addColumn} 一致。
 *
 * <p>列名<strong>仅</strong>由字段库字段编码生成：小写，{@code -} 换成 {@code _}。
 * 例：{@code FLD-BASE-facility-REF_REGION} → {@code fld_base_facility_ref_region}。</p>
 *
 * <p>禁止任何短名或历史别名（如 {@code zone_id}）。</p>
 */
public final class EntityBaseFieldColumnNames {

    private EntityBaseFieldColumnNames() {
    }

    /**
     * 字段编码 → 专用表物理列名。
     */
    public static String toColumnName(String fieldCode) {
        if (fieldCode == null || fieldCode.isBlank()) {
            return null;
        }
        return fieldCode.trim().toLowerCase().replace('-', '_');
    }

    /**
     * 从基础字段编码推断 REF 目标业务类型，如 {@code ...-REF_REGION} → {@code region}。
     */
    public static String inferRefTargetEntityType(String fieldCode) {
        if (fieldCode == null) {
            return null;
        }
        String code = fieldCode.trim();
        int idx = code.lastIndexOf("-REF_");
        if (idx < 0) {
            idx = code.lastIndexOf("_REF_");
        }
        if (idx < 0) {
            return null;
        }
        String suffix = code.substring(idx + 5).trim();
        if (suffix.isEmpty()) {
            return null;
        }
        return suffix.toLowerCase().replace('-', '_');
    }

    /**
     * 字段库未填 semantic_type 时，从字段编码推导投影用语义角色。
     * <ul>
     *   <li>{@code ...-REF_FACILITY} → {@code facility}</li>
     *   <li>{@code FLD-BASE-zone-boundary_geojson} → {@code boundary_geojson}</li>
     * </ul>
     */
    public static String inferSemanticType(String fieldCode) {
        if (fieldCode == null || fieldCode.isBlank()) {
            return null;
        }
        String fromRef = inferRefTargetEntityType(fieldCode);
        if (fromRef != null) {
            return fromRef;
        }
        String code = fieldCode.trim();
        if (code.regionMatches(true, 0, "FLD-BASE-", 0, 9)) {
            String rest = code.substring(9);
            int dash = rest.indexOf('-');
            if (dash > 0 && dash < rest.length() - 1) {
                return rest.substring(dash + 1).toLowerCase().replace('-', '_');
            }
        }
        return null;
    }
}
