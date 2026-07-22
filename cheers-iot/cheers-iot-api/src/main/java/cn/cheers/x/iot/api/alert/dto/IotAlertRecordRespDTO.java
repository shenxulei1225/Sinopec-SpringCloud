package cn.cheers.x.iot.api.alert.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * IoT 告警记录读模型（跨模块只暴露关联与展示所需字段）。
 */
@Schema(description = "RPC - IoT 告警记录")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IotAlertRecordRespDTO {

    @Schema(description = "告警记录编号")
    private Long id;

    @Schema(description = "告警配置编号")
    private Long configId;

    @Schema(description = "告警名称")
    private String configName;

    @Schema(description = "告警级别")
    private Integer configLevel;

    @Schema(description = "设备编号")
    private Long deviceId;

    @Schema(description = "产品编号")
    private Long productId;

    @Schema(description = "是否已处理")
    private Boolean processStatus;
}
