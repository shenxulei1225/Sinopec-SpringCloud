package cn.iocoder.yudao.module.emergency.dal.mysql.timeline;

import cn.iocoder.yudao.module.emergency.dal.dataobject.timeline.TimelineItemRaw;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface EmergencyTimelineMapper {

    List<TimelineItemRaw> selectTimelinePage(@Param("responseId") Long responseId,
                                             @Param("eventId") Long eventId,
                                             @Param("limit") Integer limit,
                                             @Param("offset") Integer offset,
                                             @Param("orderBy") String orderBy);

    Long countTimeline(@Param("responseId") Long responseId,
                       @Param("eventId") Long eventId);
}

















