package cn.cheers.x.maintenance.dal.mysql;

import cn.cheers.x.framework.common.pojo.PageResult;
import cn.cheers.x.framework.mybatis.core.mapper.BaseMapperX;
import cn.cheers.x.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.cheers.x.maintenance.controller.admin.vo.corrective.CorrectiveCasePageReqVO;
import cn.cheers.x.maintenance.dal.dataobject.CorrectiveCaseDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CorrectiveCaseMapper extends BaseMapperX<CorrectiveCaseDO> {
    default PageResult<CorrectiveCaseDO> selectPage(CorrectiveCasePageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<CorrectiveCaseDO>()
                .likeIfPresent(CorrectiveCaseDO::getTitle, reqVO.getTitle())
                .eqIfPresent(CorrectiveCaseDO::getStatus, reqVO.getStatus())
                .orderByDesc(CorrectiveCaseDO::getId));
    }
}
