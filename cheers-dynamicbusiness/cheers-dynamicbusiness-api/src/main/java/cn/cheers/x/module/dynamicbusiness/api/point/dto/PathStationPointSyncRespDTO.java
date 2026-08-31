package cn.cheers.x.module.dynamicbusiness.api.point.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "路网停靠站 → 点位同步结果")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PathStationPointSyncRespDTO {

    private int created;
    private int updated;
    private int deleted;
}
