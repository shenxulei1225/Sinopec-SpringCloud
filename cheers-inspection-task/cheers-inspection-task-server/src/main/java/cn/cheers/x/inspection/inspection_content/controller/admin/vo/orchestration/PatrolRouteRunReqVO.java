package cn.cheers.x.inspection.inspection_content.controller.admin.vo.orchestration;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 组路线预览/确认请求：选对象与设施，触发编排 run。
 */
@Data
public class PatrolRouteRunReqVO {

    @NotNull(message = "facilityId 不能为空")
    private Long facilityId;

    @NotEmpty(message = "objectIds 不能为空")
    private List<Long> objectIds;

    /** 多条已发布路网时显式指定 */
    private String preferredNetworkRef;
}
