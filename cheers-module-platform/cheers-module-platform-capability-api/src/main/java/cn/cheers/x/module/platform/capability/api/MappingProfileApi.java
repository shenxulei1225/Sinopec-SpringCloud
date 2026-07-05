package cn.cheers.x.module.platform.capability.api;

import cn.cheers.x.module.platform.capability.api.dto.ResolveWorkItemsReqDTO;
import cn.cheers.x.module.platform.capability.enums.ApiConstants;
import cn.cheers.x.module.platform.contract.dto.work.WorkItemDTO;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = ApiConstants.NAME)
@Tag(name = "RPC 服务 - 映射配置")
public interface MappingProfileApi {

    String PREFIX = ApiConstants.PREFIX;

    @PostMapping(PREFIX + "/mapping-profiles/{profileId}/resolve-work-items")
    @Operation(summary = "按映射配置解析作业项")
    CommonResult<List<WorkItemDTO>> resolveWorkItems(
            @PathVariable("profileId") String profileId,
            @RequestBody ResolveWorkItemsReqDTO request);
}
