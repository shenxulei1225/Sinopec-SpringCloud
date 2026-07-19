package cn.cheers.x.maintenance.api;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.maintenance.api.dto.FieldWorkStandardRespDTO;
import cn.cheers.x.maintenance.enums.ApiConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = ApiConstants.NAME)
@Tag(name = "RPC 服务 - 维护手册")
public interface MaintenanceApi {

    @GetMapping(ApiConstants.PREFIX + "/standards/get-published")
    @Operation(summary = "获取已发布现场作业标准")
    CommonResult<FieldWorkStandardRespDTO> getPublishedStandard(@RequestParam("id") Long id);
}
