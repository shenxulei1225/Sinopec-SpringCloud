package cn.cheers.x.inspection.task.service.execution.live;

import cn.cheers.x.framework.common.exception.util.ServiceExceptionUtil;
import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.module.dynamicbusiness.api.execution.TaskExecutionSessionApi;
import cn.cheers.x.module.dynamicbusiness.api.execution.dto.TaskExecutionDayAccountPageDTO;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/**
 * 场站实时监控 · 巡检图层侧栏：当前站场、所选日期的巡检账。
 *
 * <p>管什么：缺站或日期不对就停。名单按账上已写下的计划时刻收这一天。</p>
 * <p>不管什么：不写执行账，不读步骤树。点中一条之后另读那一条的步骤。</p>
 * <p>禁止：缺站时扫全租户；走数据目录的通用实体列表。</p>
 */
@Service
public class PatrolLayerDayListQueryService {

    static final int MAX_PAGE_SIZE = 100;

    private final TaskExecutionSessionApi taskExecutionSessionApi;

    public PatrolLayerDayListQueryService(TaskExecutionSessionApi taskExecutionSessionApi) {
        this.taskExecutionSessionApi = taskExecutionSessionApi;
    }

    /**
     * 侧栏这一页。日期是页面上选中的那一天，格式 yyyy-MM-dd。
     */
    public TaskExecutionDayAccountPageDTO page(Long facilityId, String day, Integer pageNo, Integer pageSize) {
        if (facilityId == null || facilityId <= 0) {
            throw ServiceExceptionUtil.invalidParamException("站场级数据须指定所属场站，请先选择要管理的场站");
        }
        if (!isCalendarDay(day)) {
            throw ServiceExceptionUtil.invalidParamException("日期须为所选的那一天");
        }
        int resolvedPageNo = pageNo == null ? 1 : pageNo;
        int resolvedPageSize = pageSize == null ? MAX_PAGE_SIZE : pageSize;
        if (resolvedPageNo < 1 || resolvedPageSize < 1 || resolvedPageSize > MAX_PAGE_SIZE) {
            throw ServiceExceptionUtil.invalidParamException("当天巡检账分页超出范围");
        }
        CommonResult<TaskExecutionDayAccountPageDTO> listed = taskExecutionSessionApi.listPatrolLayerDay(
                facilityId, day, resolvedPageNo, resolvedPageSize);
        if (listed == null || !listed.isSuccess() || listed.getData() == null || listed.getData().getList() == null) {
            throw ServiceExceptionUtil.invalidParamException(
                    listed != null && StringUtils.hasText(listed.getMsg()) ? listed.getMsg() : "读不到这一天的巡检账");
        }
        return listed.getData();
    }

    /** 侧栏选中的那一天。不是日历上的日期就停，不把请求交给执行账。 */
    private static boolean isCalendarDay(String day) {
        if (day == null || !day.matches("\\d{4}-\\d{2}-\\d{2}")) {
            return false;
        }
        try {
            LocalDate.parse(day);
            return true;
        } catch (DateTimeParseException ex) {
            return false;
        }
    }
}
