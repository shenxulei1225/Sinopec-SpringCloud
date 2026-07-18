package cn.cheers.x.inspection.inspection_content.controller.admin.vo.item;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 巡检项 Response VO。
 */
@Schema(description = "管理后台 - 巡检项 Response VO")
@Data
public class InspectionItemRespVO {

    @Schema(description = "巡检项ID", example = "1")
    private Long id;

    @Schema(description = "巡检项编码")
    private String itemCode;

    @Schema(description = "巡检项名称")
    private String itemName;

    @Schema(description = "业务实体类型")
    private String sourceType;

    @Schema(description = "分类ID")
    private Long categoryId;

    @Schema(description = "设备型号/模型")
    private String objectModel;

    @Schema(description = "检查方法说明")
    private String method;

    @Schema(description = "检查标准/判定依据")
    private String standard;

    @Schema(description = "状态")
    private String status;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}
