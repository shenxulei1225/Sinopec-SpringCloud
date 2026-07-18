package cn.cheers.x.module.platform.topology.controller.admin;

import cn.cheers.x.module.platform.contract.dto.network.PathNetworkDTO;
import cn.cheers.x.module.platform.contract.dto.network.PortalDTO;
import cn.cheers.x.module.platform.contract.enums.NetworkKind;
import cn.cheers.x.module.platform.topology.api.dto.TopologyValidateRespDTO;
import cn.cheers.x.module.platform.topology.service.PathNetworkService;
import cn.cheers.x.framework.common.pojo.CommonResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static cn.cheers.x.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 路径网络")
@RestController
@RequestMapping("/platform/path")
public class PathNetworkController {

    @Resource
    private PathNetworkService pathNetworkService;

    @GetMapping("/networks/{networkRef}")
    @Operation(summary = "按 networkRef 读取路径网络")
    public CommonResult<PathNetworkDTO> getNetwork(@PathVariable("networkRef") String networkRef) {
        return success(pathNetworkService.getNetwork(networkRef));
    }

    @GetMapping("/networks/draft")
    @Operation(summary = "读取设施草稿路径网络")
    public CommonResult<PathNetworkDTO> getDraft(
            @RequestParam("facilityId") Long facilityId,
            @RequestParam("networkKind") NetworkKind networkKind) {
        return success(pathNetworkService.getDraft(facilityId, networkKind));
    }

    @PutMapping("/networks/draft")
    @Operation(summary = "保存设施草稿路径网络")
    public CommonResult<PathNetworkDTO> saveDraft(@RequestBody PathNetworkDTO request) {
        return success(pathNetworkService.saveDraft(request));
    }

    @PostMapping("/networks/draft/validate")
    @Operation(summary = "校验路径网络草稿")
    public CommonResult<TopologyValidateRespDTO> validate(@RequestBody PathNetworkDTO request) {
        return success(pathNetworkService.validate(request));
    }

    @PostMapping("/networks/draft/publish")
    @Operation(summary = "发布设施草稿路径网络")
    public CommonResult<PathNetworkDTO> publish(
            @RequestParam("facilityId") Long facilityId,
            @RequestParam("networkKind") NetworkKind networkKind) {
        return success(pathNetworkService.publish(facilityId, networkKind));
    }

    @GetMapping("/portals")
    @Operation(summary = "查询设施 Portal 列表")
    public CommonResult<List<PortalDTO>> listPortals(@RequestParam("facilityId") Long facilityId) {
        return success(pathNetworkService.listPortals(facilityId));
    }

    @PutMapping("/portals")
    @Operation(summary = "保存设施 Portal 列表")
    public CommonResult<List<PortalDTO>> savePortals(
            @RequestParam("facilityId") Long facilityId,
            @RequestBody List<PortalDTO> portals) {
        return success(pathNetworkService.savePortals(facilityId, portals));
    }
}
