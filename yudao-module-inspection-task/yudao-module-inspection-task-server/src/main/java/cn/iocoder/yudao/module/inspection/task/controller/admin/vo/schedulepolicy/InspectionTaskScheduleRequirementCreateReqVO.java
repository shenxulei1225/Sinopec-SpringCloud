package cn.iocoder.yudao.module.inspection.task.controller.admin.vo.schedulepolicy;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 管理后台 - 排期需求创建 Request VO
 */
@Schema(description = "管理后台 - 排期需求创建 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class InspectionTaskScheduleRequirementCreateReqVO extends InspectionTaskScheduleRequirementBaseVO {
}
