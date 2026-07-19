package cn.cheers.x.maintenance.dal.mysql;

import cn.cheers.x.framework.common.pojo.PageResult;
import cn.cheers.x.framework.mybatis.core.mapper.BaseMapperX;
import cn.cheers.x.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.cheers.x.maintenance.controller.admin.vo.standard.FieldWorkStandardPageReqVO;
import cn.cheers.x.maintenance.dal.dataobject.FieldWorkStandardDO;
import cn.cheers.x.maintenance.enums.FieldWorkStandardStatusEnum;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FieldWorkStandardMapper extends BaseMapperX<FieldWorkStandardDO> {

    default Integer selectMaxVersionNoByCode(String code) {
        FieldWorkStandardDO latest = selectOne(new LambdaQueryWrapperX<FieldWorkStandardDO>()
                .eq(FieldWorkStandardDO::getCode, code)
                .orderByDesc(FieldWorkStandardDO::getVersionNo)
                .last("LIMIT 1"));
        return latest == null ? null : latest.getVersionNo();
    }

    default PageResult<FieldWorkStandardDO> selectPage(FieldWorkStandardPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<FieldWorkStandardDO>()
                .eqIfPresent(FieldWorkStandardDO::getCode, reqVO.getCode())
                .likeIfPresent(FieldWorkStandardDO::getName, reqVO.getName())
                .eqIfPresent(FieldWorkStandardDO::getScope, reqVO.getScope())
                .eqIfPresent(FieldWorkStandardDO::getStatus, reqVO.getStatus())
                .orderByDesc(FieldWorkStandardDO::getId));
    }
}
