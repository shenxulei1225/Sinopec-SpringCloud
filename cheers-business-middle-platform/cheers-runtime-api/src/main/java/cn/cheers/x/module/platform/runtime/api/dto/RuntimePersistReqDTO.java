package cn.cheers.x.module.platform.runtime.api.dto;

import cn.cheers.x.module.platform.contract.dto.runtime.RuntimeJobDTO;
import cn.cheers.x.module.platform.contract.dto.slot.ScheduleSlotDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * L4 持久化 RPC 请求。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RuntimePersistReqDTO {

    private RuntimeJobDTO job;
    private List<ScheduleSlotDTO> slots;
    private Long siteId;

    /**
     * 为 true 时仅追加计划点到已有运行作业，不新建 job（重排 release-then-insert 用）。
     */
    private Boolean appendSlotsOnly;
}
