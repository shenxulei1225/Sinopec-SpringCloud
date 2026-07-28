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

    default List<DmDataTabLayoutDO> selectListByEntityTypeCode(String entityTypeCode) {
        return selectList(new LambdaQueryWrapperX<DmDataTabLayoutDO>()
                .eq(DmDataTabLayoutDO::getEntityTypeCode, entityTypeCode)
                .eq(DmDataTabLayoutDO::getDeleted, false)
                .orderByAsc(DmDataTabLayoutDO::getColumnKind)
                .orderByAsc(DmDataTabLayoutDO::getPerspectiveId)
                .orderByAsc(DmDataTabLayoutDO::getId));
    }

    default boolean existsByScope(String entityTypeCode, String columnKind, String perspectiveId) {
        LambdaQueryWrapperX<DmDataTabLayoutDO> wrapper = new LambdaQueryWrapperX<DmDataTabLayoutDO>()
                .eq(DmDataTabLayoutDO::getEntityTypeCode, entityTypeCode)
                .eq(DmDataTabLayoutDO::getColumnKind, columnKind)
                .eq(DmDataTabLayoutDO::getDeleted, false);
        if (perspectiveId == null) {
            wrapper.isNull(DmDataTabLayoutDO::getPerspectiveId);
        } else {
            wrapper.eq(DmDataTabLayoutDO::getPerspectiveId, perspectiveId);
        }
        return selectCount(wrapper) > 0;
    }

    /** MODEL / ENTITY 等非 CATEGORY 列：任一有效行存在即视为已初始化 */
    default boolean existsByKind(String entityTypeCode, String columnKind) {
        return selectCount(new LambdaQueryWrapperX<DmDataTabLayoutDO>()
                .eq(DmDataTabLayoutDO::getEntityTypeCode, entityTypeCode)
                .eq(DmDataTabLayoutDO::getColumnKind, columnKind)
                .eq(DmDataTabLayoutDO::getDeleted, false)) > 0;
    }

    /** 保存后清理同 entity_type_code 下已逻辑删除的历史行，避免表无限膨胀 */
    @Delete("DELETE FROM dm_data_tab_layout WHERE entity_type_code = #{entityTypeCode} AND deleted = true")
    void deletePhysicalSoftDeletedByEntityTypeCode(@Param("entityTypeCode") String entityTypeCode);
}
