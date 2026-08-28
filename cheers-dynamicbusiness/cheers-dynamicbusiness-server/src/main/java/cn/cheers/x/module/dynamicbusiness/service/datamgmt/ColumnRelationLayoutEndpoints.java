package cn.cheers.x.module.dynamicbusiness.service.datamgmt;

import cn.cheers.x.module.dynamicbusiness.dal.dataobject.datamgmt.DmDataTabLayoutDO;
import cn.cheers.x.module.dynamicbusiness.enums.datamgmt.DmDataTabLayoutKindEnum;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 从页面布局行解析栏端点（列身份 + 底座类型编码 + 区段）。
 * <p>
 * ## 本文件负责什么
 * <ul>
 *   <li>默认 filter 自动连线：{@link #fromLayoutRows}（只要启用且类型码齐全的端点）</li>
 *   <li>删栏时算出作废列身份：{@link #columnIdentityOf}（与前端 columnIdentity 对齐）</li>
 * </ul>
 * <p>
 * ## 本文件不负责什么
 * <ul>
 *   <li>不删边、不写边（删边见 {@link DmDataTabColumnRelationService}）</li>
 *   <li>不根据 enabled / 缺类型码「猜测」哪些边该留</li>
 * </ul>
 * 读列表接口不得调用写库逻辑。
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

    /** 自动连线用端点分组；不含「有效身份全集」（已废止按身份猜删）。 */
    record LayoutEndpoints(
            List<ColumnEndpoint> categories,
            List<ColumnEndpoint> models,
            List<ColumnEndpoint> entities) {
    }

    /**
     * 默认自动连线用端点列表。
     * <p>
     * 跳过配置隐藏栏、缺类型码的空壳——这些栏<strong>不自动补 filter</strong>，
     * 但栏行仍在时其上的用户边不得被误删（删边只认「布局行被删」）。
     */
    static LayoutEndpoints fromLayoutRows(List<DmDataTabLayoutDO> layouts) {
        List<ColumnEndpoint> categories = new ArrayList<>();
        List<ColumnEndpoint> models = new ArrayList<>();
        List<ColumnEndpoint> entities = new ArrayList<>();

        for (DmDataTabLayoutDO row : layouts) {
            if (Boolean.FALSE.equals(row.getEnabled())) {
                continue;
            }
            ColumnEndpoint endpoint = toEndpointForAutoWire(row);
            if (endpoint == null) {
                continue;
            }
            switch (endpoint.kind()) {
                case CATEGORY -> categories.add(endpoint);
                case MODEL -> models.add(endpoint);
                case ENTITY -> entities.add(endpoint);
                default -> {
                }
            }
        }
        return new LayoutEndpoints(categories, models, entities);
    }

    /**
     * 布局行 → 列身份（与前端 {@code columnIdentity} 对齐）。
     * <p>
     * 权威用途：本次保存布局时<strong>即将删除的行</strong>，算出作废身份交给
     * {@link DmDataTabColumnRelationService#removeRelationsTouchingIdentities}。
     * <p>
     * 不看 enabled、不要求类型码：配置隐藏的栏仍有身份；身份只随 tabId / columnKey 变。
     * DETAIL 无查数边身份，返回 null。
     */
    static String columnIdentityOf(DmDataTabLayoutDO row) {
        if (row == null) {
            return null;
        }
        String kindCode = row.getColumnKind() == null ? "" : row.getColumnKind().trim().toUpperCase();
        DmDataTabLayoutKindEnum kind = DmDataTabLayoutKindEnum.getByCode(kindCode);
        if (kind == null) {
            return null;
        }
        Map<String, Object> meta = row.getColumnMeta() != null ? row.getColumnMeta() : Map.of();
        String tab = StringUtils.hasText(row.getTabId()) ? row.getTabId().trim() : "default";
        return switch (kind) {
            case CATEGORY -> {
                String columnKey = metaString(meta, "columnKey");
                if (!StringUtils.hasText(columnKey)) {
                    columnKey = "default";
                }
                yield "CATEGORY:" + columnKey + ":" + tab;
            }
            case MODEL -> "MODEL:" + tab;
            case ENTITY -> "ENTITY:" + tab;
            default -> null;
        };
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

    /** 自动连线端点：必须有类型码，否则无法写 from/toTypeCode。 */
    private static ColumnEndpoint toEndpointForAutoWire(DmDataTabLayoutDO row) {
        String kindCode = row.getColumnKind() == null ? "" : row.getColumnKind().trim().toUpperCase();
        DmDataTabLayoutKindEnum kind = DmDataTabLayoutKindEnum.getByCode(kindCode);
        if (kind == null) {
            return null;
        }
        Map<String, Object> meta = row.getColumnMeta() != null ? row.getColumnMeta() : Map.of();
        String identity = columnIdentityOf(row);
        if (identity == null || identity.isEmpty()) {
            return null;
        }
        String section = metaString(meta, "columnSection");
        return switch (kind) {
            case CATEGORY -> {
                String typeCode = metaString(meta, "categoryTypeCode");
                if (!StringUtils.hasText(typeCode)) {
                    yield null;
                }
                yield new ColumnEndpoint(
                        DmDataTabLayoutKindEnum.CATEGORY, identity, typeCode.trim(), section);
            }
            case MODEL -> {
                String typeCode = metaString(meta, "modelEntityTypeCode");
                if (!StringUtils.hasText(typeCode)) {
                    yield null;
                }
                yield new ColumnEndpoint(
                        DmDataTabLayoutKindEnum.MODEL, identity, typeCode.trim(), section);
            }
            case ENTITY -> {
                String typeCode = metaString(meta, "entityEntityTypeCode");
                if (!StringUtils.hasText(typeCode)) {
                    yield null;
                }
                yield new ColumnEndpoint(
                        DmDataTabLayoutKindEnum.ENTITY, identity, typeCode.trim(), section);
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
