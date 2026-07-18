package cn.cheers.x.inspection.inspection_content.controller.admin.library.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 巡检对象 VO。
 *
 * <p>用于前端展示的巡检对象信息。</p>
 */
@Schema(description = "管理后台 - 巡检对象 VO")
@Data
public class LibraryObjectVO {

    @Schema(description = "对象编码（业务系统中的编码）")
    private String objectCode;

    @Schema(description = "对象名称")
    private String objectName;

    @Schema(description = "分类ID")
    private Long categoryId;

    @Schema(description = "对象模型/型号")
    private String objectModel;

    @Schema(description = "扩展信息（JSON格式）")
    private String extra;
}
