package cn.cheers.x.module.platform.topology.controller.admin;

import cn.cheers.x.module.platform.contract.dto.network.PathNetworkDTO;
import cn.cheers.x.module.platform.contract.dto.network.PortalDTO;
import cn.cheers.x.module.platform.contract.enums.NetworkKind;
import cn.cheers.x.module.platform.topology.api.dto.PathNetworkSummaryDTO;
import cn.cheers.x.module.platform.topology.api.dto.TopologyValidateRespDTO;
import cn.cheers.x.module.platform.topology.controller.admin.vo.PathNetworkCreateReqVO;
import cn.cheers.x.module.platform.topology.controller.admin.vo.PathNetworkMetaUpdateReqVO;
import cn.cheers.x.module.platform.topology.service.PathNetworkService;
import cn.cheers.x.framework.common.pojo.CommonResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
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

    @GetMapping("/networks")
    @Operation(summary = "列出设施下路网（含正式与另存草稿）")
    public CommonResult<List<PathNetworkSummaryDTO>> listDrafts(
            @RequestParam("facilityId") Long facilityId) {
        return success(pathNetworkService.listDrafts(facilityId));
    }

    @GetMapping("/networks/published")
    @Operation(summary = "列出设施下已发布路网摘要（不含草稿）")
    public CommonResult<List<PathNetworkSummaryDTO>> listPublished(
            @RequestParam("facilityId") Long facilityId) {
        return success(pathNetworkService.listPublished(facilityId));
    }

    @PostMapping("/networks")
    @Operation(summary = "新建路网（正式记录，名称无草稿后缀）")
    public CommonResult<PathNetworkDTO> createDraft(@Valid @RequestBody PathNetworkCreateReqVO request) {
        return success(pathNetworkService.createDraft(request));
    }

    @PutMapping("/networks/{networkRef}/meta")
    @Operation(summary = "更新路网元数据（名称/说明/适用设备类型）")
    public CommonResult<PathNetworkDTO> updateMeta(
            @PathVariable("networkRef") String networkRef,
            @Valid @RequestBody PathNetworkMetaUpdateReqVO request) {
        return success(pathNetworkService.updateMeta(networkRef, request));
    }

    // 固定路径须写在 /networks/{networkRef} 之前，避免 draft 被当成 networkRef
    @GetMapping("/networks/draft")
    @Operation(summary = "读取设施草稿路径网络（兼容旧种类槽位）")
    public CommonResult<PathNetworkDTO> getDraft(
            @RequestParam("facilityId") Long facilityId,
            @RequestParam("networkKind") NetworkKind networkKind) {
        return success(pathNetworkService.getDraft(facilityId, networkKind));
    }

    @PutMapping("/networks/draft")
    @Operation(summary = "保存路网（body.isDraft=false 正式保存；true 则写为草稿状态）")
    public CommonResult<PathNetworkDTO> saveDraft(@RequestBody PathNetworkDTO request) {
        return success(pathNetworkService.saveDraft(request));
    }

    @PostMapping("/networks/save-as-draft")
    @Operation(summary = "另存为草稿（新开一条带草稿标记的副本）")
    public CommonResult<PathNetworkDTO> saveAsDraft(@RequestBody PathNetworkDTO request) {
        return success(pathNetworkService.saveAsDraft(request));
    }

    @PostMapping("/networks/draft/validate")
    @Operation(summary = "校验路径网络草稿")
    public CommonResult<TopologyValidateRespDTO> validate(@RequestBody PathNetworkDTO request) {
        return success(pathNetworkService.validate(request));
    }

    @PostMapping("/networks/draft/publish")
    @Operation(summary = "发布设施草稿路径网络（兼容旧种类槽位）")
    public CommonResult<PathNetworkDTO> publish(
            @RequestParam("facilityId") Long facilityId,
            @RequestParam("networkKind") NetworkKind networkKind) {
        return success(pathNetworkService.publish(facilityId, networkKind));
    }

    @DeleteMapping("/networks/{networkRef}")
    @Operation(summary = "删除草稿路网（仅草稿）")
    public CommonResult<Boolean> deleteDraft(@PathVariable("networkRef") String networkRef) {
        pathNetworkService.deleteDraft(networkRef);
        return success(true);
    }

    @GetMapping("/networks/{networkRef}")
    @Operation(summary = "按 networkRef 读取路径网络")
    public CommonResult<PathNetworkDTO> getNetwork(@PathVariable("networkRef") String networkRef) {
        return success(pathNetworkService.getNetwork(networkRef));
    }

    @PostMapping("/networks/{networkRef}/publish")
    @Operation(summary = "按 networkRef 发布草稿路网")
    public CommonResult<PathNetworkDTO> publishByRef(@PathVariable("networkRef") String networkRef) {
        return success(pathNetworkService.publishByRef(networkRef));
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
