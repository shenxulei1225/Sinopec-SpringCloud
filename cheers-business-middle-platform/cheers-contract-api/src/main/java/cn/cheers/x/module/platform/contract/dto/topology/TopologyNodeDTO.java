package cn.cheers.x.module.platform.contract.dto.topology;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TopologyNodeDTO {

    private String nodeId;
    private String nodeType;
    private String displayName;
    private TopologyPointDTO position;
    private Map<String, Object> payload;
}
