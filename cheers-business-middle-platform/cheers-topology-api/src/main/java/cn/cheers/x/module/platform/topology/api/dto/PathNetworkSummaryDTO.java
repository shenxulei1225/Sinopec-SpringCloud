package cn.cheers.x.module.platform.topology.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PathNetworkSummaryDTO {

    private String networkRef;
    private Long facilityId;
    private String displayName;
    private String description;
    private String status;
    /** 是否草稿（与 status=DRAFT 一致，便于前端展示） */
    private Boolean isDraft;
    private Integer version;
    private List<String> applicableEquipmentTypes;
    private Integer nodeCount;
    private Integer edgeCount;
}
