package cn.cheers.x.module.dynamicbusiness.api.point;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.module.dynamicbusiness.api.point.dto.PathStationPointSyncReqDTO;
import cn.cheers.x.module.dynamicbusiness.api.point.dto.PathStationPointSyncRespDTO;
import cn.cheers.x.module.dynamicbusiness.enums.ApiConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import jakarta.validation.Valid;

/**
 * 路网停靠站 → 数据管理点位同步 RPC。
 *
 * <p>路网保存后调用：upsert 停靠站对应点位，软删已移除停靠站。</p>
 */
@FeignClient(name = ApiConstants.NAME)
@Tag(name = "RPC 服务 - 路网点位同步")
public interface PathStationPointSyncApi {

    String PREFIX = ApiConstants.DYNAMICBUSINESS_PREFIX + "/point-station-sync";

    @PostMapping(PREFIX + "/from-path-network")
    @Operation(summary = "按设施同步路网停靠站到标准点位")
    CommonResult<PathStationPointSyncRespDTO> syncFromPathNetwork(
            @Valid @RequestBody PathStationPointSyncReqDTO req);
}
