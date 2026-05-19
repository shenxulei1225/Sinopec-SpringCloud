package cn.iocoder.yudao.module.facility.management.service.facility;

import cn.iocoder.yudao.module.facility.management.controller.admin.vo.facility.FacilityCreateReqVO;
import cn.iocoder.yudao.module.facility.management.controller.admin.vo.facility.FacilitySpatialSaveReqVO;
import cn.iocoder.yudao.module.facility.management.controller.admin.vo.facility.FacilityUpdateReqVO;
import cn.iocoder.yudao.module.facility.management.dal.dataobject.FacilityDO;

/**
 * 设施服务接口
 */
public interface FacilityService {

    /**
     * 创建设施
     *
     * @param reqVO 创建请求
     * @return 设施ID
     */
    Long createFacility(FacilityCreateReqVO reqVO);

    /**
     * 更新设施
     *
     * @param reqVO 更新请求
     */
    void updateFacility(FacilityUpdateReqVO reqVO);

    /**
     * 删除设施
     *
     * @param id 设施ID
     */
    void deleteFacility(Long id);

    /**
     * 获取设施详情
     *
     * @param id 设施ID
     * @return 设施DO
     */
    FacilityDO getFacility(Long id);

    /**
     * 根据编码获取设施
     *
     * @param facilityCode 设施编码
     * @return 设施DO
     */
    FacilityDO getFacilityByCode(String facilityCode);

    /**
     * 保存设施空间信息
     */
    void saveSpatialInfo(FacilitySpatialSaveReqVO reqVO);

    /**
     * 更新设施状态
     */
    void updateFacilityStatus(Long facilityId, Integer status);

}
