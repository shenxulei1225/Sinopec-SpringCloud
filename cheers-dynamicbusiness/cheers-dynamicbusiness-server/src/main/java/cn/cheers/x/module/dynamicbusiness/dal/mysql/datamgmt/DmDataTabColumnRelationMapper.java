package cn.cheers.x.module.dynamicbusiness.dal.mysql.datamgmt;

import cn.cheers.x.framework.mybatis.core.mapper.BaseMapperX;
import cn.cheers.x.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.datamgmt.DmDataTabColumnRelationDO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DmDataTabColumnRelationMapper extends BaseMapperX<DmDataTabColumnRelationDO> {

    default List<DmDataTabColumnRelationDO> selectListByLayoutId(Long layoutId) {
        return selectList(new LambdaQueryWrapperX<DmDataTabColumnRelationDO>()
                .eq(DmDataTabColumnRelationDO::getLayoutId, layoutId)
                .eq(DmDataTabColumnRelationDO::getDeleted, false)
                .orderByAsc(DmDataTabColumnRelationDO::getId));
    }

    @Delete("DELETE FROM dm_data_tab_column_relation WHERE layout_id = #{layoutId} AND deleted = true")
    void deletePhysicalSoftDeletedByLayoutId(@Param("layoutId") Long layoutId);
}
