package cn.cheers.x.maintenance.service.calendar;

import cn.cheers.x.framework.common.pojo.PageResult;
import cn.cheers.x.maintenance.controller.admin.vo.calendar.*;

public interface CalendarService {
    int expand(CalendarExpandReqVO reqVO);
    PageResult<CalendarEntryRespVO> page(CalendarPageReqVO reqVO);
    void trigger(Long entryId);
}
