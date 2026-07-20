package cn.iocoder.yudao.module.emergency.controller.admin.report.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - 信息报送提交 Request VO")
@Data
public class InformationReportSubmitReqVO {

    @Schema(description = "提交备注", example = "已提交报送")
    private String remark;
}








