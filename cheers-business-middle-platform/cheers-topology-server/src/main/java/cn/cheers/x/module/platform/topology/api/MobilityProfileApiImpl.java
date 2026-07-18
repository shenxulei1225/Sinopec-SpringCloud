package cn.cheers.x.module.platform.topology.api;

import cn.cheers.x.module.platform.contract.dto.network.MobilityProfileDTO;
import cn.cheers.x.module.platform.topology.service.query.MobilityProfileQueryService;
import cn.cheers.x.framework.common.pojo.CommonResult;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static cn.cheers.x.framework.common.pojo.CommonResult.success;

@RestController
@Validated
public class MobilityProfileApiImpl implements MobilityProfileApi {

    @Resource
    private MobilityProfileQueryService mobilityProfileQueryService;

    @Override
    public CommonResult<List<MobilityProfileDTO>> listProfiles() {
        return success(mobilityProfileQueryService.listProfiles());
    }

    @Override
    public CommonResult<MobilityProfileDTO> getProfile(String profileId) {
        return success(mobilityProfileQueryService.getProfile(profileId));
    }
}
