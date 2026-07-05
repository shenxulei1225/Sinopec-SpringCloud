package cn.cheers.x.module.platform.capability.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CapabilityPackRespDTO {

    private String capabilityPackId;
    private String displayName;
    private String domain;
    private String version;
    private List<String> requiredEngines;
    private String orchestrationRef;
    private String defaultPolicyTemplateId;
    private Map<String, Object> manifest;
}
