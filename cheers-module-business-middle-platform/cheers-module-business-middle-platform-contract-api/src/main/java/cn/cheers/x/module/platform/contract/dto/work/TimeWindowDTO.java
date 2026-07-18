package cn.cheers.x.module.platform.contract.dto.work;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 时间窗（ISO-8601 字符串）。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TimeWindowDTO {

    private String start;
    private String end;
}
