package cn.iocoder.yudao.module.scene.platform.controller.admin.coordinate.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "管理后台 - 坐标校验问题响应 VO")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CoordinateValidationIssueRespVO {

    @Schema(description = "问题编码")
    private String code;

    @Schema(description = "字段名")
    private String field;

    @Schema(description = "级别")
    private String level;

    @Schema(description = "消息")
    private String message;

    @Schema(description = "建议")
    private String suggestion;
}
