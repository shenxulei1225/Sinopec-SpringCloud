package cn.iocoder.yudao.module.emergency.controller.admin.command.vo;

import cn.cheers.x.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Schema(description = "管理后台 - 应急指令模板分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class EmergencyCommandTemplatePageReqVO extends PageParam {

    @Schema(description = "模板名称", example = "消防处置指令")
    private String name;

    @Schema(description = "模板分类", example = "disposal")
    private String category;

    @Schema(description = "适用阶段", example = "disposal")
    private String stage;

    @Schema(description = "是否启用", example = "true")
    private Boolean isEnabled;

    @Schema(description = "是否系统模板", example = "false")
    private Boolean isSystem;
}
