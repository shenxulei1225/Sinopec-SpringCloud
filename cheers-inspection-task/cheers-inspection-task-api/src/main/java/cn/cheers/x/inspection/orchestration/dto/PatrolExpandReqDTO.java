package cn.cheers.x.inspection.orchestration.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatrolExpandReqDTO {

    private Long facilityId;

    private List<Long> objectIds;

    private String preferredNetworkRef;

    private Long taskId;

    private Boolean fromConfirmedSnapshot;

    /** 种子工作项 id（可选，回写 workItems 时使用） */
    private String seedWorkId;
}
