package cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo;

import cn.cheers.x.module.dynamicbusiness.controller.admin.field.vo.FieldRespVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Schema(description = "管理后台 - 实体响应 VO")
@Data
public class EntityRespVO {

    @Schema(description = "实体ID", example = "1001")
    private Long id;

    @Schema(description = "同一父节点下的排序序号（从1开始）", example = "1")
    private Integer sort;

    @Schema(description = "固定列（BaseField），key 为 fieldCode")
    private Map<String, Object> baseFields;

    @Schema(description = "扩展列（CustomField），key 为 fieldCode")
    private Map<String, Object> customFields;

    @Schema(description = "子实体列表")
    private List<EntityRespVO> children;

    @Schema(description = "创建人用户编号。报修列表用它只显示自己上报的单。保存的是用户目录编号，不是姓名。")
    private String creator;

    @Schema(description = "创建人在用户目录里的昵称。只在这次返回里带上，不写入报修单。", accessMode = Schema.AccessMode.READ_ONLY)
    private String creatorName;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @Schema(description = "模型名称（查询增强，只读）")
    private String modelName;

    @Schema(description = "模型的字段定义列表（包含字段名称、类型等信息）")
    private List<FieldRespVO> fields;

    @Schema(description = "字段ID到字段名称的映射，用于前端友好显示")
    private Map<String, String> fieldNameMapping;

    @Schema(description = "各 REF/REFMulti 字段的关联数据块；仅 includeAssociations=true 时填充")
    private List<AssociationsVO> associations;

    @Schema(description = "点位对应字段的说明。只在这次详情返回，不写回点位。", accessMode = Schema.AccessMode.READ_ONLY)
    private String collectionPointNote;

    // ---------- 兼容读路径（由 baseFields 投影，勿用于 Write） ----------

    @Schema(description = "业务类型编码（= baseFields.entityTypeCode）", accessMode = Schema.AccessMode.READ_ONLY)
    public String getEntityTypeCode() {
        return readBaseString("entityTypeCode");
    }

    @Schema(description = "模型ID（= baseFields.modelId）", accessMode = Schema.AccessMode.READ_ONLY)
    public Long getModelId() {
        return readBaseLong("modelId");
    }

    @Schema(description = "名称（= baseFields.name）", accessMode = Schema.AccessMode.READ_ONLY)
    public String getName() {
        return readBaseString("name");
    }

    @Schema(description = "状态（= baseFields.status）", accessMode = Schema.AccessMode.READ_ONLY)
    public Integer getStatus() {
        Object value = baseFields != null ? baseFields.get("status") : null;
        if (value instanceof Number number) {
            return number.intValue();
        }
        if (value == null) {
            return null;
        }
        try {
            return Integer.parseInt(String.valueOf(value));
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    @Schema(description = "父实体编号。基础字段里的上级若已带名称，这里仍只返回编号", accessMode = Schema.AccessMode.READ_ONLY)
    public Long getParentId() {
        return readBaseLong("parentId");
    }

    private String readBaseString(String key) {
        if (baseFields == null) {
            return null;
        }
        Object value = baseFields.get(key);
        return value == null ? null : String.valueOf(value);
    }

    private Long readBaseLong(String key) {
        if (baseFields == null) {
            return null;
        }
        Object value = baseFields.get(key);
        if (value instanceof Map<?, ?> map) {
            Object id = map.get("id");
            if (id == null) {
                id = map.get("entityId");
            }
            value = id;
        }
        if (value instanceof Number number) {
            return number.longValue();
        }
        if (value == null) {
            return null;
        }
        try {
            return Long.parseLong(String.valueOf(value));
        } catch (NumberFormatException ex) {
            return null;
        }
    }
}
