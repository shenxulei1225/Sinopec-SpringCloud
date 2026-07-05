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
public class ProcessCapabilityBindingRespDTO {

    private String businessTypeCode;
    private String capabilityPackId;
    private String orchestrationRef;
    private String policySetId;
    private List<String> mappingProfileIds;
    private Map<String, Object> engineBindings;
    private String status;
    private Integer version;
}
