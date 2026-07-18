package cn.cheers.x.module.platform.topology.api;

import cn.cheers.x.module.platform.contract.dto.topology.TopologyGraphDTO;
import cn.cheers.x.module.platform.topology.enums.ApiConstants;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = ApiConstants.NAME)
public interface TopologyGraphApi {

    String PREFIX = ApiConstants.PREFIX;

    @GetMapping(PREFIX + "/graphs/{topologyRef}")
    @Operation(summary = "按 topologyRef 读取拓扑图")
    CommonResult<TopologyGraphDTO> getGraph(@PathVariable("topologyRef") String topologyRef);
}
