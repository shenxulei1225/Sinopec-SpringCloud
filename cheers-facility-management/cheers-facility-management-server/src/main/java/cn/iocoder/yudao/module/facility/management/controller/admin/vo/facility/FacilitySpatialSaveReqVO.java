package cn.iocoder.yudao.module.facility.management.controller.admin.vo.facility;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 设施空间信息保存请求 VO（legacy接口使用）
 */
@Schema(description = "管理后台 - 设施空间信息保存请求 VO")
@Data
public class FacilitySpatialSaveReqVO {

    @NotNull(message = "设施ID不能为空")
    @Schema(description = "设施ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long facilityId;

    @Schema(description = "位置")
    private Position position;

    @Schema(description = "旋转")
    private Position rotation;

    @Schema(description = "缩放")
    private Position scale;

    @Data
    public static class Position {
        private Double x;
        private Double y;
        private Double z;
    }
}
