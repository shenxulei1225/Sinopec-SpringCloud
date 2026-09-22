package cn.cheers.x.module.platform.contract.dto.work;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

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
    /** 首选设备；指定设备时只填这一台。 */
    private String fixedResourceId;
    /**
     * 同类候选设备（智能分配时的资源池成员）。
     * 资源优先换设备只认这份名单，不在引擎里另查设备台账。
     */
    private List<String> candidateResourceIds;
}
