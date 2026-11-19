package cn.cheers.x.scene.platform.controller.admin.basemap;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.scene.platform.controller.admin.basemap.vo.GisProviderConnectionRespVO;
import cn.cheers.x.scene.platform.controller.admin.basemap.vo.GisProviderConnectionSaveReqVO;
import cn.cheers.x.scene.platform.service.basemap.GisProviderConnectionQueryService;
import cn.cheers.x.scene.platform.service.basemap.GisProviderConnectionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static cn.cheers.x.framework.common.pojo.CommonResult.success;

/**
 * 地图服务连接（按客户）。GIS 管理页读写，地图页只读。
 * 瓦片由浏览器拿这里的令牌直连地图服务器，后台不取瓦片。
 */
@Tag(name = "管理后台 - 地图服务连接")
@RestController
@RequestMapping("/gis/provider-connections")
@Validated
public class GisProviderConnectionController {

    @Resource
    private GisProviderConnectionQueryService connectionQueryService;
    @Resource
    private GisProviderConnectionService connectionService;

    /**
     * 登录即可读：打开任何地图都要拼瓦片地址，浏览器本来就会拿到这些令牌。
     * 令牌须是限域名的浏览器 key，GIS 管理页的填写说明里写明这一点。
     */
    @GetMapping
    @Operation(summary = "获得当前客户的地图服务连接")
    public CommonResult<List<GisProviderConnectionRespVO>> listConnections() {
        return success(connectionQueryService.listConnections());
    }

    @PutMapping("/{providerCode}")
    @Operation(summary = "保存一种地图服务连接，关键项为空表示清除")
    @PreAuthorize("@ss.hasPermission('gis:provider-connection:update')")
    public CommonResult<Boolean> saveConnection(@PathVariable String providerCode,
                                                @Valid @RequestBody GisProviderConnectionSaveReqVO reqVO) {
        connectionService.saveConnection(providerCode, reqVO);
        return success(true);
    }
}
