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

@Tag(name = "管理后台 - 站场拓扑图")
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

    @GetMapping("/sites/{siteId}/graph/draft")
    @Operation(summary = "读取站场草稿拓扑图")
    public CommonResult<TopologyGraphDTO> getDraft(@PathVariable("siteId") Long siteId) {
        return success(topologyGraphService.getDraftBySiteId(siteId));
    }

    @PutMapping("/sites/{siteId}/graph")
    @Operation(summary = "保存站场草稿拓扑图")
    public CommonResult<TopologyGraphDTO> saveDraft(
            @PathVariable("siteId") Long siteId,
            @Valid @RequestBody TopologyGraphSaveReqDTO request) {
        return success(topologyGraphService.saveDraft(siteId, request));
    }

    @PostMapping("/sites/{siteId}/graph/publish")
    @Operation(summary = "发布站场拓扑图")
    public CommonResult<TopologyGraphDTO> publish(@PathVariable("siteId") Long siteId) {
        return success(topologyGraphService.publish(siteId));
    }

    @PostMapping("/sites/{siteId}/graph/validate")
    @Operation(summary = "校验拓扑图")
    public CommonResult<TopologyValidateRespDTO> validate(
            @PathVariable("siteId") Long siteId,
            @Valid @RequestBody TopologyGraphSaveReqDTO request) {
        return success(topologyGraphService.validate(siteId, request));
    }

    @PostMapping("/sites/{siteId}/import/legacy")
    @Operation(summary = "从旧库导入拓扑并保存为草稿")
    public CommonResult<TopologyGraphDTO> importLegacy(@PathVariable("siteId") Long siteId) {
        return success(topologyGraphService.importLegacy(siteId));
    }
}
