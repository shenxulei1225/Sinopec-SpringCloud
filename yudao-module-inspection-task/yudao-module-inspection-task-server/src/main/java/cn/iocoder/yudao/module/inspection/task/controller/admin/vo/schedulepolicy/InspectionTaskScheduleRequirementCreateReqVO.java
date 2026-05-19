package cn.iocoder.yudao.module.inspection.task.controller.admin.vo.schedulepolicy;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 管理后台 - 排期需求创建 Request VO
 *
 * <p>排期需求是一个配置容器，包含多个模板组合。</p>
 */
@Schema(description = "管理后台 - 排期需求创建 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class InspectionTaskScheduleRequirementCreateReqVO extends InspectionTaskScheduleRequirementBaseVO {

    @Schema(description = "关联的任务ID")
    @NotNull(message = "任务ID不能为空")
    private Long taskId;

    @Schema(description = "模板组合配置列表")
    private List<ScheduleTemplateConfigVO> scheduleTemplates;
}
