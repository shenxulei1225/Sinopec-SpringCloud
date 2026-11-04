package cn.cheers.x.module.dynamicbusiness.service.action.execution;

import cn.cheers.x.framework.mybatis.core.mapper.BaseMapperX;
import cn.cheers.x.framework.mybatis.core.query.LambdaQueryWrapperX;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 动作执行记录的数据访问。
 */
@Mapper
public interface ActionExecutionMapper extends BaseMapperX<ActionExecutionDO> {

    default ActionExecutionDO selectByIdempotencyKey(String key) {
        return selectOne(new LambdaQueryWrapperX<ActionExecutionDO>()
                .eq(ActionExecutionDO::getIdempotencyKey, key)
                .eq(ActionExecutionDO::getDeleted, false));
    }

    default List<ActionExecutionDO> selectPending(int limit) {
        return selectList(new LambdaQueryWrapperX<ActionExecutionDO>()
                .eq(ActionExecutionDO::getStatus, ActionExecutionStatus.PENDING)
                .le(ActionExecutionDO::getAvailableAt, LocalDateTime.now())
                .eq(ActionExecutionDO::getDeleted, false)
                .orderByAsc(ActionExecutionDO::getId)
                .last("LIMIT " + Math.max(1, limit)));
    }

    @Update("""
            UPDATE dynamic_action_execution
            SET status = 'RUNNING',
                attempts = attempts + 1,
                started_at = CURRENT_TIMESTAMP,
                update_time = CURRENT_TIMESTAMP
            WHERE id = #{id}
              AND status = 'PENDING'
              AND deleted = FALSE
            """)
    int claim(@Param("id") Long id);
}
