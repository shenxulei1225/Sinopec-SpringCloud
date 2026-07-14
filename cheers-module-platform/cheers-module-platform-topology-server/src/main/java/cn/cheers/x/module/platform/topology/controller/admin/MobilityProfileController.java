package cn.cheers.x.module.platform.topology.controller.admin;

import cn.cheers.x.module.platform.contract.dto.network.MobilityProfileDTO;
import cn.cheers.x.module.platform.topology.service.query.MobilityProfileQueryService;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 路径机动剖面")
@RestController
@RequestMapping("/platform/path")
public class MobilityProfileController {

    @Resource
    private MobilityProfileQueryService mobilityProfileQueryService;

    @GetMapping("/mobility-profiles")
    @Operation(summary = "查询内置机动剖面列表")
    public CommonResult<List<MobilityProfileDTO>> listProfiles() {
        return success(mobilityProfileQueryService.listProfiles());
    }

    @GetMapping("/mobility-profiles/{profileId}")
    @Operation(summary = "按 profileId 读取机动剖面")
    public CommonResult<MobilityProfileDTO> getProfile(@PathVariable("profileId") String profileId) {
        return success(mobilityProfileQueryService.getProfile(profileId));
    }
}
