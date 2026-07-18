package cn.cheers.x.facility.management.service.sitetype;

import cn.cheers.x.facility.management.controller.admin.vo.site.SiteTypeCreateReqVO;
import cn.cheers.x.facility.management.controller.admin.vo.site.SiteTypeUpdateReqVO;
import cn.cheers.x.facility.management.dal.dataobject.SiteTypeDO;
import jakarta.validation.Valid;

import java.util.List;

/**
 * 站场类型服务接口
 */
public interface SiteTypeService {

    /**
     * 创建站场类型
     */
    Long createSiteType(@Valid SiteTypeCreateReqVO createReqVO);

    /**
     * 更新站场类型
     */
    void updateSiteType(@Valid SiteTypeUpdateReqVO updateReqVO);

    /**
     * 删除站场类型
     */
    void deleteSiteType(Long id);

    /**
     * 获取站场类型详情
     */
    SiteTypeDO getSiteType(Long id);

    /**
     * 获取所有正常状态的站场类型列表
     */
    List<SiteTypeDO> getNormalSiteTypeList();

    /**
     * 获取所有站场类型列表
     */
    List<SiteTypeDO> getAllSiteTypeList();

}
