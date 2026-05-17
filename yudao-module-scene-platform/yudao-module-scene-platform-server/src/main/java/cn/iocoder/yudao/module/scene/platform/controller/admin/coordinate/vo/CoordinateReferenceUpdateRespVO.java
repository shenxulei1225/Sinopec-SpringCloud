package cn.iocoder.yudao.module.scene.platform.controller.admin.coordinate.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Schema(description = "管理后台 - 坐标参考更新响应 VO")
@Data
public class CoordinateReferenceUpdateRespVO {

    @Schema(description = "是否成功")
    private Boolean success;

    @Schema(description = "是否严格校验")
    private Boolean strict;

    @Schema(description = "校验问题")
    private List<CoordinateValidationIssueRespVO> issues;

    @Schema(description = "警告信息")
    private List<String> warnings;
}
