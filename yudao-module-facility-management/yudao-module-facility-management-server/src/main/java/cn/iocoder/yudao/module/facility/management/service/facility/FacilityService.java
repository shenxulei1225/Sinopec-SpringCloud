package cn.iocoder.yudao.module.facility.management.service.facility;

import cn.iocoder.yudao.module.facility.management.controller.admin.vo.facility.FacilitySpatialSaveReqVO;

/**
 * 设施服务接口
 */
public interface FacilityService {

    /**
     * 保存设施空间信息
     */
    void saveSpatialInfo(FacilitySpatialSaveReqVO reqVO);

    /**
     * 更新设施状态
     */
    void updateFacilityStatus(Long facilityId, Integer status);

}
