package cn.cheers.x.intelligentanalysis.api.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 标准分析任务结果。
 *
 * <p>normalizedResult 使用平台结果结构。厂商原始响应不会通过公共契约向动作扩散。</p>
 */
@Data
public class AnalysisJobRespDTO {

    private String jobNo;

    private Long analysisCapabilityId;

    private String capabilityCode;

    private String capabilityVersion;

    private String inputType;

    private String status;

    private Map<String, Object> normalizedResult = new LinkedHashMap<>();

    private String failureMessage;

    private LocalDateTime createTime;

    private LocalDateTime completedTime;
}
