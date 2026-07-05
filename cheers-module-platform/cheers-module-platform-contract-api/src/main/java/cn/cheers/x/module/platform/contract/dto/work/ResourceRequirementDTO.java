package cn.cheers.x.module.platform.contract.dto.work;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 资源需求。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResourceRequirementDTO {

    private String resourceType;
    private Integer quantity;
    private String fixedResourceId;
}
