package cn.cheers.x.maintenance.dal.mysql;

import cn.cheers.x.framework.common.pojo.PageResult;
import cn.cheers.x.framework.mybatis.core.mapper.BaseMapperX;
import cn.cheers.x.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.cheers.x.maintenance.controller.admin.vo.handbook.HandbookPageReqVO;
import cn.cheers.x.maintenance.dal.dataobject.HandbookDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface HandbookMapper extends BaseMapperX<HandbookDO> {

    default Integer selectMaxVersionNoByCode(String code) {
        HandbookDO latest = selectOne(new LambdaQueryWrapperX<HandbookDO>()
                .eq(HandbookDO::getCode, code)
                .orderByDesc(HandbookDO::getVersionNo)
                .last("LIMIT 1"));
        return latest == null ? null : latest.getVersionNo();
    }

    default PageResult<HandbookDO> selectPage(HandbookPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<HandbookDO>()
                .eqIfPresent(HandbookDO::getCode, reqVO.getCode())
                .likeIfPresent(HandbookDO::getName, reqVO.getName())
                .eqIfPresent(HandbookDO::getScope, reqVO.getScope())
                .eqIfPresent(HandbookDO::getStatus, reqVO.getStatus())
                .orderByDesc(HandbookDO::getId));
    }
}
