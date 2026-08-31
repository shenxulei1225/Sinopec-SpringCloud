package cn.cheers.x.module.platform.contract.dto.network;

import cn.cheers.x.module.platform.contract.dto.topology.TopologyPointDTO;
import cn.cheers.x.module.platform.contract.enums.NetworkLayer;
import cn.cheers.x.module.platform.contract.enums.NodeType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PathNodeDTO {

    private String nodeId;
    private NodeType nodeType;
    private NetworkLayer layer;
    private Long zoneId;
    private String displayName;
    private TopologyPointDTO position;
    private Map<String, Object> payload;
    /**
     * 归属网多选：HUMAN / GROUND_ROBOT / UAV。
     * 空或 null 时同步侧按三网全开兼容旧数据。
     */
    private List<String> memberships;
}
