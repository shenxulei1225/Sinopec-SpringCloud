package cn.cheers.x.module.platform.runtime.api.dto;

import cn.cheers.x.module.platform.runtime.enums.RuntimeSlotReleaseMode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 按运行作业释放未执行计划点占用。
 * <p>
 * 一期仅支持 {@link #runtimeJobId}；业务任务键（taskBusinessKey）由门面解析后再调用本接口。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RuntimeSlotReleaseReqDTO {

    @NotBlank
    private String runtimeJobId;

    @NotNull
    private RuntimeSlotReleaseMode mode;

    /** 业务原因文案，写入过程时间线摘要 */
    private String reason;

    private Long facilityId;
}
