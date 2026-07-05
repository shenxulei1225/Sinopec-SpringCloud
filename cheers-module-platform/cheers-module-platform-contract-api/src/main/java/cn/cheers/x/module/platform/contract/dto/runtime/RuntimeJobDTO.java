package cn.cheers.x.module.platform.contract.dto.runtime;

import cn.cheers.x.module.platform.contract.enums.RuntimeJobStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 运行作业（Runtime Job）— L4 根实例。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RuntimeJobDTO {

    private String contractVersion;
    private String runtimeJobId;
    private String businessTypeCode;
    private String triggerAction;
    private RuntimeJobStatus status;
    private List<String> sourceWorkIds;
    private String policySnapshotId;
    private String orchestrationRef;
    private String createdAt;
}
