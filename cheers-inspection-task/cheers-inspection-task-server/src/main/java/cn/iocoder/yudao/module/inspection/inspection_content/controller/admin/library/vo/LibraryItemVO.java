package cn.iocoder.yudao.module.inspection.inspection_content.controller.admin.library.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 巡检项 VO。
 *
 * <p>用于前端展示的巡检项信息。</p>
 */
@Schema(description = "管理后台 - 巡检项 VO")
@Data
public class LibraryItemVO {

    @Schema(description = "巡检项ID")
    private Long itemId;

    @Schema(description = "巡检项编码")
    private String itemCode;

    @Schema(description = "巡检项名称")
    private String itemName;

    @Schema(description = "检查方法说明")
    private String method;

    @Schema(description = "检查标准/判定依据")
    private String standard;

    @Schema(description = "状态")
    private String status;

    @Schema(description = "备注")
    private String remark;
}
