package cn.cheers.x.module.dynamicbusiness.api.capability.dto;

import lombok.Data;

/**
 * 数据能力实例摘要（RPC 列表）。
 */
@Data
public class CapabilityInstanceSummaryDTO {

    private String instanceKey;
    private String label;
    private String domain;
    private String businessTypeCode;
    private Long modelId;
    private String resourceCode;
}
