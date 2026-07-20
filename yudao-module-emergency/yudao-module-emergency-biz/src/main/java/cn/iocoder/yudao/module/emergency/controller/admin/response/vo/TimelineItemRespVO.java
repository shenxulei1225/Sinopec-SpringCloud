package cn.iocoder.yudao.module.emergency.controller.admin.response.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.Map;

@Schema(description = "管理后台 - 响应时间轴条目")
@Data
public class TimelineItemRespVO {

    @Schema(description = "类型：report|assess|handling|task|upgrade|cancel")
    private String type;

    @Schema(description = "时间戳")
    private OffsetDateTime timestamp;

    @Schema(description = "数据载荷")
    private Map<String, Object> data;
}

















