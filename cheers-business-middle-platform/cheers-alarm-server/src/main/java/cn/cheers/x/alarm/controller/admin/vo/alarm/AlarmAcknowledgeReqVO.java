package cn.cheers.x.alarm.controller.admin.vo.alarm;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 管理后台 - 告警确认 Request VO
 */
@Schema(description = "管理后台 - 告警确认 Request VO")
@Data
public class AlarmAcknowledgeReqVO {

    @Schema(description = "确认备注", example = "已确认告警，正在处理")
    private String acknowledgeRemark;

}
