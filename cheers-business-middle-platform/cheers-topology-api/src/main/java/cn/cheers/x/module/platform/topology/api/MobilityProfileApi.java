package cn.cheers.x.module.platform.topology.api;

import cn.cheers.x.module.platform.contract.dto.network.MobilityProfileDTO;
import cn.cheers.x.module.platform.topology.enums.ApiConstants;
import cn.cheers.x.framework.common.pojo.CommonResult;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = ApiConstants.NAME)
public interface MobilityProfileApi {

    String PREFIX = ApiConstants.PATH_PREFIX;

    @GetMapping(PREFIX + "/mobility-profiles")
    @Operation(summary = "查询内置机动剖面列表")
    CommonResult<List<MobilityProfileDTO>> listProfiles();

    @GetMapping(PREFIX + "/mobility-profiles/{profileId}")
    @Operation(summary = "按 profileId 读取机动剖面")
    CommonResult<MobilityProfileDTO> getProfile(@PathVariable("profileId") String profileId);
}
