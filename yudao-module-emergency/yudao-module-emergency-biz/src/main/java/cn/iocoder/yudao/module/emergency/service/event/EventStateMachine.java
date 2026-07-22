package cn.iocoder.yudao.module.emergency.service.event;

import cn.cheers.x.framework.common.exception.util.ServiceExceptionUtil;
import cn.iocoder.yudao.module.emergency.dal.dataobject.event.EmergencyEventDO;
import cn.iocoder.yudao.module.emergency.dal.dataobject.task.EmergencyTaskDO;
import cn.iocoder.yudao.module.emergency.dal.mysql.task.EmergencyTaskMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import cn.iocoder.yudao.module.emergency.enums.EventStatus;
import cn.iocoder.yudao.module.emergency.enums.error.ErrorCodeConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;
import java.util.*;

/**
 * 事件状态机
 * 
 * 管理事件状态的转换规则和校验
 */
@Component
@Slf4j
public class EventStateMachine {

    @Resource
    private EmergencyTaskMapper taskMapper;

    /**
     * 合法的状态转换映射
     * key: 当前状态, value: 允许转换的目标状态列表
     */
    private static final Map<String, Set<String>> VALID_TRANSITIONS = new HashMap<>();

    static {
        // 待响应 → 预警、响应中、已取消
        VALID_TRANSITIONS.put(EventStatus.PENDING.getCode(), Set.of(
                EventStatus.WARNING.getCode(),
                EventStatus.RESPONDING.getCode(),
                EventStatus.CANCELLED.getCode()
        ));

        // 预警 → 响应中、已取消
        VALID_TRANSITIONS.put(EventStatus.WARNING.getCode(), Set.of(
                EventStatus.RESPONDING.getCode(),
                EventStatus.CLOSED.getCode(),
                EventStatus.CANCELLED.getCode()
        ));

        // 响应中 → 处理中、已关闭
        VALID_TRANSITIONS.put(EventStatus.RESPONDING.getCode(), Set.of(
                EventStatus.PROCESSING.getCode(),
                EventStatus.CLOSED.getCode()
        ));

        // 处理中 → 监控中、已关闭
        VALID_TRANSITIONS.put(EventStatus.PROCESSING.getCode(), Set.of(
                EventStatus.MONITORING.getCode(),
                EventStatus.CLOSED.getCode()
        ));

        // 监控中 → 已关闭
        VALID_TRANSITIONS.put(EventStatus.MONITORING.getCode(), Set.of(
                EventStatus.CLOSED.getCode()
        ));

        // 已关闭、已取消是终态，不允许转换
        VALID_TRANSITIONS.put(EventStatus.CLOSED.getCode(), Collections.emptySet());
        VALID_TRANSITIONS.put(EventStatus.CANCELLED.getCode(), Collections.emptySet());
    }

