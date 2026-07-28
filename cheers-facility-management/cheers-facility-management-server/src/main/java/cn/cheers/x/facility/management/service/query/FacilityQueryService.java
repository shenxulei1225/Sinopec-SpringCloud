package cn.cheers.x.facility.management.service.query;

import cn.cheers.x.framework.common.pojo.PageResult;
import cn.cheers.x.facility.management.controller.admin.vo.facility.FacilityPageReqVO;
import cn.cheers.x.facility.management.controller.admin.vo.facility.FacilitySearchReqVO;
import cn.cheers.x.facility.management.controller.admin.vo.facility.FacilityCategoryTreeResponse;
import cn.cheers.x.facility.management.controller.admin.vo.facility.FacilityTreeNodeVO;
import cn.cheers.x.facility.management.service.query.model.FacilityView;

import java.util.Collection;
import java.util.List;

/**
 * 设施查询服务接口
 */
public interface FacilityQueryService {

    /**
     * 获取设施详情视图
     */
    FacilityView getFacilityView(Long id);

    /**
     * 获取设施详情视图（根据编码）
     */
    FacilityView getFacilityViewByCode(String code);

    /**
     * 获取设施简单视图列表（用于巡检对象选择）
     */
    List<FacilityView> getFacilitySimpleViewList(Long stationId, Long categoryId, String keyword);

    /**
     * 根据ID列表获取设施视图
     */
    List<FacilityView> getFacilityViewListByIds(Collection<Long> ids);

    /**
     * 根据编码列表获取设施视图
     */
    List<FacilityView> getFacilityViewListByCodes(Collection<String> codes);

    /**
     * 分页查询设施
     */
    PageResult<FacilityView> getFacilityViewPage(FacilityPageReqVO pageReqVO);

    // ==================== Legacy 接口支持 ====================

    /**
     * 获取设施分类树
     */
    List<FacilityTreeNodeVO> getFacilityTree(Long stationId);

    /**
     * 获取设施类型列表
     */
    List<FacilityCategoryTreeResponse.FacilityTypeVO> getFacilityTypes();

    /**
     * 搜索设施（支持多条件）
     */
    PageResult<FacilityView> searchFacilities(FacilitySearchReqVO reqVO);

}
