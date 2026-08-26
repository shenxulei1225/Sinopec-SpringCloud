package cn.cheers.x.module.dynamicbusiness.service.model;

import cn.cheers.x.module.dynamicbusiness.controller.admin.model.vo.ModelFacilityFootprintRespVO;

/**
 * 型号设施覆盖范围只读服务。
 *
 * <p>权威来源是型号实体的所属设施列；本服务不使用型号发起设施推断覆盖范围。</p>
 */
public interface ModelFacilityFootprintQueryService {

    /**
     * 查询型号实体实际覆盖的设施数量与分类。
     *
     * @param modelId 型号编号
     * @return 设施覆盖范围
     */
    ModelFacilityFootprintRespVO getFacilityFootprint(Long modelId, Long effectiveFacilityId);
}
