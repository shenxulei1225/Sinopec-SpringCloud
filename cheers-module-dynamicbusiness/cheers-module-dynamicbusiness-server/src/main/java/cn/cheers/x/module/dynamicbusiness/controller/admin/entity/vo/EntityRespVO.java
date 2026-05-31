package cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo;

import cn.cheers.x.module.dynamicbusiness.controller.admin.field.vo.FieldRespVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Schema(description = "管理后台 - 实体响应 VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class EntityRespVO extends EntityBaseVO {

    @Schema(description = "实体ID", example = "1001")
    private Long id;

    @Schema(description = "父实体ID", example = "1000")
    private Long parentId;

    @Schema(description = "同一父节点下的排序序号（从1开始）", example = "1")
    private Integer sort;

    @Schema(description = "子实体列表")
    private List<EntityRespVO> children;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    // ==================== 字段定义信息 ====================

    @Schema(description = "模型的字段定义列表（包含字段名称、类型等信息）")
    private List<FieldRespVO> fields;

    // ==================== 增强字段信息 ====================

    @Schema(description = "字段ID到字段名称的映射，用于前端友好显示")
    private Map<String, String> fieldNameMapping;

    @Schema(description = "各 REF/REFMulti 字段的关联数据块；仅 includeAssociations=true 时填充，见 entity-detail-associations-design.md")
    private List<AssociationsVO> associations;
}

