package cn.cheers.x.module.platform.topology.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TopologyValidateIssueDTO {

    private String code;
    private String severity;
    private String message;
    private String refId;
}
