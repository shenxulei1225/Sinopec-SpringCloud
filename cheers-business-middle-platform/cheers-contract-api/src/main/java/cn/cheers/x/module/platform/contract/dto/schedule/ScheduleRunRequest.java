package cn.cheers.x.module.platform.contract.dto.schedule;

import cn.cheers.x.module.platform.contract.dto.work.SourceInstanceRefDTO;
import cn.cheers.x.module.platform.contract.dto.work.WorkItemDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 排程运行请求（Phase 1 编排入口使用）。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleRunRequest {

    private String contractVersion;
    private String entityTypeCode;
    private List<WorkItemDTO> workItems;
    /** Phase 3：L1 实例引用；与 workItems 二选一，由编排按 binding.mappingProfileIds 解析 */
    private List<SourceInstanceRefDTO> sourceInstances;
    private SchedulingSpecDTO schedulingSpec;
    private String orchestrationRef;
    /** Phase 2：已发布策略集 id；与 schedulingSpec 二选一，run 时解析为快照 */
    private String policySetId;
    private String policySnapshotId;

    /**
     * 排程落库成功后是否派工生成工单；默认 false。
     */
    @Builder.Default
    private Boolean dispatchWorkOrders = false;
    /** 业务域范围（scope），如 inspection */
    private String scope;
    /** 现场作业标准 ID；显式传入时优先于绑定解析 */
    private Long fieldWorkStandardId;
    /** 绑定解析用：资产 id（可空） */
    private Long assetId;
    /** 绑定解析用：资产类型编码（可空） */
    private String assetTypeCode;
    /** 绑定解析用：频率编码（可空） */
    private String frequencyCode;
}
