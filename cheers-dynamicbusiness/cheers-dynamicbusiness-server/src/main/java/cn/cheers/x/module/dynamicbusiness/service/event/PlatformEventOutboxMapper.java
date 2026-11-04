package cn.cheers.x.module.dynamicbusiness.service.event;

import cn.cheers.x.framework.mybatis.core.mapper.BaseMapperX;
import cn.cheers.x.framework.mybatis.core.query.LambdaQueryWrapperX;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 平台事件 outbox 数据访问。
 */
@Mapper
public interface PlatformEventOutboxMapper extends BaseMapperX<PlatformEventOutboxDO> {

    default List<PlatformEventOutboxDO> selectRecentAlarmEvents(String sourceType, long sourceId, int limit) {
        return selectList(new LambdaQueryWrapperX<PlatformEventOutboxDO>()
                .eq(PlatformEventOutboxDO::getSourceEntityTypeCode, sourceType)
                .eq(PlatformEventOutboxDO::getSourceEntityId, sourceId)
                .eq(PlatformEventOutboxDO::getEventKind, PlatformEventKinds.ALARM_LIFECYCLE)
                .eq(PlatformEventOutboxDO::getDeleted, false)
                .orderByDesc(PlatformEventOutboxDO::getId)
                .last("LIMIT " + Math.max(1, limit)));
    }

    default List<PlatformEventOutboxDO> selectPending(int limit) {
        return selectList(new LambdaQueryWrapperX<PlatformEventOutboxDO>()
                .eq(PlatformEventOutboxDO::getStatus, PlatformEventOutboxStatus.PENDING)
                .le(PlatformEventOutboxDO::getAvailableAt, LocalDateTime.now())
                .eq(PlatformEventOutboxDO::getDeleted, false)
                .orderByAsc(PlatformEventOutboxDO::getId)
                .last("LIMIT " + Math.max(1, limit)));
    }

    @Update("""
            UPDATE platform_event_outbox
            SET status = 'PROCESSING',
                attempts = attempts + 1,
                update_time = CURRENT_TIMESTAMP
            WHERE id = #{id}
              AND status = 'PENDING'
              AND deleted = FALSE
            """)
    int claim(@Param("id") Long id);

    /**
     * 投递中途进程没写完的行重新排队。主路径提交后立刻发。这里只收卡住的。
     */
    @Update("""
            UPDATE platform_event_outbox
            SET status = 'PENDING',
                last_error = '投递未完成，重新排队',
                available_at = CURRENT_TIMESTAMP,
                update_time = CURRENT_TIMESTAMP
            WHERE status = 'PROCESSING'
              AND deleted = FALSE
              AND update_time < #{staleBefore}
            """)
    int reclaimStale(@Param("staleBefore") LocalDateTime staleBefore);
}
