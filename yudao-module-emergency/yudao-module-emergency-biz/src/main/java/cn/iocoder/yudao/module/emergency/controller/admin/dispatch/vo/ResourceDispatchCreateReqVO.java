package cn.iocoder.yudao.module.emergency.controller.admin.dispatch.vo;

import cn.iocoder.yudao.module.emergency.dal.dataobject.dispatch.ResourceDispatchDO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ResourceDispatchCreateReqVO {

    @Schema(description = "事件 ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    private Long eventId;

    @Schema(description = "响应 ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    private Long responseId;

    @Schema(description = "资源 ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    private Long resourceId;

    @Schema(description = "预计到达分钟")
    private Integer etaMinutes;

    @Schema(description = "备注")
    private String comment;

    public ResourceDispatchDO toDO() {
        return ResourceDispatchDO.builder()
                .eventId(eventId)
                .responseId(responseId)
                .resourceId(resourceId)
                .etaMinutes(etaMinutes)
                .comment(comment)
                .status("pending")
                .build();
    }
}

















