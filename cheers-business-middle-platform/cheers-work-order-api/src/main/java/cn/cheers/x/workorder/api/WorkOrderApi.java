package cn.cheers.x.workorder.api;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.workorder.api.dto.WorkOrderCreateReqDTO;
import cn.cheers.x.workorder.enums.ApiConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 工单标准服务 RPC
 */
@FeignClient(name = ApiConstants.NAME)
@Tag(name = "RPC 服务 - 工单")
public interface WorkOrderApi {

    @PostMapping(ApiConstants.PREFIX + "/create")
    @Operation(summary = "派工创建工单")
    CommonResult<Long> create(@Valid @RequestBody WorkOrderCreateReqDTO reqDTO);

}
