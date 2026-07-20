package cn.iocoder.yudao.module.emergency.controller.admin.task.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class TaskCompleteReqVO {

    @Schema(description = "完成说明")
    private String comment;

    @Schema(description = "附件")
    private List<String> attachments;

    @Schema(description = "及时上报记录内容")
    @Size(max = 2000)
    private String timelyReport;
}

















