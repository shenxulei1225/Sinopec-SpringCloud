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
}
