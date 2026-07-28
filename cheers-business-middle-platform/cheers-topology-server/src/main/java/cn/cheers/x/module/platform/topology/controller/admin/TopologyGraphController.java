package cn.cheers.x.module.platform.topology.controller.admin;

import cn.cheers.x.module.platform.contract.dto.topology.TopologyGraphDTO;
import cn.cheers.x.module.platform.topology.api.dto.TopologyGraphSaveReqDTO;
import cn.cheers.x.module.platform.topology.api.dto.TopologyValidateRespDTO;
import cn.cheers.x.module.platform.topology.service.TopologyGraphService;
import cn.cheers.x.framework.common.pojo.CommonResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static cn.cheers.x.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 设施拓扑图")
@RestController
@RequestMapping("/platform/topology")
public class TopologyGraphController {

    @Resource
    private TopologyGraphService topologyGraphService;

    @GetMapping("/graphs/{topologyRef}")
    @Operation(summary = "按 topologyRef 读取拓扑图")
    public CommonResult<TopologyGraphDTO> getGraph(@PathVariable("topologyRef") String topologyRef) {
        return success(topologyGraphService.getGraph(topologyRef));
    }

    @GetMapping("/facilities/{facilityId}/graph/draft")
    @Operation(summary = "读取设施草稿拓扑图")
    public CommonResult<TopologyGraphDTO> getDraft(@PathVariable("facilityId") Long facilityId) {
        return success(topologyGraphService.getDraftByFacilityId(facilityId));
    }

    @PutMapping("/facilities/{facilityId}/graph")
    @Operation(summary = "保存设施草稿拓扑图")
    public CommonResult<TopologyGraphDTO> saveDraft(
            @PathVariable("facilityId") Long facilityId,
            @Valid @RequestBody TopologyGraphSaveReqDTO request) {
        return success(topologyGraphService.saveDraft(facilityId, request));
    }

    @PostMapping("/facilities/{facilityId}/graph/publish")
    @Operation(summary = "发布设施拓扑图")
    public CommonResult<TopologyGraphDTO> publish(@PathVariable("facilityId") Long facilityId) {
        return success(topologyGraphService.publish(facilityId));
    }

    @PostMapping("/facilities/{facilityId}/graph/validate")
    @Operation(summary = "校验拓扑图")
    public CommonResult<TopologyValidateRespDTO> validate(
            @PathVariable("facilityId") Long facilityId,
            @Valid @RequestBody TopologyGraphSaveReqDTO request) {
        return success(topologyGraphService.validate(facilityId, request));
    }

    @PostMapping("/facilities/{facilityId}/import/legacy")
    @Operation(summary = "从旧库导入拓扑并保存为草稿")
    public CommonResult<TopologyGraphDTO> importLegacy(@PathVariable("facilityId") Long facilityId) {
        return success(topologyGraphService.importLegacy(facilityId));
    }
}
