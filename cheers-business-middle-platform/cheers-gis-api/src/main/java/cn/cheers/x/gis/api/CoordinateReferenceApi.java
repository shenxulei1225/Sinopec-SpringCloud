package cn.cheers.x.gis.api;

import cn.cheers.x.gis.api.dto.CoordinateReferenceRespDTO;
import cn.cheers.x.gis.enums.GisApiConstants;
import cn.cheers.x.framework.common.pojo.CommonResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = GisApiConstants.NAME)
@Tag(name = "RPC 服务 - 坐标参考")
public interface CoordinateReferenceApi {

    String PREFIX = GisApiConstants.PREFIX + "/coordinate-reference";

    @GetMapping(PREFIX + "/get-by-scene-code")
    @Operation(summary = "按场景编码获取坐标参考")
    CommonResult<CoordinateReferenceRespDTO> getBySceneCode(@RequestParam("sceneCode") String sceneCode);
}
