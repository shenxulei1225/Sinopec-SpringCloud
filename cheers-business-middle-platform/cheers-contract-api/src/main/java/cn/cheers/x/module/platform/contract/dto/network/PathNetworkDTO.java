package cn.cheers.x.module.platform.contract.dto.network;

import cn.cheers.x.module.platform.contract.enums.NetworkKind;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PathNetworkDTO {

    private String networkRef;
    private NetworkKind networkKind;
    private Long facilityId;
    private Long scopeId;
    private String status;
    private Integer version;
    /** 路网显示名称 */
    private String displayName;
    /** 说明 */
    private String description;
    /**
     * 是否草稿。true：配置中的草稿；false：正式路网。
     * 仅状态区分，不是另存一条「发布」记录。
     */
    private Boolean isDraft;
    /** 适用设备类型：HUMAN / GROUND_ROBOT / UAV */
    private List<String> applicableEquipmentTypes;
    private List<PathNodeDTO> nodes;
    private List<PathEdgeDTO> edges;
    private List<PortalDTO> portals;
}
