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
public class PatrolConfirmReqDTO {

    /** 写入任务快照时必填 */
    private Long taskId;

    private Long facilityId;

    /** 路线方案名称（可选） */
    private String name;

    private List<WorkItemDTO> workItems;

    private Boolean dryRun;
}
