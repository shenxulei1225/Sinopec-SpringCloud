package cn.iocoder.yudao.module.facility.management.service.site;

import cn.iocoder.yudao.module.facility.management.controller.admin.vo.site.SiteCreateReqVO;
import cn.iocoder.yudao.module.facility.management.controller.admin.vo.site.SiteUpdateReqVO;
import jakarta.validation.Valid;

/**
 * 站场管理服务
 */
public interface SiteService {

    /**
     * 创建站场
     */
    Long createSite(@Valid SiteCreateReqVO createReqVO);

    /**
     * 更新站场
     */
    void updateSite(@Valid SiteUpdateReqVO updateReqVO);

    /**
     * 删除站场
     */
    void deleteSite(Long id);

}
