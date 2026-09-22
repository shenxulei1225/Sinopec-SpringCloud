package cn.cheers.x.module.platform.contract.dto.schedule;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 冲突检测弹窗里的一段窗：本任务计划或已有任务。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleConflictWindowDTO {

    private String start;
    private String end;
    private String label;
}
