package cn.cheers.x.inspection.orchestration.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatrolConfirmRespDTO {

    private Long routePlanId;

    private Long taskId;

    private Boolean dryRun;
}
