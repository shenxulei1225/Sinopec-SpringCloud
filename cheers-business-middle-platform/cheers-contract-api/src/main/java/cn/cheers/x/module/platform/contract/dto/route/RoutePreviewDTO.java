package cn.cheers.x.module.platform.contract.dto.route;

import cn.cheers.x.module.platform.contract.dto.topology.TopologyPointDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 路径预览（route preview）— 路径引擎输出，不写回拓扑图。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoutePreviewDTO {

    private String contractVersion;

    /**
     * @deprecated 请使用 {@link #networkRef}；保留以兼容旧消费方。
     */
    @Deprecated
    private String topologyRef;

    private String networkRef;
    private List<RoutePreviewSegmentDTO> segments;
    private Long totalDistanceMeters;
    private String decisionTraceId;

    /**
     * 规划必经站序：起点 → 检查停靠点 → 终点。同一点可出现两次（例如回到起点）。
     */
    private List<String> orderedStopIds;

    /**
     * 实际走过的点位序，含途径点；相邻段接头去重，重复经过的点保留多次。
     */
    private List<String> visitNodeIds;

    /**
     * 与 {@link #visitNodeIds} 一一对应的坐标，供结果图按经过顺序标号。
     */
    private List<TopologyPointDTO> visitPositions;
}
