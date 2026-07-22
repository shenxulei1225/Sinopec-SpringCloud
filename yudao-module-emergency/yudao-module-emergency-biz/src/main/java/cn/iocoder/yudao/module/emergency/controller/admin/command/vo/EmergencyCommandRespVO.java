package cn.iocoder.yudao.module.emergency.controller.admin.command.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * 管理后台 - 应急指令 Response VO
 */
@Schema(description = "管理后台 - 应急指令 Response VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class EmergencyCommandRespVO extends EmergencyCommandBaseVO {

    @Schema(description = "创建时间", example = "2024-12-18 10:00:00")
    private LocalDateTime createTime;

    @Schema(description = "最后更新时间", example = "2024-12-18 10:30:00")
    private LocalDateTime updateTime;
}
