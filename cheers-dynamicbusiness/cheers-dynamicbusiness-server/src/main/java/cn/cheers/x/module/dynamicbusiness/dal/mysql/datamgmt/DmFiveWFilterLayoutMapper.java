package cn.cheers.x.module.dynamicbusiness.dal.mysql.datamgmt;

import cn.cheers.x.framework.mybatis.core.mapper.BaseMapperX;
import cn.cheers.x.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.datamgmt.DmFiveWFilterLayoutDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DmFiveWFilterLayoutMapper extends BaseMapperX<DmFiveWFilterLayoutDO> {

    default List<DmFiveWFilterLayoutDO> selectListByEntityTypeCode(String entityTypeCode) {
        return selectList(new LambdaQueryWrapperX<DmFiveWFilterLayoutDO>()
                .eq(DmFiveWFilterLayoutDO::getEntityTypeCode, entityTypeCode)
                .eq(DmFiveWFilterLayoutDO::getDeleted, false)
                .orderByAsc(DmFiveWFilterLayoutDO::getColumnKind)
                .orderByAsc(DmFiveWFilterLayoutDO::getPerspectiveId)
                .orderByAsc(DmFiveWFilterLayoutDO::getId));
    }
}
