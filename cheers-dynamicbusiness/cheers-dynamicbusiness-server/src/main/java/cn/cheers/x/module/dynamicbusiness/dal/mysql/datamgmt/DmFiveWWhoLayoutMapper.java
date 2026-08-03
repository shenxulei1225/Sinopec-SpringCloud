package cn.cheers.x.module.dynamicbusiness.dal.mysql.datamgmt;

import cn.cheers.x.framework.mybatis.core.mapper.BaseMapperX;
import cn.cheers.x.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.datamgmt.DmFiveWWhoLayoutDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DmFiveWWhoLayoutMapper extends BaseMapperX<DmFiveWWhoLayoutDO> {

    default List<DmFiveWWhoLayoutDO> selectListByEntityTypeCode(String entityTypeCode) {
        return selectList(new LambdaQueryWrapperX<DmFiveWWhoLayoutDO>()
                .eq(DmFiveWWhoLayoutDO::getEntityTypeCode, entityTypeCode)
                .eq(DmFiveWWhoLayoutDO::getDeleted, false)
                .orderByAsc(DmFiveWWhoLayoutDO::getColumnKind)
                .orderByAsc(DmFiveWWhoLayoutDO::getPerspectiveId)
                .orderByAsc(DmFiveWWhoLayoutDO::getId));
    }
}
