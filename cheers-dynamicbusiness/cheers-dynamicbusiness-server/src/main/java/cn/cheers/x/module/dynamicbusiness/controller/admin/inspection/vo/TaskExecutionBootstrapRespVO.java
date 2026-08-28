package cn.cheers.x.module.dynamicbusiness.controller.admin.inspection.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Schema(description = "开跑 bootstrap 结果")
@Data
public class TaskExecutionBootstrapRespVO {

    private Long executionRecordId;
    private List<Long> stepIds;
}
