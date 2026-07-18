package cn.cheers.x.module.platform.topology.exchange;

import cn.cheers.x.module.platform.contract.enums.NetworkKind;
import cn.cheers.x.module.platform.exchange.dto.GeoJsonImportResultDTO;
import cn.cheers.x.framework.common.pojo.CommonResult;
import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static cn.cheers.x.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - GeoJSON 交换")
@RestController
@RequestMapping("/platform/path/exchange")
public class GeoJsonExchangeController {

    @Resource
    private GeoJsonExchangeService geoJsonExchangeService;

    @PostMapping("/import")
    @Operation(summary = "导入 GeoJSON FeatureCollection 为路径网络草稿")
    public CommonResult<GeoJsonImportResultDTO> importGeoJson(
            @RequestParam("facilityId") Long facilityId,
            @RequestParam("networkKind") NetworkKind networkKind,
            @RequestBody JsonNode geoJson) {
        return success(geoJsonExchangeService.importGeoJson(facilityId, networkKind, geoJson));
    }

    @GetMapping("/export")
    @Operation(summary = "导出路径网络为 GeoJSON FeatureCollection")
    public CommonResult<JsonNode> exportGeoJson(
            @RequestParam(value = "facilityId", required = false) Long facilityId,
            @RequestParam(value = "networkKind", required = false) NetworkKind networkKind,
            @RequestParam(value = "networkRef", required = false) String networkRef) {
        if (networkRef != null && !networkRef.isBlank()) {
            return success(geoJsonExchangeService.exportByNetworkRef(networkRef));
        }
        if (facilityId == null || networkKind == null) {
            throw new IllegalArgumentException("facilityId 与 networkKind 或 networkRef 必须提供其一");
        }
        return success(geoJsonExchangeService.exportDraft(facilityId, networkKind));
    }
}
