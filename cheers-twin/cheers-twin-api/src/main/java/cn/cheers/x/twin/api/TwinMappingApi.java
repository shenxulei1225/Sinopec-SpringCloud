package cn.cheers.x.twin.api;

import cn.cheers.x.twin.api.dto.TwinMappingRespDTO;
import cn.cheers.x.twin.enums.ApiConstants;
import cn.cheers.x.framework.common.pojo.CommonResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = ApiConstants.NAME)
@Tag(name = "RPC 服务 - Twin 映射")
public interface TwinMappingApi {

    String PREFIX = ApiConstants.PREFIX + "/mapping";

    @GetMapping(PREFIX + "/facility/{facilityId}")
    @Operation(summary = "按 Facility 查询当前映射")
    CommonResult<TwinMappingRespDTO> getByFacilityId(@PathVariable("facilityId") Long facilityId);
}
