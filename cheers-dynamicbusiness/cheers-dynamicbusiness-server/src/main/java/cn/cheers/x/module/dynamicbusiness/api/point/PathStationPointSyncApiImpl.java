package cn.cheers.x.module.dynamicbusiness.api.point;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.module.dynamicbusiness.api.point.dto.PathStationPointSyncReqDTO;
import cn.cheers.x.module.dynamicbusiness.api.point.dto.PathStationPointSyncRespDTO;
import cn.cheers.x.module.dynamicbusiness.service.point.PathStationPointSyncService;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import static cn.cheers.x.framework.common.pojo.CommonResult.success;

/**
 * 路网停靠站 → 点位同步 RPC 实现。
 */
@RestController
@Validated
public class PathStationPointSyncApiImpl implements PathStationPointSyncApi {

    @Resource
    private PathStationPointSyncService pathStationPointSyncService;

    @Override
    public CommonResult<PathStationPointSyncRespDTO> syncFromPathNetwork(PathStationPointSyncReqDTO req) {
        return success(pathStationPointSyncService.syncFromPathNetwork(req));
    }
}
