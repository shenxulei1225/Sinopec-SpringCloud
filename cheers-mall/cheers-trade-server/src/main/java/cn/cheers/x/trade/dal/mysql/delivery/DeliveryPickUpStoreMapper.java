package cn.cheers.x.trade.dal.mysql.delivery;

import cn.cheers.x.framework.common.pojo.PageResult;
import cn.cheers.x.framework.mybatis.core.mapper.BaseMapperX;
import cn.cheers.x.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.cheers.x.trade.controller.admin.delivery.vo.pickup.DeliveryPickUpStorePageReqVO;
import cn.cheers.x.trade.dal.dataobject.delivery.DeliveryPickUpStoreDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DeliveryPickUpStoreMapper extends BaseMapperX<DeliveryPickUpStoreDO> {

    default PageResult<DeliveryPickUpStoreDO> selectPage(DeliveryPickUpStorePageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<DeliveryPickUpStoreDO>()
                .likeIfPresent(DeliveryPickUpStoreDO::getName, reqVO.getName())
                .eqIfPresent(DeliveryPickUpStoreDO::getPhone, reqVO.getPhone())
                .eqIfPresent(DeliveryPickUpStoreDO::getAreaId, reqVO.getAreaId())
                .eqIfPresent(DeliveryPickUpStoreDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(DeliveryPickUpStoreDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(DeliveryPickUpStoreDO::getId));
    }

    default List<DeliveryPickUpStoreDO> selectListByStatus(Integer status) {
        return selectList(DeliveryPickUpStoreDO::getStatus, status);
    }

}




