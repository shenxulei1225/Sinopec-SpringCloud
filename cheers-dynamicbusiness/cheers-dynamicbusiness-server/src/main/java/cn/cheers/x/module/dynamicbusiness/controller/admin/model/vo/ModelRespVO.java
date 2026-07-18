package cn.cheers.x.module.dynamicbusiness.controller.admin.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 业务模型响应 VO
 * 
 * @author yudao
 */
@Schema(description = "管理后台 - 业务模型响应")
@Data
public class ModelRespVO {

    @Schema(description = "模型ID", example = "1")
    private Long id;

    @Schema(description = "模型编码", example = "MODEL_20250101001")
    private String code;

    @Schema(description = "模型名称", example = "9kg 泡沫灭火器 A 型号")
    private String name;

    @Schema(description = "业务类型编码", example = "equipment")
    private String entityTypeCode;

    @Schema(description = "业务域 Scope", example = "巡检")
    private String dataScope;

    @Schema(description = "模型描述", example = "适用于消防设备的9kg泡沫灭火器A型号")
    private String description;

    @Schema(description = "模型状态（1-启用，0-禁用）", example = "1")
    private Integer status;

    @Schema(description = "模型在业务类型下的显示顺序（值越小越靠前）", example = "1")
    private Integer sort;

    @Schema(description = "是否启用实体层级（实体树）", example = "false")
    private Boolean isTreeEntity;

    @Schema(description = "分类ID列表（模型绑定的所有分类，支持多对多）", example = "[1, 2, 3]")
    private List<Long> categoryIds;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    // ========== 前端树形展示辅助字段（非持久化，仅用于返回） ==========

    @Schema(description = "子模型列表（用于前端构建模型树）")
    private List<ModelRespVO> children;
}

