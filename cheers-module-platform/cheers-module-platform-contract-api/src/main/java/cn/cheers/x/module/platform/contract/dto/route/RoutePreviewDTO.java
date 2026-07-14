package cn.cheers.x.module.platform.contract.dto.route;

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
}
