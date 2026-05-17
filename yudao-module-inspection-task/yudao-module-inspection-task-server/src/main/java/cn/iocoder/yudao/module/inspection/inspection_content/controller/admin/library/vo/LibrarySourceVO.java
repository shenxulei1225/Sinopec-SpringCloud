package cn.iocoder.yudao.module.inspection.inspection_content.controller.admin.library.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 巡检对象来源 VO。
 */
@Schema(description = "管理后台 - 巡检对象来源 VO")
@Data
public class LibrarySourceVO {

    @Schema(description = "来源编码")
    private String sourceCode;

    @Schema(description = "来源名称")
    private String sourceName;

    @Schema(description = "来源类型")
    private String sourceType;

    @Schema(description = "状态")
    private String status;

    @Schema(description = "备注")
    private String remark;
}
