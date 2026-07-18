package cn.cheers.x.inspection.task.controller.admin.vo.template;

import cn.cheers.x.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(description = "管理后台 - 巡检任务模板分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class InspectionTaskTemplatePageReqVO extends PageParam {

    @Schema(description = "父模板ID")
    private Long parentId;

    @Schema(description = "模板分类ID")
    private Long categoryId;

    @Schema(description = "模板名称")
    private String templateName;

    @Schema(description = "模板编码")
    private String templateCode;

    @Schema(description = "是否启用")
    private Boolean enabled;
}
