package cn.iocoder.yudao.module.twin.controller.admin.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - Twin 解绑请求")
@Data
public class TwinMappingUnbindReqVO {

    @Schema(description = "映射 ID")
    private Long mappingId;

    @Schema(description = "设施 ID")
    private Long facilityId;

    @Schema(description = "ActorInstance ID")
    private Long actorInstanceId;

    @Schema(description = "解绑原因/备注")
    private String remark;
}
