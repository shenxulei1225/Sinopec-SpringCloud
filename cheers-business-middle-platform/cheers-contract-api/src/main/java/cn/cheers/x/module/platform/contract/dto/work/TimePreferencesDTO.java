package cn.cheers.x.module.platform.contract.dto.work;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 时间偏好（允许/禁止窗）。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TimePreferencesDTO {

    private List<TimeWindowDTO> allowedWindows;
    private List<TimeWindowDTO> forbiddenWindows;
}
