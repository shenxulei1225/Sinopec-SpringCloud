package cn.cheers.x.inspection.orchestration.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatrolExpandReqDTO {

    private Long facilityId;

    private List<Long> objectIds;

    private String preferredNetworkRef;

    private Long taskId;

    private Boolean fromConfirmedSnapshot;

    /** 种子工作项 id（可选，回写 workItems 时使用） */
    private String seedWorkId;

    /** 任务创建选定的起点（无人机起飞点） */
    private String startStopId;

    /** 任务创建选定的终点（无人机降落点） */
    private String endStopId;

    /** 是否回到起点 */
    private Boolean returnToStart;

    /**
     * 检查项位置展开后的路网点。算路只认这个，不读对象↔停靠点绑定。
     */
    private List<String> stopIds;

    /**
     * 任务已选巡检方式对应的路网类型（HUMAN / UAV / GROUND_ROBOT）。
     * 选网只认这个，不读被巡检设备上的对象巡检类型台账。
     */
    private String inspectionType;
}
