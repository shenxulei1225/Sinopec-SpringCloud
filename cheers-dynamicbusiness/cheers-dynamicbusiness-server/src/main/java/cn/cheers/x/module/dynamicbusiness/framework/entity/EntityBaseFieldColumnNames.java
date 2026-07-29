package cn.cheers.x.module.dynamicbusiness.framework.entity;

/**
 * 专用表基础字段固定列命名：与 {@code DynamicTableService#addColumn} 一致。
 *
 * <p>列名 = 字段编码小写，{@code -} 换成 {@code _}。
 * 例：{@code FLD-BASE-facility-REF_REGION} → {@code fld_base_facility_ref_region}。</p>
 */
public final class EntityBaseFieldColumnNames {

    private EntityBaseFieldColumnNames() {
    }

    public static String toColumnName(String fieldCode) {
        if (fieldCode == null || fieldCode.isBlank()) {
            return null;
        }
        return fieldCode.trim().toLowerCase().replace('-', '_');
    }

    /**
     * 解析专用表物理列名：优先字段编码转列名；若不存在则尝试历史短名 {@code {target}_id}
     *（如 {@code FLD-BASE-equipment-REF_ZONE} → {@code zone_id}）。
     *
     * @param columnExists 表内是否存在该列
     */
    public static String resolvePhysicalColumnName(String fieldCode, java.util.function.Predicate<String> columnExists) {
        String primary = toColumnName(fieldCode);
        if (primary != null && columnExists != null && columnExists.test(primary)) {
            return primary;
        }
        String target = inferRefTargetEntityType(fieldCode);
        if (target != null && !target.isBlank()) {
            String alias = target.trim().toLowerCase().replace('-', '_') + "_id";
            if (columnExists != null && columnExists.test(alias)) {
                return alias;
            }
        }
        return primary;
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
}
