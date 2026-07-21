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
public class PatrolExpandRespDTO {

    private List<WorkItemDTO> workItems;

    private String networkRef;

    private List<String> stopIds;

    private String inspectionType;
}
