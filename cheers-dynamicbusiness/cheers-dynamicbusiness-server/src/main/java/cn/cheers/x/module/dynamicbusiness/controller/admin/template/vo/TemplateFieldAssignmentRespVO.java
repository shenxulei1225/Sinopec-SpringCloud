package cn.cheers.x.module.dynamicbusiness.controller.admin.template.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 模板字段分配响应 VO
 * 
 * @author yudao
 */
@Schema(description = "管理后台 - 模板字段分配响应")
@Data
public class TemplateFieldAssignmentRespVO {

    @Schema(description = "分配ID", example = "1")
    private Long id;

    @Schema(description = "模板ID", example = "1")
    private Long templateId;

    @Schema(description = "字段ID", example = "1")
    private Long fieldId;

    @Schema(description = "字段编码", example = "name")
    private String fieldCode;

    @Schema(description = "字段名称", example = "设备名称")
    private String fieldName;

    @Schema(description = "字段数据类型", example = "TEXT")
    private String dataType;

    @Schema(description = "排序值", example = "0")
    private Integer sortOrder;

    @Schema(description = "是否必填", example = "false")
    private Boolean required;

    @Schema(description = "默认值", example = "")
    private String defaultValue;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
