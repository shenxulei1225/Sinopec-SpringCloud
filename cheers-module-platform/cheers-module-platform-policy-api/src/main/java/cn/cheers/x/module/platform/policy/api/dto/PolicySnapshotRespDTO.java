package cn.cheers.x.module.platform.policy.api.dto;

import cn.cheers.x.module.platform.contract.dto.schedule.SchedulingSpecDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PolicySnapshotRespDTO {

    private String policySnapshotId;
    private String policySetId;
    private Integer policySetVersion;
    private String entityTypeCode;
    private SchedulingSpecDTO schedulingSpec;
}
