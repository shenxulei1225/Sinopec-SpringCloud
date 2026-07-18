package cn.iocoder.yudao.module.inspection.inspection_content.dal.mysql.point;

import cn.cheers.x.framework.mybatis.core.mapper.BaseMapperX;
import cn.cheers.x.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.inspection.inspection_content.controller.admin.vo.point.InspectionExecutionPointPageReqVO;
import cn.iocoder.yudao.module.inspection.inspection_content.dal.dataobject.point.InspectionExecutionPointDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface InspectionExecutionPointMapper extends BaseMapperX<InspectionExecutionPointDO> {

    default InspectionExecutionPointDO selectByPointCode(String pointCode) {
        return selectOne(InspectionExecutionPointDO::getPointCode, pointCode);
    }

    default List<InspectionExecutionPointDO> selectListByObjectId(Long objectId) {
        return selectList(InspectionExecutionPointDO::getObjectId, objectId);
    }

    default List<InspectionExecutionPointDO> selectListBySiteIdAndStatus(Long siteId, String status) {
        return selectList(new LambdaQueryWrapperX<InspectionExecutionPointDO>()
                .eqIfPresent(InspectionExecutionPointDO::getSiteId, siteId)
                .eqIfPresent(InspectionExecutionPointDO::getStatus, status));
    }

    default List<InspectionExecutionPointDO> selectListByCondition(InspectionExecutionPointPageReqVO reqVO) {
        return selectList(new LambdaQueryWrapperX<InspectionExecutionPointDO>()
                .likeIfPresent(InspectionExecutionPointDO::getPointName, reqVO.getPointName())
                .likeIfPresent(InspectionExecutionPointDO::getPointCode, reqVO.getPointCode())
                .eqIfPresent(InspectionExecutionPointDO::getSiteId, reqVO.getSiteId())
                .eqIfPresent(InspectionExecutionPointDO::getObjectId, reqVO.getObjectId())
                .eqIfPresent(InspectionExecutionPointDO::getDeviceType, reqVO.getDeviceType())
                .eqIfPresent(InspectionExecutionPointDO::getStatus, reqVO.getStatus())
                .orderByAsc(InspectionExecutionPointDO::getWeight)
                .orderByAsc(InspectionExecutionPointDO::getId));
    }
}
