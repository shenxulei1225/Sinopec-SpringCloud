package cn.cheers.x.module.platformresource.service.component;

import cn.hutool.core.util.StrUtil;
import cn.cheers.x.module.platformresource.controller.admin.component.vo.ComponentDataSourceVO;
import cn.cheers.x.module.platformresource.dal.dataobject.component.ComponentPropsDO;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;

/**
 * 组件数据来源结构体：businessCategory + entityTypeCode + dataKind。
 * system 分类强制 dataKind=entity。
 */
public final class ComponentDataSource {

    public static final String CATEGORY_DYNAMIC = "dynamic";
    public static final String CATEGORY_SYSTEM = "system";
    /** 分类体系（与前端 businessCategory=category 对齐，dataKind 恒为 entity） */
    public static final String CATEGORY_CATEGORY = "category";
    public static final String KIND_MODEL = "model";
    public static final String KIND_ENTITY = "entity";

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private ComponentDataSource() {
    }

    public static Normalized normalize(String businessCategory, String entityTypeCode, String dataKind) {
        String category = StrUtil.trim(businessCategory);
        String typeCode = StrUtil.trim(entityTypeCode);
        String kind = StrUtil.trim(dataKind);

        if (StrUtil.isBlank(category) && StrUtil.isBlank(typeCode) && StrUtil.isBlank(kind)) {
            return Normalized.empty();
        }
        if (CATEGORY_SYSTEM.equals(category)) {
            return new Normalized(CATEGORY_SYSTEM, typeCode, KIND_ENTITY);
        }
        if (CATEGORY_CATEGORY.equals(category)) {
            if (StrUtil.isBlank(typeCode)) {
                throw new IllegalArgumentException("分类体系须指定 entityTypeCode（分类类型编码）");
            }
            return new Normalized(CATEGORY_CATEGORY, typeCode, KIND_ENTITY);
        }
        if (CATEGORY_DYNAMIC.equals(category)) {
            if (!KIND_MODEL.equals(kind) && !KIND_ENTITY.equals(kind)) {
                throw new IllegalArgumentException("动态业务须指定 dataKind 为 model 或 entity");
            }
            return new Normalized(CATEGORY_DYNAMIC, typeCode, kind);
        }
        throw new IllegalArgumentException("businessCategory 须为 dynamic、system 或 category");
    }

    public static Normalized fromVo(ComponentDataSourceVO vo) {
        if (vo == null) {
            return Normalized.empty();
        }
        return normalize(vo.getBusinessCategory(), vo.getEntityTypeCode(), vo.getDataKind());
    }

    public static ComponentDataSourceVO toVo(Normalized normalized) {
        if (normalized == null || !normalized.isPresent()) {
            return null;
        }
        ComponentDataSourceVO vo = new ComponentDataSourceVO();
        vo.setBusinessCategory(normalized.businessCategory());
        vo.setEntityTypeCode(normalized.entityTypeCode());
        vo.setDataKind(normalized.dataKind());
        return vo;
    }

    public static void applyToRow(ComponentPropsDO row, ComponentDataSourceVO vo) {
        applyToRow(row, fromVo(vo));
    }

    public static void applyToRow(ComponentPropsDO row, Normalized normalized) {
        if (normalized == null || !normalized.isPresent()) {
            row.setDataSource(null);
            return;
        }
        row.setDataSource(toJson(normalized));
    }

    public static Normalized fromRow(ComponentPropsDO row) {
        if (row == null || StrUtil.isBlank(row.getDataSource())) {
            return Normalized.empty();
        }
        return fromJson(row.getDataSource());
    }

    /** 迁移旧 composite 字符串（dynamic-entity:code / system:code）。 */
    public static Normalized parseLegacyKey(String legacyKey) {
        String key = StrUtil.trim(legacyKey);
        if (StrUtil.isBlank(key)) {
            return Normalized.empty();
        }
        if (key.startsWith("system:")) {
            return new Normalized(CATEGORY_SYSTEM, key.substring("system:".length()).trim(), KIND_ENTITY);
        }
        if (key.startsWith("dynamic-model:")) {
            return new Normalized(CATEGORY_DYNAMIC, key.substring("dynamic-model:".length()).trim(), KIND_MODEL);
        }
        if (key.startsWith("dynamic-entity:")) {
            String rest = key.substring("dynamic-entity:".length());
            String typeCode = rest.contains(":") ? rest.substring(0, rest.indexOf(':')).trim() : rest.trim();
            return new Normalized(CATEGORY_DYNAMIC, typeCode, KIND_ENTITY);
        }
        return new Normalized(CATEGORY_DYNAMIC, key, KIND_ENTITY);
    }

    @SneakyThrows
    public static String toJson(Normalized normalized) {
        return MAPPER.writeValueAsString(toVo(normalized));
    }

    @SneakyThrows
    private static Normalized fromJson(String json) {
        ComponentDataSourceVO vo = MAPPER.readValue(json, ComponentDataSourceVO.class);
        return fromVo(vo);
    }

    public record Normalized(String businessCategory, String entityTypeCode, String dataKind) {
        public static Normalized empty() {
            return new Normalized(null, null, null);
        }

        public boolean isPresent() {
            return StrUtil.isNotBlank(businessCategory) && StrUtil.isNotBlank(entityTypeCode);
        }
    }
}
