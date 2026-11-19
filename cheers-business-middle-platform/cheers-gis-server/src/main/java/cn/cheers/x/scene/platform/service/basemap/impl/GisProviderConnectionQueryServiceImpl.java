package cn.cheers.x.scene.platform.service.basemap.impl;

import cn.cheers.x.scene.platform.controller.admin.basemap.vo.GisProviderConnectionRespVO;
import cn.cheers.x.scene.platform.dal.dataobject.basemap.GisProviderConnectionDO;
import cn.cheers.x.scene.platform.dal.mysql.basemap.GisProviderConnectionMapper;
import cn.cheers.x.scene.platform.enums.GisProviderCodeEnum;
import cn.cheers.x.scene.platform.service.basemap.GisProviderConnectionQueryService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 地图服务连接只读查询。数据只来自 gis_provider_connection 表（当前客户）。
 * 不读环境变量、不读前端文件。表里没有就是没配置，地图页把这种底图显示为不可选。
 */
@Service
public class GisProviderConnectionQueryServiceImpl implements GisProviderConnectionQueryService {

    /** 没填影像资源编号时用 Cesium Ion 的 2 号资源（Bing 卫星影像） */
    static final int DEFAULT_CESIUM_IMAGERY_ASSET_ID = 2;
    /** 本地瓦片服务没填令牌参数名时用 token */
    static final String DEFAULT_TOKEN_QUERY_PARAM = "token";

    @Resource
    private GisProviderConnectionMapper connectionMapper;

    @Override
    public List<GisProviderConnectionRespVO> listConnections() {
        Map<String, GisProviderConnectionDO> byCode = connectionMapper.selectList().stream()
                .collect(Collectors.toMap(GisProviderConnectionDO::getProviderCode, Function.identity(), (a, b) -> a));
        List<GisProviderConnectionRespVO> rows = new ArrayList<>();
        for (GisProviderCodeEnum provider : GisProviderCodeEnum.values()) {
            rows.add(toRespVO(provider, byCode.get(provider.getCode())));
        }
        return rows;
    }

    /** 天地图、Cesium 有令牌才算配置。本地瓦片服务有地址模板才算配置。 */
    static boolean isConfigured(GisProviderCodeEnum provider, GisProviderConnectionDO row) {
        if (row == null) {
            return false;
        }
        return provider.isTileTemplate()
                ? StringUtils.hasText(row.getUrlTemplate())
                : StringUtils.hasText(row.getToken());
    }

    private static GisProviderConnectionRespVO toRespVO(GisProviderCodeEnum provider, GisProviderConnectionDO row) {
        GisProviderConnectionRespVO vo = new GisProviderConnectionRespVO();
        vo.setProviderCode(provider.getCode());
        vo.setConfigured(isConfigured(provider, row));
        vo.setLabel(row != null && StringUtils.hasText(row.getLabel()) ? row.getLabel() : provider.getDefaultLabel());
        vo.setToken(row != null ? row.getToken() : null);
        if (provider.isTileTemplate()) {
            vo.setUrlTemplate(row != null ? row.getUrlTemplate() : null);
            vo.setTokenQueryParam(row != null && StringUtils.hasText(row.getTokenQueryParam())
                    ? row.getTokenQueryParam() : DEFAULT_TOKEN_QUERY_PARAM);
        }
        if (provider == GisProviderCodeEnum.CESIUM_ION) {
            vo.setImageryAssetId(row != null && row.getImageryAssetId() != null
                    ? row.getImageryAssetId() : DEFAULT_CESIUM_IMAGERY_ASSET_ID);
        }
        return vo;
    }
}
