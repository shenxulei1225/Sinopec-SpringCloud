package cn.cheers.x.scene.platform.service.basemap.impl;

import cn.cheers.x.framework.common.exception.util.ServiceExceptionUtil;
import cn.cheers.x.scene.platform.controller.admin.basemap.vo.GisProviderConnectionSaveReqVO;
import cn.cheers.x.scene.platform.dal.dataobject.basemap.GisProviderConnectionDO;
import cn.cheers.x.scene.platform.dal.mysql.basemap.GisProviderConnectionMapper;
import cn.cheers.x.scene.platform.enums.GisProviderCodeEnum;
import cn.cheers.x.scene.platform.service.basemap.GisProviderConnectionService;
import cn.cheers.x.scene.platform.service.basemap.support.GisTileUrlTemplates;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * GIS 管理页「保存」的唯一写入口。每个客户每种地图服务一行，按服务编码覆盖写。
 * 只存用户填的内容，不补默认值（默认的令牌参数名、影像资源编号在读取时给出）。
 */
@Service
public class GisProviderConnectionServiceImpl implements GisProviderConnectionService {

    @Resource
    private GisProviderConnectionMapper connectionMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveConnection(String providerCode, GisProviderConnectionSaveReqVO reqVO) {
        GisProviderCodeEnum provider = GisProviderCodeEnum.of(providerCode);
        if (provider == null) {
            throw ServiceExceptionUtil.invalidParamException("不认识的地图服务：" + providerCode);
        }
        GisProviderConnectionDO existing = connectionMapper.selectByProviderCode(provider.getCode());
        // 1. 关键项为空 = 用户在页面上清除这一种服务
        if (isClearRequest(provider, reqVO)) {
            if (existing != null) {
                connectionMapper.deleteById(existing.getId());
            }
            return;
        }
        if (provider.isTileTemplate()) {
            GisTileUrlTemplates.validate(reqVO.getUrlTemplate().trim());
        }
        // 2. 有就覆盖，没有就新建
        GisProviderConnectionDO row = toDO(provider, reqVO);
        if (existing == null) {
            connectionMapper.insert(row);
            return;
        }
        // 逐列写入：按 id 更新会跳过空值，清不掉用户删掉的旧令牌
        // 传一个空对象，让框架照常填更新人、更新时间
        connectionMapper.update(new GisProviderConnectionDO(), new LambdaUpdateWrapper<GisProviderConnectionDO>()
                .eq(GisProviderConnectionDO::getId, existing.getId())
                .set(GisProviderConnectionDO::getToken, row.getToken())
                .set(GisProviderConnectionDO::getLabel, row.getLabel())
                .set(GisProviderConnectionDO::getUrlTemplate, row.getUrlTemplate())
                .set(GisProviderConnectionDO::getTokenQueryParam, row.getTokenQueryParam())
                .set(GisProviderConnectionDO::getImageryAssetId, row.getImageryAssetId()));
    }

    private static boolean isClearRequest(GisProviderCodeEnum provider, GisProviderConnectionSaveReqVO reqVO) {
        return provider.isTileTemplate()
                ? !StringUtils.hasText(reqVO.getUrlTemplate())
                : !StringUtils.hasText(reqVO.getToken());
    }

    /** 只留这一种服务用得到的字段，用不到的写空，避免残留旧值。 */
    private static GisProviderConnectionDO toDO(GisProviderCodeEnum provider, GisProviderConnectionSaveReqVO reqVO) {
        GisProviderConnectionDO row = new GisProviderConnectionDO();
        row.setProviderCode(provider.getCode());
        row.setToken(trimToNull(reqVO.getToken()));
        row.setLabel(trimToNull(reqVO.getLabel()));
        row.setUrlTemplate(provider.isTileTemplate() ? trimToNull(reqVO.getUrlTemplate()) : null);
        row.setTokenQueryParam(provider.isTileTemplate() ? trimToNull(reqVO.getTokenQueryParam()) : null);
        row.setImageryAssetId(provider == GisProviderCodeEnum.CESIUM_ION ? reqVO.getImageryAssetId() : null);
        return row;
    }

    private static String trimToNull(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }
}
