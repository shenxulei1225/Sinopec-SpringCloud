package cn.cheers.x.inspection.task.controller.admin.vo.template;

import cn.cheers.x.inspection.task.model.task.InspectionContent;
import cn.cheers.x.inspection.task.model.task.ResourcePolicy;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 巡检任务模板 Response VO")
@Data
public class InspectionTaskTemplateRespVO {

    @Schema(description = "模板 ID", example = "1")
    private Long id;
    @Schema(description = "父模板 ID")
    private Long parentId;
    @Schema(description = "模板分类 ID")
    private Long categoryId;
    @Schema(description = "模板分类名称")
    private String categoryName;
    @Schema(description = "模板编码")
    private String templateCode;
    @Schema(description = "模板名称")
    private String templateName;
    @Schema(description = "是否启用")
    private Boolean enabled;
    @Schema(description = "模板说明")
    private String description;
    @Schema(description = "默认排期策略 ID")
    private Long defaultSchedulePolicyId;
    @Schema(description = "默认排期策略名称")
    private String defaultSchedulePolicyName;
    @Schema(description = "默认资源策略")
    private ResourcePolicy resourcePolicy;
    @Schema(description = "模板巡检内容")
    private InspectionContent inspectionContent;
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}
