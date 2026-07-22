package cn.iocoder.yudao.module.emergency.controller.admin.plan.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import jakarta.validation.constraints.NotNull;

/**
 * 预案步骤更新 Request VO
 * <p>
 * 用于更新已存在的预案步骤信息，支持更新步骤属性、调整顺序、移动位置等操作。
 * 继承自创建VO，包含所有可更新的字段。
 *
 * @author 芋道源码
 */
@Schema(description = "管理后台 - 预案步骤更新 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class PlanStepUpdateReqVO extends PlanStepCreateReqVO {

    @Schema(description = "步骤编号 - 要更新的步骤的唯一标识符，必填。注意：如果ID是很大的整数（超过2^53），前端应传递字符串类型以避免JavaScript精度丢失", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @NotNull(message = "步骤编号不能为空")
    @com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = cn.iocoder.yudao.module.emergency.framework.jackson.LongDeserializer.class)
    private Long id;
}
















