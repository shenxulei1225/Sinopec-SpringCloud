package cn.cheers.x.inspection.task.controller.admin.vo.schedulepolicy;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(description = "管理后台 - 排期配置创建 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class InspectionTaskSchedulePolicyCreateReqVO extends InspectionTaskSchedulePolicyBaseVO {
}
