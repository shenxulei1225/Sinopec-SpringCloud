package cn.iocoder.yudao.module.facility.management.dal.mysql;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.facility.management.controller.admin.vo.facility.FacilityPageReqVO;
import cn.iocoder.yudao.module.facility.management.controller.admin.vo.facility.FacilitySearchReqVO;
import cn.iocoder.yudao.module.facility.management.dal.dataobject.FacilityDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.Collection;
import java.util.List;

/**
 * 设施台账 Mapper
 */
@Mapper
public interface FacilityMapper extends BaseMapperX<FacilityDO> {

    /**
     * 分页查询
     */
    default PageResult<FacilityDO> selectPage(FacilityPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<FacilityDO>()
                .likeIfPresent(FacilityDO::getFacilityName, reqVO.getFacilityName())
                .eqIfPresent(FacilityDO::getSiteId, reqVO.getSiteId())
                .eqIfPresent(FacilityDO::getCategoryId, reqVO.getCategoryId())
                .eqIfPresent(FacilityDO::getModel, reqVO.getModel())
                .eqIfPresent(FacilityDO::getStatus, reqVO.getStatus())
                .orderByAsc(FacilityDO::getSortNo)
                .orderByDesc(FacilityDO::getId));
    }

    /**
     * 搜索设施分页查询
     */
    default PageResult<FacilityDO> selectSearchPage(FacilitySearchReqVO reqVO) {
        LambdaQueryWrapperX<FacilityDO> wrapper = new LambdaQueryWrapperX<FacilityDO>()
                .eqIfPresent(FacilityDO::getSiteId, reqVO.getSiteId())
                .eqIfPresent(FacilityDO::getCategoryId, reqVO.getCategoryId())
                .eqIfPresent(FacilityDO::getModel, reqVO.getEquipmentType())
                .likeIfPresent(FacilityDO::getFacilityCode, reqVO.getKeyword())
                .likeIfPresent(FacilityDO::getFacilityName, reqVO.getKeyword())
                .orderByAsc(FacilityDO::getSortNo);
        return selectPage(reqVO, wrapper);
    }

    /**
     * 根据编码列表查询
     */
    default List<FacilityDO> selectByCodes(Collection<String> codes) {
        return selectList(FacilityDO::getFacilityCode, codes);
    }

    /**
     * 根据设施编码查询
     */
    default FacilityDO selectByCode(String facilityCode) {
        return selectOne(new LambdaQueryWrapperX<FacilityDO>()
                .eq(FacilityDO::getFacilityCode, facilityCode));
    }

    /**
     * 简单列表查询（用于下拉选择）
     */
    default List<FacilityDO> selectSimpleList(Long siteId, Long categoryId, String keyword) {
        LambdaQueryWrapperX<FacilityDO> wrapper = new LambdaQueryWrapperX<FacilityDO>()
                .eqIfPresent(FacilityDO::getStatus, 0);
        if (siteId != null) {
            wrapper.eq(FacilityDO::getSiteId, siteId);
        }
        if (categoryId != null) {
            wrapper.eq(FacilityDO::getCategoryId, categoryId);
        }
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like(FacilityDO::getFacilityName, keyword);
        }
        return selectList(wrapper.orderByAsc(FacilityDO::getSortNo).orderByAsc(FacilityDO::getId));
    }
}
