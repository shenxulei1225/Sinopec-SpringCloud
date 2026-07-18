package cn.cheers.x.module.platform.topology.api;

import cn.cheers.x.module.platform.contract.dto.topology.TopologyGraphDTO;
import cn.cheers.x.module.platform.topology.service.TopologyGraphService;
import cn.cheers.x.framework.common.pojo.CommonResult;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import static cn.cheers.x.framework.common.pojo.CommonResult.success;

@RestController
@Validated
public class TopologyGraphApiImpl implements TopologyGraphApi {

    @Resource
    private TopologyGraphService topologyGraphService;

    @Override
    public CommonResult<TopologyGraphDTO> getGraph(String topologyRef) {
        return success(topologyGraphService.getGraph(topologyRef));
    }
}
