package cn.iocoder.yudao.module.emergency.controller.admin.plan.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

/**
 * 应急预案步骤更新 VO
 * <p>
 * 用于更新应急预案步骤，支持树形结构的更新。
 * 继承基础VO类，额外包含子步骤列表，用于构建完整的步骤树。
 */
@Schema(description = "管理后台 - 应急预案步骤更新 VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class EmergencyPlanStepUpdateVO extends EmergencyPlanStepBaseVO {

    @Schema(description = "子步骤列表 - 该步骤的下级步骤，支持多级嵌套，构成完整的步骤树形结构")
    private List<EmergencyPlanStepUpdateVO> children;

}
