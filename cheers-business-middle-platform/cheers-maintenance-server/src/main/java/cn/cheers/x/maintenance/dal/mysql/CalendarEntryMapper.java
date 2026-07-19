package cn.cheers.x.maintenance.dal.mysql;

import cn.cheers.x.framework.common.pojo.PageResult;
import cn.cheers.x.framework.mybatis.core.mapper.BaseMapperX;
import cn.cheers.x.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.cheers.x.maintenance.controller.admin.vo.calendar.CalendarPageReqVO;
import cn.cheers.x.maintenance.dal.dataobject.CalendarEntryDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CalendarEntryMapper extends BaseMapperX<CalendarEntryDO> {

    default CalendarEntryDO selectByUnique(Long handbookId, Long assetId, java.time.LocalDate date) {
        return selectOne(new LambdaQueryWrapperX<CalendarEntryDO>()
                .eq(CalendarEntryDO::getHandbookId, handbookId)
                .eq(CalendarEntryDO::getAssetId, assetId)
                .eq(CalendarEntryDO::getPlannedDate, date));
    }

    default PageResult<CalendarEntryDO> selectPage(CalendarPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<CalendarEntryDO>()
                .eqIfPresent(CalendarEntryDO::getHandbookId, reqVO.getHandbookId())
                .eqIfPresent(CalendarEntryDO::getAssetId, reqVO.getAssetId())
                .eqIfPresent(CalendarEntryDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(CalendarEntryDO::getPlannedDate, reqVO.getFrom(), reqVO.getTo())
                .orderByAsc(CalendarEntryDO::getPlannedDate));
    }
}
