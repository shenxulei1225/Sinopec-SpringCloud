package cn.cheers.x.module.platform.contract.dto.slot;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 已分配资源。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssignedResourceDTO {

    private String resourceType;
    private String resourceId;
}
