package cn.cheers.x.module.dynamicbusiness.dal.mysql.datamgmt;

import cn.cheers.x.framework.mybatis.core.mapper.BaseMapperX;
import cn.cheers.x.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.datamgmt.DmWorkbenchLayoutDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DmWorkbenchLayoutMapper extends BaseMapperX<DmWorkbenchLayoutDO> {

    default List<DmWorkbenchLayoutDO> selectTemplates() {
        return selectList(new LambdaQueryWrapperX<DmWorkbenchLayoutDO>()
                .eq(DmWorkbenchLayoutDO::getIsTemplate, true)
                .eq(DmWorkbenchLayoutDO::getDeleted, false)
                .orderByAsc(DmWorkbenchLayoutDO::getId));
    }

    default DmWorkbenchLayoutDO selectDefaultTemplate() {
        return selectOne(new LambdaQueryWrapperX<DmWorkbenchLayoutDO>()
                .eq(DmWorkbenchLayoutDO::getIsTemplate, true)
                .eq(DmWorkbenchLayoutDO::getDeleted, false)
                .eq(DmWorkbenchLayoutDO::getName, DmWorkbenchLayoutNames.DEFAULT_LEDGER_TEMPLATE)
                .orderByAsc(DmWorkbenchLayoutDO::getId)
                .last("LIMIT 1"));
    }
}
