package cn.cheers.x.module.platform.contract.dto.work;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * 作业项（Work Item）— 排程引擎输入。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkItemDTO {

    private String contractVersion;
    private String workId;
    private String entityTypeCode;
    private String sourceModelCode;
    private String sourceInstanceId;
    private Integer durationEstimateMinutes;
    private Integer priority;
    private List<ResourceRequirementDTO> resourceRequirements;
    private TimePreferencesDTO timePreferences;
    private List<String> predecessorWorkIds;
    private Map<String, Object> payload;
}
