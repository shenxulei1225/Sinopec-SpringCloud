package cn.cheers.x.module.dynamicbusiness.dal.mysql.datapermission;

import cn.cheers.x.framework.mybatis.core.mapper.BaseMapperX;
import cn.cheers.x.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.datapermission.PostAssignmentDO;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

/** 任职。只读写本表。 */
@Mapper
public interface PostAssignmentMapper extends BaseMapperX<PostAssignmentDO> {

    default List<PostAssignmentDO> selectListByUserId(Long userId) {
        return selectList(new LambdaQueryWrapperX<PostAssignmentDO>()
                .eq(PostAssignmentDO::getUserId, userId)
                .orderByAsc(PostAssignmentDO::getId));
    }

    default List<PostAssignmentDO> selectListByManagementUnitId(Long managementUnitId) {
        return selectList(new LambdaQueryWrapperX<PostAssignmentDO>()
                .eq(PostAssignmentDO::getManagementUnitId, managementUnitId)
                .orderByAsc(PostAssignmentDO::getDutyPostId)
                .orderByAsc(PostAssignmentDO::getId));
    }

    default List<PostAssignmentDO> selectListByManagementUnitIds(Collection<Long> managementUnitIds) {
        return selectList(new LambdaQueryWrapperX<PostAssignmentDO>()
                .in(PostAssignmentDO::getManagementUnitId, managementUnitIds)
                .orderByAsc(PostAssignmentDO::getDutyPostId)
                .orderByAsc(PostAssignmentDO::getId));
    }

    /** 这个人在这一天有效的任职：起始日期空或不晚于这天，截止日期空或不早于这天（含当天）。 */
    default List<PostAssignmentDO> selectActiveListByUserId(Long userId, LocalDate day) {
        return selectList(new LambdaQueryWrapperX<PostAssignmentDO>()
                .eq(PostAssignmentDO::getUserId, userId)
                .and(w -> w.isNull(PostAssignmentDO::getValidFrom).or().le(PostAssignmentDO::getValidFrom, day))
                .and(w -> w.isNull(PostAssignmentDO::getValidTo).or().ge(PostAssignmentDO::getValidTo, day))
                .orderByAsc(PostAssignmentDO::getId));
    }

    default boolean existsSame(Long userId, String managementUnitRegistry, Long managementUnitId, Long dutyPostId) {
        return selectCount(new LambdaQueryWrapperX<PostAssignmentDO>()
                .eq(PostAssignmentDO::getUserId, userId)
                .eq(PostAssignmentDO::getManagementUnitRegistry, managementUnitRegistry)
                .eq(PostAssignmentDO::getManagementUnitId, managementUnitId)
                .eq(PostAssignmentDO::getDutyPostId, dutyPostId)) > 0;
    }
}
