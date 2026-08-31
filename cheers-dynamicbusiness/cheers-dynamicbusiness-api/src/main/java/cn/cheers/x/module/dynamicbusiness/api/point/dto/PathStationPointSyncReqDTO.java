package cn.cheers.x.module.dynamicbusiness.api.point.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * 路网节点 → 点位台账同步请求。
 * <p>一期同步停靠站 / 途径点 / 门点；点位类型写死枚举，与路网 NodeType 对齐。</p>
 */
@Schema(description = "路网节点 → 点位同步请求")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PathStationPointSyncReqDTO {

    @NotNull
    @Schema(description = "设施实体 id", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long facilityId;

    /**
     * 当前路网中应落账的节点（停靠站 / 途径点 / 门点）。
     * 空列表表示该设施下同步点位应全部软删（仍须显式传入，禁止 null 当「不删」）。
     */
    @NotNull
    @Valid
    @Builder.Default
    private List<StationNodeItem> stations = new ArrayList<>();

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StationNodeItem {
        @NotEmpty
        @Schema(description = "路网节点 nodeId", requiredMode = Schema.RequiredMode.REQUIRED)
        private String nodeId;

        @Schema(description = "显示名；空则用 nodeId")
        private String displayName;

        /**
         * 点位类型：STATION / TRAVERSAL / DOOR（与路网 NodeType 一致）。
         * 一期写死枚举；缺省按 STATION（兼容旧调用方）。
         */
        @Schema(description = "点位类型：STATION | TRAVERSAL | DOOR", example = "STATION")
        private String nodeType;

        /**
         * 归属网：HUMAN / GROUND_ROBOT / UAV（可多选）。
         * 空或 null → 同步侧按三网全开（兼容旧调用方）。
         */
        @Schema(description = "归属网多选：HUMAN | GROUND_ROBOT | UAV")
        private List<String> memberships;
    }
}
