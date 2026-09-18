package cn.cheers.x.inspection.task.controller.admin.vo.task;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 前面步骤改了，后面已放行的步骤作废。
 *
 * <p>例如改了巡检对象：keepThroughStep=1，路线这一步还能进，资源和排期要重做；已保存路线清掉。</p>
 */
@Data
@Schema(description = "建任务后面步骤作废")
public class InspectionTaskCreateInvalidateReqVO {

    @NotNull(message = "保留到哪一步不能为空")
    @Schema(description = "最多还保留到哪一步，更后面的作废", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer keepThroughStep;
}
