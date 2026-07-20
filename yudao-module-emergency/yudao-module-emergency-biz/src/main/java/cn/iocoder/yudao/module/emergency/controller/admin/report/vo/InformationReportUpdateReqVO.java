package cn.iocoder.yudao.module.emergency.controller.admin.report.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Schema(description = "管理后台 - 信息报送更新 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class InformationReportUpdateReqVO extends InformationReportBaseVO {

    @Schema(description = "编号。注意：如果ID是很大的整数（超过2^53），前端应传递字符串类型以避免JavaScript精度丢失", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "编号不能为空")
    @com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = cn.iocoder.yudao.module.emergency.framework.jackson.LongDeserializer.class)
    private Long id;
}








