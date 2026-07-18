package cn.cheers.x.module.platform.topology.api;

import cn.cheers.x.module.platform.contract.dto.network.PathNetworkDTO;
import cn.cheers.x.module.platform.contract.dto.network.PortalDTO;
import cn.cheers.x.module.platform.topology.service.PathNetworkService;

import java.util.List;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@RestController
@Validated
public class PathNetworkApiImpl implements PathNetworkApi {

    @Resource
    private PathNetworkService pathNetworkService;

    @Override
    public CommonResult<PathNetworkDTO> getNetwork(String networkRef) {
        return success(pathNetworkService.getNetwork(networkRef));
    }

    @Override
    public CommonResult<List<PortalDTO>> listPortals(Long facilityId) {
        return success(pathNetworkService.listPortals(facilityId));
    }
}
