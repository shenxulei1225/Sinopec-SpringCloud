package cn.cheers.x.inspection.orchestration.dto;

import cn.cheers.x.module.platform.contract.dto.work.WorkItemDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatrolSaveRouteReqDTO {

    /** 写入任务快照时必填 */
    private Long taskId;

    private Long facilityId;

    private List<WorkItemDTO> workItems;

    private Boolean dryRun;
}
