package cn.iocoder.yudao.module.inspection.inspection_content.controller.admin.vo.collection;

import cn.iocoder.yudao.module.inspection.task.model.task.InspectionContent;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 巡检对象集合更新 Request VO。
 */
@Schema(description = "管理后台 - 巡检对象集合更新 Request VO")
@Data
public class InspectionObjectCollectionUpdateReqVO {

    @Schema(description = "集合ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "集合ID不能为空")
    private Long id;

    @Schema(description = "集合名称", example = "罐体检修模板")
    @Size(max = 128, message = "集合名称长度不能超过128位")
    private String collectionName;

    @Schema(description = "描述", example = "包含所有储罐的巡检配置模板")
    @Size(max = 500, message = "描述长度不能超过500位")
    private String description;

    @Schema(description = "状态", example = "enabled")
    private String status;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "集合内容（完整的对象和巡检项配置）")
    private InspectionContent content;
}
