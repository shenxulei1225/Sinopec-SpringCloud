package cn.cheers.x.module.platform.contract.dto.route;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 路径请求（Route Request）— 路径引擎输入。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RouteRequestDTO {

    private String contractVersion;

    /**
     * @deprecated 请使用 {@link #networkRef}；读取时若 networkRef 为空则回退本字段。
     */
    @Deprecated
    private String topologyRef;

    private String networkRef;
    private List<String> networkRefs;
    private List<String> stopIds;
    /**
     * 起点 / 充电出发点（depot）。有值时顺序以它为首；
     * 配合 {@link #returnToStart} 在巡检结束后回到该点。
     */
    private String startStopId;
    /**
     * 是否回到起点充电。未传且 {@link #startStopId} 有值时默认 true。
     */
    private Boolean returnToStart;
    private String mobilityProfileId;
    private List<RouteLegDTO> legs;
    private String strategy;
    private String entityTypeCode;
    /** 设施编号（facilityId）；多网联程时用于加载 Portal，缺省取首张站场网络（SITE）的 facilityId。 */
    private Long facilityId;

    /**
     * 解析有效网络引用：优先 {@link #networkRef}，否则回退 {@link #topologyRef}。
     */
    public String resolvedNetworkRef() {
        if (networkRef != null && !networkRef.isBlank()) {
            return networkRef;
        }
        return topologyRef;
    }
}
