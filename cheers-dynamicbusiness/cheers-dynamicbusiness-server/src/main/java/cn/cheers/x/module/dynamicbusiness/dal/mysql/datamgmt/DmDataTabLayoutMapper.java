package cn.cheers.x.module.dynamicbusiness.dal.mysql.datamgmt;

import cn.cheers.x.module.dynamicbusiness.dal.dataobject.datamgmt.DmDataTabLayoutDO;
import cn.cheers.x.framework.mybatis.core.mapper.BaseMapperX;
import cn.cheers.x.framework.mybatis.core.query.LambdaQueryWrapperX;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DmDataTabLayoutMapper extends BaseMapperX<DmDataTabLayoutDO> {

    default List<DmDataTabLayoutDO> selectListByLayoutId(Long layoutId) {
        return selectList(new LambdaQueryWrapperX<DmDataTabLayoutDO>()
                .eq(DmDataTabLayoutDO::getLayoutId, layoutId)
                .eq(DmDataTabLayoutDO::getDeleted, false)
                .orderByAsc(DmDataTabLayoutDO::getColumnKind)
                .orderByAsc(DmDataTabLayoutDO::getTabId)
                .orderByAsc(DmDataTabLayoutDO::getId));
    }

    default boolean existsByLayoutScope(Long layoutId, String columnKind, String tabId) {
        LambdaQueryWrapperX<DmDataTabLayoutDO> wrapper = new LambdaQueryWrapperX<DmDataTabLayoutDO>()
                .eq(DmDataTabLayoutDO::getLayoutId, layoutId)
                .eq(DmDataTabLayoutDO::getColumnKind, columnKind)
                .eq(DmDataTabLayoutDO::getDeleted, false);
        if (tabId == null) {
            wrapper.isNull(DmDataTabLayoutDO::getTabId);
        } else {
            wrapper.eq(DmDataTabLayoutDO::getTabId, tabId);
        }
        return selectCount(wrapper) > 0;
    }

    @Delete("DELETE FROM dm_data_tab_layout WHERE layout_id = #{layoutId} AND deleted = true")
    void deletePhysicalSoftDeletedByLayoutId(@Param("layoutId") Long layoutId);
}
