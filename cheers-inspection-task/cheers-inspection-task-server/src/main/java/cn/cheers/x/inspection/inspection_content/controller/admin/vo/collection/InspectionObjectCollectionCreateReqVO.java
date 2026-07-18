package cn.cheers.x.inspection.inspection_content.controller.admin.vo.collection;

import cn.cheers.x.inspection.task.model.task.InspectionContent;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 巡检对象集合创建 Request VO。
 */
@Schema(description = "管理后台 - 巡检对象集合创建 Request VO")
@Data
public class InspectionObjectCollectionCreateReqVO {

    @Schema(description = "集合编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "COL-001")
    @NotBlank(message = "集合编码不能为空")
    @Size(max = 64, message = "集合编码长度不能超过64位")
    private String collectionCode;

    @Schema(description = "集合名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "罐体检修模板")
    @NotBlank(message = "集合名称不能为空")
    @Size(max = 128, message = "集合名称长度不能超过128位")
    private String collectionName;

    @Schema(description = "描述", example = "包含所有储罐的巡检配置模板")
    @Size(max = 500, message = "描述长度不能超过500位")
    private String description;

    @Schema(description = "状态", example = "enabled")
    private String status;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "集合内容（完整的对象和巡检项配置）", hidden = true)
    private InspectionContent content;
}
