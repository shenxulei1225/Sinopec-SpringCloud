package cn.cheers.x.module.platform.capability.api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MappingProfileSaveReqDTO {

    @NotBlank(message = "id 不能为空")
    private String id;

    @NotBlank(message = "businessTypeCode 不能为空")
    private String businessTypeCode;

    @NotBlank(message = "sourceModelCode 不能为空")
    private String sourceModelCode;

    private String displayName;
    /** Work Item 字段名 → L1 customFields 键名 */
    private Map<String, String> fieldMappings;
    private Integer defaultDurationMinutes;
    private Integer defaultPriority;
}
