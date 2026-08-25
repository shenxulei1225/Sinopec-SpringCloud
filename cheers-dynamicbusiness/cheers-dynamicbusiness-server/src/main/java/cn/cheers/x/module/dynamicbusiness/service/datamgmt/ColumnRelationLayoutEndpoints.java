package cn.cheers.x.module.dynamicbusiness.service.datamgmt;

import cn.cheers.x.module.dynamicbusiness.dal.dataobject.datamgmt.DmDataTabLayoutDO;
import cn.cheers.x.module.dynamicbusiness.enums.datamgmt.DmDataTabLayoutKindEnum;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 从页面布局行解析栏端点（列身份 + 底座类型编码 + 区段）。
 * 供栏间关系写出与孤儿清理使用；读列表接口不得调用写库逻辑。
 */
final class ColumnRelationLayoutEndpoints {

    static final String SECTION_OBJECT = "OBJECT";

    private ColumnRelationLayoutEndpoints() {
    }

    record ColumnEndpoint(
            DmDataTabLayoutKindEnum kind,
            String identity,
            String typeCode,
            String columnSection) {
    }

    record LayoutEndpoints(
            List<ColumnEndpoint> categories,
            List<ColumnEndpoint> models,
            List<ColumnEndpoint> entities,
            Set<String> validIdentities) {
    }

    static LayoutEndpoints fromLayoutRows(List<DmDataTabLayoutDO> layouts) {
        List<ColumnEndpoint> categories = new ArrayList<>();
        List<ColumnEndpoint> models = new ArrayList<>();
        List<ColumnEndpoint> entities = new ArrayList<>();
        Set<String> validIdentities = new HashSet<>();

        for (DmDataTabLayoutDO row : layouts) {
            if (Boolean.FALSE.equals(row.getEnabled())) {
                continue;
            }
            ColumnEndpoint endpoint = toEndpoint(row);
            if (endpoint == null) {
                continue;
            }
            validIdentities.add(endpoint.identity());
            switch (endpoint.kind()) {
                case CATEGORY -> categories.add(endpoint);
                case MODEL -> models.add(endpoint);
                case ENTITY -> entities.add(endpoint);
                default -> {
                }
            }
        }
        return new LayoutEndpoints(categories, models, entities, validIdentities);
    }

    static boolean isWhoEntity(ColumnEndpoint entity) {
        return entity.kind() == DmDataTabLayoutKindEnum.ENTITY
                && SECTION_OBJECT.equalsIgnoreCase(
                        entity.columnSection() == null ? "" : entity.columnSection().trim());
    }

    static boolean sameTypeCode(String a, String b) {
        if (!StringUtils.hasText(a) || !StringUtils.hasText(b)) {
            return false;
        }
        return a.trim().equalsIgnoreCase(b.trim());
    }

    static String normalizeType(String typeCode) {
        return typeCode == null ? "" : typeCode.trim().toLowerCase();
    }

    static String pairKey(String from, String to, String edgeRole) {
        return (from == null ? "" : from.trim())
                + "=>"
                + (to == null ? "" : to.trim())
                + "=>"
                + (edgeRole == null ? "filter" : edgeRole.trim());
    }

    private static ColumnEndpoint toEndpoint(DmDataTabLayoutDO row) {
        String kindCode = row.getColumnKind() == null ? "" : row.getColumnKind().trim().toUpperCase();
        DmDataTabLayoutKindEnum kind = DmDataTabLayoutKindEnum.getByCode(kindCode);
        if (kind == null) {
            return null;
        }
        Map<String, Object> meta = row.getColumnMeta() != null ? row.getColumnMeta() : Map.of();
        String tab = StringUtils.hasText(row.getTabId()) ? row.getTabId().trim() : "default";
        String section = metaString(meta, "columnSection");
        return switch (kind) {
            case CATEGORY -> {
                String typeCode = metaString(meta, "categoryTypeCode");
                if (!StringUtils.hasText(typeCode)) {
                    yield null;
                }
                String columnKey = metaString(meta, "columnKey");
                if (!StringUtils.hasText(columnKey)) {
                    columnKey = "default";
                }
                yield new ColumnEndpoint(
                        DmDataTabLayoutKindEnum.CATEGORY,
                        "CATEGORY:" + columnKey + ":" + tab,
                        typeCode.trim(),
                        section);
            }
            case MODEL -> {
                String typeCode = metaString(meta, "modelEntityTypeCode");
                if (!StringUtils.hasText(typeCode)) {
                    yield null;
                }
                yield new ColumnEndpoint(
                        DmDataTabLayoutKindEnum.MODEL,
                        "MODEL:" + tab,
                        typeCode.trim(),
                        section);
            }
            case ENTITY -> {
                String typeCode = metaString(meta, "entityEntityTypeCode");
                if (!StringUtils.hasText(typeCode)) {
                    yield null;
                }
                yield new ColumnEndpoint(
                        DmDataTabLayoutKindEnum.ENTITY,
                        "ENTITY:" + tab,
                        typeCode.trim(),
                        section);
            }
            default -> null;
        };
    }

    private static String metaString(Map<String, Object> meta, String key) {
        Object value = meta.get(key);
        if (value == null) {
            return null;
        }
        String text = String.valueOf(value).trim();
        return text.isEmpty() || "null".equals(text) ? null : text;
    }
}