    /**
     * 校验状态转换是否合法
     *
     * @param currentStatus 当前状态
     * @param targetStatus 目标状态
     * @throws ServiceExceptionUtil 如果转换不合法
     */
    public void validateTransition(String currentStatus, String targetStatus) {
        if (currentStatus == null) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.EVENT_STATUS_INVALID);
        }

        if (targetStatus == null) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.EVENT_STATUS_INVALID);
        }

        // 如果状态相同，允许（幂等性）
        if (currentStatus.equals(targetStatus)) {
            return;
        }

        // 检查是否允许转换
        Set<String> allowedTargets = VALID_TRANSITIONS.get(currentStatus);
        if (allowedTargets == null || !allowedTargets.contains(targetStatus)) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.EVENT_STATUS_TRANSITION_INVALID,
                    "不允许从状态 " + currentStatus + " 转换到 " + targetStatus);
        }
    }

    /**
     * 校验状态转换的业务规则
     *
     * @param event 事件
     * @param targetStatus 目标状态
     * @throws ServiceExceptionUtil 如果业务规则校验失败
     */
    public void validateBusinessRules(EmergencyEventDO event, String targetStatus) {
        String currentStatus = event.getStatus();

        // BR-001: 事件进入"响应中"状态时，必须已分配至少一名负责人
        if (EventStatus.RESPONDING.getCode().equals(targetStatus)) {
            if (event.getCommandOrg() == null || event.getCommandOrg().isEmpty()) {
                throw ServiceExceptionUtil.exception(ErrorCodeConstants.EVENT_RESPONDING_REQUIRES_LEADER);
            }
        }

        // BR-002: 事件关闭前，所有"关键"级别的任务必须已完成
        if (EventStatus.CLOSED.getCode().equals(targetStatus)) {
            validateAllCriticalTasksCompleted(event.getId());
        }

        // BR-007: 已关闭的事件和已取消的事件处于只读状态
        if (EventStatus.CLOSED.getCode().equals(currentStatus) || 
            EventStatus.CANCELLED.getCode().equals(currentStatus)) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.EVENT_READ_ONLY_STATUS);
        }
    }

    /**
     * 校验所有关键任务是否已完成
     *
     * @param eventId 事件ID
     * @throws ServiceExceptionUtil 如果有关键任务未完成
     */
    private void validateAllCriticalTasksCompleted(Long eventId) {
        // 查询事件的所有任务
        List<EmergencyTaskDO> tasks = taskMapper.selectList(
                new LambdaQueryWrapper<EmergencyTaskDO>()
                        .eq(EmergencyTaskDO::getEventId, eventId)
                        .ne(EmergencyTaskDO::getStatus, "terminated")
        );

        // 检查是否有未完成的关键任务
        // 过滤关键任务（isKey = true）
        for (EmergencyTaskDO task : tasks) {
            // 如果是关键任务且未完成，则不允许关闭事件
            if (Boolean.TRUE.equals(task.getIsKey()) && !"completed".equals(task.getStatus())) {
                throw ServiceExceptionUtil.exception(ErrorCodeConstants.EVENT_CLOSE_REQUIRES_ALL_TASKS_COMPLETED,
                        "关键任务 " + task.getTaskCode() + " 未完成，无法关闭事件");
            }
        }
    }

    /**
     * 检查状态转换是否需要特殊处理
     *
     * @param currentStatus 当前状态
     * @param targetStatus 目标状态
     * @return 是否需要特殊处理
     */
    public boolean requiresSpecialHandling(String currentStatus, String targetStatus) {
        // 从待响应或预警转换到响应中，需要启动响应流程
        if ((EventStatus.PENDING.getCode().equals(currentStatus) || 
             EventStatus.WARNING.getCode().equals(currentStatus)) &&
            EventStatus.RESPONDING.getCode().equals(targetStatus)) {
            return true;
        }

        // 从响应中转换到处理中，需要开始处理措施
        if (EventStatus.RESPONDING.getCode().equals(currentStatus) &&
            EventStatus.PROCESSING.getCode().equals(targetStatus)) {
            return true;
        }

        return false;
    }

    /**
     * 获取状态转换的必须条件
     *
     * @param currentStatus 当前状态
     * @param targetStatus 目标状态
     * @return 必须条件列表
     */
    public List<String> getRequiredConditions(String currentStatus, String targetStatus) {
        List<String> conditions = new ArrayList<>();

        if (EventStatus.RESPONDING.getCode().equals(targetStatus)) {
            conditions.add("必须分配至少一名负责人");
        }

        if (EventStatus.CLOSED.getCode().equals(targetStatus)) {
            conditions.add("所有关键任务必须已完成");
        }

        if (EventStatus.PROCESSING.getCode().equals(targetStatus)) {
            conditions.add("建议已分配必要资源和创建初始任务");
        }

        if (EventStatus.MONITORING.getCode().equals(targetStatus)) {
            conditions.add("所有关键任务必须已完成");
        }

        return conditions;
    }
}

