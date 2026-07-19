package cn.cheers.x.maintenance.service.calendar;

import cn.cheers.x.framework.common.pojo.PageResult;
import cn.cheers.x.framework.common.util.json.JsonUtils;
import cn.cheers.x.framework.common.util.object.BeanUtils;
import cn.cheers.x.maintenance.api.ScheduleRunApi;
import cn.cheers.x.maintenance.controller.admin.vo.calendar.*;
import cn.cheers.x.maintenance.dal.dataobject.CalendarEntryDO;
import cn.cheers.x.maintenance.dal.dataobject.HandbookDO;
import cn.cheers.x.maintenance.dal.mysql.CalendarEntryMapper;
import cn.cheers.x.maintenance.dal.mysql.HandbookMapper;
import cn.cheers.x.maintenance.enums.HandbookStatusEnum;
import cn.cheers.x.maintenance.framework.config.MaintenanceCalendarProperties;
import cn.cheers.x.module.platform.contract.dto.schedule.ScheduleRunRequest;
import cn.cheers.x.module.platform.contract.dto.schedule.ScheduleRunResponse;
import cn.cheers.x.module.platform.contract.dto.schedule.SchedulingSpecDTO;
import cn.cheers.x.module.platform.contract.dto.work.WorkItemDTO;
import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static cn.cheers.x.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.cheers.x.maintenance.enums.ErrorCodeConstants.*;

@Service
@Validated
public class CalendarServiceImpl implements CalendarService {

    public static final String STATUS_PLANNED = "PLANNED";
    public static final String STATUS_TRIGGERED = "TRIGGERED";
    public static final String STATUS_FAILED = "FAILED";

    @Resource private CalendarEntryMapper calendarEntryMapper;
    @Resource private HandbookMapper handbookMapper;
    @Resource private ScheduleRunApi scheduleRunApi;
    @Resource private MaintenanceCalendarProperties calendarProperties;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int expand(CalendarExpandReqVO reqVO) {
        HandbookDO handbook = handbookMapper.selectById(reqVO.getHandbookId());
        if (handbook == null || !HandbookStatusEnum.PUBLISHED.getStatus().equals(handbook.getStatus())) {
            throw exception(CALENDAR_HANDBOOK_NOT_PUBLISHED);
        }
        List<LocalDate> dates = expandDates(reqVO.getFrom(), reqVO.getTo(), handbook.getFrequencyCode());
        int created = 0;
        for (Long assetId : reqVO.getAssetIds()) {
            for (LocalDate date : dates) {
                CalendarEntryDO existing = calendarEntryMapper.selectByUnique(handbook.getId(), assetId, date);
                if (existing != null) {
                    continue;
                }
                CalendarEntryDO row = new CalendarEntryDO();
                row.setHandbookId(handbook.getId());
                row.setAssetId(assetId);
                row.setScope(handbook.getScope());
                row.setPlannedDate(date);
                row.setStatus(STATUS_PLANNED);
                calendarEntryMapper.insert(row);
                created++;
            }
        }
        return created;
    }

    @Override
    public PageResult<CalendarEntryRespVO> page(CalendarPageReqVO reqVO) {
        PageResult<CalendarEntryDO> page = calendarEntryMapper.selectPage(reqVO);
        return new PageResult<>(page.getList().stream().map(this::convert).toList(), page.getTotal());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void trigger(Long entryId) {
        CalendarEntryDO entry = calendarEntryMapper.selectById(entryId);
        if (entry == null) {
            throw exception(CALENDAR_ENTRY_NOT_EXISTS);
        }
        if (!STATUS_PLANNED.equals(entry.getStatus()) && !STATUS_FAILED.equals(entry.getStatus())) {
            throw exception(CALENDAR_STATUS_INVALID);
        }
        if (!StringUtils.hasText(calendarProperties.getSchedulingMode())) {
            throw exception(CALENDAR_SCHEDULING_SPEC_REQUIRED);
        }
        HandbookDO handbook = handbookMapper.selectById(entry.getHandbookId());
        if (handbook == null) {
            throw exception(CALENDAR_HANDBOOK_NOT_PUBLISHED);
        }
        try {
            ScheduleRunResponse response = scheduleRunApi.run(ScheduleRunRequest.builder()
                    .dispatchWorkOrders(true)
                    .scope(entry.getScope())
                    .assetId(entry.getAssetId())
                    .assetTypeCode(handbook.getAssetTypeCode())
                    .frequencyCode(handbook.getFrequencyCode())
                    .orchestrationRef(calendarProperties.getOrchestrationRef())
                    .schedulingSpec(SchedulingSpecDTO.builder()
                            .mode(calendarProperties.getSchedulingMode())
                            .conflictStrategy(calendarProperties.getConflictStrategy())
                            .build())
                    .workItems(List.of(WorkItemDTO.builder()
                            .workId(String.valueOf(entry.getAssetId()))
                            .build()))
                    .build()).getCheckedData();
            CalendarEntryDO update = new CalendarEntryDO();
            update.setId(entryId);
            update.setStatus(STATUS_TRIGGERED);
            update.setRuntimeJobId(response.getRuntimeJobId());
            update.setWorkOrderIds(JsonUtils.toJsonString(
                    response.getWorkOrderIds() == null ? Collections.emptyList() : response.getWorkOrderIds()));
            update.setLastError(null);
            calendarEntryMapper.updateById(update);
        } catch (Exception ex) {
            CalendarEntryDO update = new CalendarEntryDO();
            update.setId(entryId);
            update.setStatus(STATUS_FAILED);
            String msg = ex.getMessage();
            update.setLastError(msg == null ? "trigger failed" : msg.substring(0, Math.min(msg.length(), 500)));
            calendarEntryMapper.updateById(update);
            throw ex;
        }
    }

    static List<LocalDate> expandDates(LocalDate from, LocalDate to, String frequencyCode) {
        if (!StringUtils.hasText(frequencyCode)) {
            throw exception(CALENDAR_FREQUENCY_UNSUPPORTED);
        }
        List<LocalDate> dates = new ArrayList<>();
        switch (frequencyCode.toUpperCase()) {
            case "D" -> {
                for (LocalDate d = from; !d.isAfter(to); d = d.plusDays(1)) {
                    dates.add(d);
                }
            }
            case "W" -> {
                LocalDate d = from;
                while (!d.isAfter(to)) {
                    dates.add(d);
                    d = d.plusWeeks(1);
                }
            }
            case "M" -> {
                LocalDate d = from.withDayOfMonth(1);
                if (d.isBefore(from)) {
                    d = d.plusMonths(1);
                }
                while (!d.isAfter(to)) {
                    dates.add(d);
                    d = d.plusMonths(1);
                }
            }
            default -> throw exception(CALENDAR_FREQUENCY_UNSUPPORTED);
        }
        return dates;
    }

    private CalendarEntryRespVO convert(CalendarEntryDO row) {
        CalendarEntryRespVO vo = BeanUtils.toBean(row, CalendarEntryRespVO.class);
        if (row.getWorkOrderIds() != null && !row.getWorkOrderIds().isBlank()) {
            vo.setWorkOrderIds(JsonUtils.parseObject(row.getWorkOrderIds(), new TypeReference<List<Long>>() {}));
        }
        return vo;
    }
}
