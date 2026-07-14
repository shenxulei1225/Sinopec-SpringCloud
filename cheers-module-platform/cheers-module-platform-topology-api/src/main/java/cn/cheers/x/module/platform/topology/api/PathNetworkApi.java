package cn.cheers.x.module.platform.topology.api;

import cn.cheers.x.module.platform.contract.dto.network.PathNetworkDTO;
import cn.cheers.x.module.platform.contract.dto.network.PortalDTO;
import cn.cheers.x.module.platform.topology.enums.ApiConstants;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = ApiConstants.NAME)
public interface PathNetworkApi {

    String PREFIX = ApiConstants.PATH_PREFIX;

    @GetMapping(PREFIX + "/networks/{networkRef}")
    @Operation(summary = "按 networkRef 读取路径网络")
    CommonResult<PathNetworkDTO> getNetwork(@PathVariable("networkRef") String networkRef);

    @GetMapping(PREFIX + "/portals")
    @Operation(summary = "查询设施 Portal 列表")
    CommonResult<List<PortalDTO>> listPortals(@RequestParam("facilityId") Long facilityId);
}
