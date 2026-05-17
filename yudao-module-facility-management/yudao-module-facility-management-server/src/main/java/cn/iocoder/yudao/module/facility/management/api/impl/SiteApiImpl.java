package cn.iocoder.yudao.module.facility.management.api.impl;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.facility.management.api.SiteApi;
import cn.iocoder.yudao.module.facility.management.api.dto.SiteRespDTO;
import cn.iocoder.yudao.module.facility.management.service.query.SiteQueryService;
import cn.iocoder.yudao.module.facility.management.service.query.model.SiteView;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 区域 API 实现类
 */
@Slf4j
@Service
public class SiteApiImpl implements SiteApi {

    @Resource
    private SiteQueryService siteQueryService;

    @Override
    public CommonResult<List<SiteRespDTO>> getSiteTree() {
        List<SiteView> views = siteQueryService.getSiteTree();
        return CommonResult.success(toDTOList(views));
    }

    @Override
    public CommonResult<SiteRespDTO> getSite(Long id) {
        SiteView view = siteQueryService.getSiteView(id);
        return CommonResult.success(toDTO(view));
    }

    @Override
    public CommonResult<List<Long>> listChildIds(Long parentId) {
        return CommonResult.success(siteQueryService.getChildIds(parentId));
    }

    private SiteRespDTO toDTO(SiteView view) {
        if (view == null) {
            return null;
        }
        SiteRespDTO dto = new SiteRespDTO();
        dto.setSiteId(view.getSiteId());
        dto.setSiteCode(view.getSiteCode());
        dto.setSiteName(view.getSiteName());
        dto.setParentId(view.getParentId());
        dto.setSortNo(view.getSortNo());
        return dto;
    }

    private List<SiteRespDTO> toDTOList(List<SiteView> views) {
        return views.stream().map(this::toDTO).collect(java.util.stream.Collectors.toList());
    }

}
