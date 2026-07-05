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
    private String topologyRef;
    private List<RoutePreviewSegmentDTO> segments;
    private Long totalDistanceMeters;
    private String decisionTraceId;
}
