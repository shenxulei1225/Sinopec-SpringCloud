package cn.cheers.x.inspection.task.controller.admin.vo.template;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(description = "管理后台 - 巡检任务模板创建 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class InspectionTaskTemplateCreateReqVO extends InspectionTaskTemplateBaseVO {
}
