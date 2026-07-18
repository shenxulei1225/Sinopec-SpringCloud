package cn.cheers.x.module.dynamicbusiness.controller.admin.template.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 字段模板响应 VO
 * 
 * @author yudao
 */
@Schema(description = "管理后台 - 字段模板响应")
@Data
public class TemplateRespVO {

    @Schema(description = "模板ID", example = "1")
    private Long id;

    @Schema(description = "模板编码", example = "TPL_20260106001")
    private String code;

    @Schema(description = "模板名称", example = "设备基础模板")
    private String name;

    @Schema(description = "业务类型编码", example = "equipment")
    private String entityTypeCode;

    @Schema(description = "模板描述", example = "包含设备管理的基础字段")
    private String description;

    @Schema(description = "模板状态（1-启用，0-禁用）", example = "1")
    private Integer status;

    @Schema(description = "是否为系统预设模板", example = "false")
    private Boolean isSystem;

    @Schema(description = "字段数量", example = "5")
    private Integer fieldCount;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}
