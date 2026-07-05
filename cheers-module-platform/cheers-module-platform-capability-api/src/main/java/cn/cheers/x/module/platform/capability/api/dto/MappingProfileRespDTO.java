package cn.cheers.x.module.platform.capability.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MappingProfileRespDTO {

    private String id;
    private String businessTypeCode;
    private String sourceModelCode;
    private String displayName;
    private Map<String, String> fieldMappings;
    private Integer defaultDurationMinutes;
    private Integer defaultPriority;
    private String status;
}
