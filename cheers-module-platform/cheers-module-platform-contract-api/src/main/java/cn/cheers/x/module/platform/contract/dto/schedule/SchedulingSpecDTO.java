package cn.cheers.x.module.platform.contract.dto.schedule;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 排程规格（Phase 1 可内嵌于请求体）。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SchedulingSpecDTO {

    private String mode;
    private String horizonStart;
    private String horizonEnd;
    private String conflictStrategy;
}
