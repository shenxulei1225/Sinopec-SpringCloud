package cn.cheers.x.module.dynamicbusiness.dal.mysql.datamgmt;

import cn.cheers.x.module.dynamicbusiness.dal.dataobject.datamgmt.DmEntityDimensionDO;
import cn.cheers.x.framework.mybatis.core.mapper.BaseMapperX;
import cn.cheers.x.framework.mybatis.core.query.LambdaQueryWrapperX;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DmEntityDimensionMapper extends BaseMapperX<DmEntityDimensionDO> {

    default List<DmEntityDimensionDO> selectListByEntityTypeCode(String entityTypeCode) {
        return selectList(new LambdaQueryWrapperX<DmEntityDimensionDO>()
                .eq(DmEntityDimensionDO::getEntityTypeCode, entityTypeCode)
                .eq(DmEntityDimensionDO::getDeleted, false)
                .orderByAsc(DmEntityDimensionDO::getDimensionKind)
                .orderByAsc(DmEntityDimensionDO::getPerspectiveId)
                .orderByAsc(DmEntityDimensionDO::getId));
    }

    default boolean existsByScope(String entityTypeCode, String dimensionKind, String perspectiveId) {
        LambdaQueryWrapperX<DmEntityDimensionDO> wrapper = new LambdaQueryWrapperX<DmEntityDimensionDO>()
                .eq(DmEntityDimensionDO::getEntityTypeCode, entityTypeCode)
                .eq(DmEntityDimensionDO::getDimensionKind, dimensionKind)
                .eq(DmEntityDimensionDO::getDeleted, false);
        if (perspectiveId == null) {
            wrapper.isNull(DmEntityDimensionDO::getPerspectiveId);
        } else {
            wrapper.eq(DmEntityDimensionDO::getPerspectiveId, perspectiveId);
        }
        return selectCount(wrapper) > 0;
    }

    /** MODEL / ENTITY 等非 CATEGORY 维：任一有效行存在即视为已初始化 */
    default boolean existsByKind(String entityTypeCode, String dimensionKind) {
        return selectCount(new LambdaQueryWrapperX<DmEntityDimensionDO>()
                .eq(DmEntityDimensionDO::getEntityTypeCode, entityTypeCode)
                .eq(DmEntityDimensionDO::getDimensionKind, dimensionKind)
                .eq(DmEntityDimensionDO::getDeleted, false)) > 0;
    }

    /** 保存后清理同 entity_type_code 下已逻辑删除的历史行，避免表无限膨胀 */
    @Delete("DELETE FROM dm_entity_dimension WHERE entity_type_code = #{entityTypeCode} AND deleted = true")
    void deletePhysicalSoftDeletedByEntityTypeCode(@Param("entityTypeCode") String entityTypeCode);
}
