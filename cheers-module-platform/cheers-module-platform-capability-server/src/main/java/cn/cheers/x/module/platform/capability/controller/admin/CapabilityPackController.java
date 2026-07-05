package cn.cheers.x.module.platform.capability.controller.admin;

import cn.cheers.x.module.platform.capability.api.dto.CapabilityPackRespDTO;
import cn.cheers.x.module.platform.capability.service.CapabilityPackService;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 平台能力包")
@RestController
@RequestMapping("/platform/capability-packs")
public class CapabilityPackController {

    @Resource
    private CapabilityPackService capabilityPackService;

    @GetMapping
    @Operation(summary = "能力包列表")
    public CommonResult<List<CapabilityPackRespDTO>> list(
            @RequestParam(value = "domain", required = false) String domain) {
        return success(capabilityPackService.list(domain));
    }

    @GetMapping("/{packId}")
    @Operation(summary = "能力包详情")
    public CommonResult<CapabilityPackRespDTO> get(@PathVariable("packId") String packId) {
        return success(capabilityPackService.getById(packId));
    }
}
