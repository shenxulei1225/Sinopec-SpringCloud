package cn.cheers.x.alarm.service.statistics;

import cn.cheers.x.alarm.controller.admin.vo.statistics.*;
import cn.cheers.x.alarm.dal.dataobject.AlarmDO;
import cn.cheers.x.alarm.dal.mysql.AlarmMapper;
import cn.cheers.x.alarm.dal.mysql.LinkageExecutionMapper;
import cn.cheers.x.alarm.enums.AlarmLevelEnum;
import cn.cheers.x.alarm.enums.AlarmStatusEnum;
import cn.cheers.x.alarm.enums.LinkageExecutionStatusEnum;
import cn.cheers.x.alarm.framework.cache.AlarmCacheService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import jakarta.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 告警统计 Service 实现类
 * 
 * <p>性能优化：
 * <ul>
 *   <li>实时统计数据使用 Redis 缓存，缓存时间 30 秒</li>
 *   <li>减少数据库查询次数，提升响应速度</li>
 * </ul>
 * </p>
 *
 * @author 告警管理模块
 */
@Service
@Validated
@Slf4j
public class AlarmStatisticsServiceImpl implements AlarmStatisticsService {

    @Resource
    private AlarmMapper alarmMapper;

    @Resource
    private LinkageExecutionMapper linkageExecutionMapper;

    @Resource
    private AlarmCacheService alarmCacheService;

    @Override
    public AlarmStatisticsRespVO getRealTimeStatistics() {
        // 1. 尝试从缓存获取
        AlarmStatisticsRespVO cached = alarmCacheService.getRealTimeStatisticsCache();
        if (cached != null) {
            log.debug("[getRealTimeStatistics] 从缓存获取实时统计数据");
            return cached;
        }

        // 2. 缓存未命中，从数据库查询
        AlarmStatisticsRespVO result = new AlarmStatisticsRespVO();
        
        // 今日时间范围
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        LocalDateTime todayEnd = LocalDate.now().atTime(LocalTime.MAX);
        
        // 1. 当前活跃告警数（未关闭）
        result.setActiveCount(alarmMapper.countActiveAlarms().intValue());
        
        // 2. 今日新增告警数
        result.setTodayNewCount(alarmMapper.countByCreateTimeBetween(todayStart, todayEnd).intValue());
        
        // 3. 今日处理告警数
        result.setTodayHandledCount(alarmMapper.countHandledByTimeBetween(todayStart, todayEnd).intValue());
        
        // 4. 今日关闭告警数
        result.setTodayClosedCount(alarmMapper.countClosedByTimeBetween(todayStart, todayEnd).intValue());
        
        // 5. 各状态告警数
        result.setPendingCount(alarmMapper.selectCountByStatus(AlarmStatusEnum.PENDING.getStatus()).intValue());
        result.setAcknowledgedCount(alarmMapper.selectCountByStatus(AlarmStatusEnum.ACKNOWLEDGED.getStatus()).intValue());
        result.setHandlingCount(alarmMapper.selectCountByStatus(AlarmStatusEnum.HANDLING.getStatus()).intValue());
        
        // 6. 各级别告警数（实时告警）
        result.setEmergencyCount(alarmMapper.countRealTimeAlarmByLevel(AlarmLevelEnum.EMERGENCY.getLevel()).intValue());
        result.setCriticalCount(alarmMapper.countRealTimeAlarmByLevel(AlarmLevelEnum.CRITICAL.getLevel()).intValue());
        result.setWarningCount(alarmMapper.countRealTimeAlarmByLevel(AlarmLevelEnum.WARNING.getLevel()).intValue());
        result.setInfoCount(alarmMapper.countRealTimeAlarmByLevel(AlarmLevelEnum.INFO.getLevel()).intValue());
        
        // 7. 计算平均响应时间和处理时间
        calculateAverageResponseAndHandleTime(result, todayStart, todayEnd);

        // 3. 设置缓存
        alarmCacheService.setRealTimeStatisticsCache(result);
        log.debug("[getRealTimeStatistics] 从数据库查询并缓存实时统计数据");
        
        return result;
    }

    @Override
    public List<AlarmTrendRespVO> getAlarmTrend(AlarmTrendReqVO reqVO) {
        List<AlarmTrendRespVO> result = new ArrayList<>();
        
        LocalDate currentDate = reqVO.getStartDate();
        LocalDate endDate = reqVO.getEndDate();
        
        while (!currentDate.isAfter(endDate)) {
            LocalDateTime dayStart = currentDate.atStartOfDay();
            LocalDateTime dayEnd = currentDate.atTime(LocalTime.MAX);
            
            AlarmTrendRespVO trendVO = new AlarmTrendRespVO();
            trendVO.setDate(currentDate);
            
            // 查询当天的告警数据
            List<AlarmDO> dayAlarms = selectAlarmsByDateAndConditions(dayStart, dayEnd, reqVO);
            
            // 统计新增告警数
            trendVO.setNewCount(dayAlarms.size());
            
            // 统计处理告警数
            long handledCount = dayAlarms.stream()
                    .filter(alarm -> alarm.getHandleTime() != null)
                    .count();
            trendVO.setHandledCount((int) handledCount);
            
            // 统计关闭告警数
            long closedCount = dayAlarms.stream()
                    .filter(alarm -> AlarmStatusEnum.CLOSED.getStatus().equals(alarm.getAlarmStatus()))
                    .count();
            trendVO.setClosedCount((int) closedCount);
            
            // 按级别统计
            Map<String, Long> levelCounts = dayAlarms.stream()
                    .collect(Collectors.groupingBy(AlarmDO::getAlarmLevel, Collectors.counting()));
            
            trendVO.setEmergencyCount(levelCounts.getOrDefault(AlarmLevelEnum.EMERGENCY.getLevel(), 0L).intValue());
            trendVO.setCriticalCount(levelCounts.getOrDefault(AlarmLevelEnum.CRITICAL.getLevel(), 0L).intValue());
            trendVO.setWarningCount(levelCounts.getOrDefault(AlarmLevelEnum.WARNING.getLevel(), 0L).intValue());
            trendVO.setInfoCount(levelCounts.getOrDefault(AlarmLevelEnum.INFO.getLevel(), 0L).intValue());
            
            result.add(trendVO);
            currentDate = currentDate.plusDays(1);
        }
        
        return result;
    }


    @Override
    public List<AlarmDistributionRespVO> getAlarmDistribution(AlarmDistributionReqVO reqVO) {
        List<AlarmDistributionRespVO> result = new ArrayList<>();
        
        // 获取时间范围内的告警数据
        LocalDateTime startTime = reqVO.getStartDate() != null 
                ? reqVO.getStartDate().atStartOfDay() 
                : LocalDate.now().minusDays(30).atStartOfDay();
        LocalDateTime endTime = reqVO.getEndDate() != null 
                ? reqVO.getEndDate().atTime(LocalTime.MAX) 
                : LocalDateTime.now();
        
        List<AlarmDO> alarms = selectAlarmsByTimeRangeAndCategory(startTime, endTime, reqVO.getAlarmCategoryId());
        
        if (alarms.isEmpty()) {
            return result;
        }
        
        int totalCount = alarms.size();
        
        // 根据分布类型进行统计
        switch (reqVO.getDistributionType().toUpperCase()) {
            case "TYPE":
                result = calculateDistributionByType(alarms, totalCount);
                break;
            case "LEVEL":
                result = calculateDistributionByLevel(alarms, totalCount);
                break;
            case "LOCATION":
                result = calculateDistributionByLocation(alarms, totalCount);
                break;
            case "STATUS":
                result = calculateDistributionByStatus(alarms, totalCount);
                break;
            default:
                log.warn("未知的分布类型: {}", reqVO.getDistributionType());
        }
        
        return result;
    }

    @Override
    public AlarmEfficiencyRespVO getProcessingEfficiency(AlarmEfficiencyReqVO reqVO) {
        AlarmEfficiencyRespVO result = new AlarmEfficiencyRespVO();
        
        LocalDateTime startTime = reqVO.getStartDate().atStartOfDay();
        LocalDateTime endTime = reqVO.getEndDate().atTime(LocalTime.MAX);
        
        // 查询时间范围内的告警
        List<AlarmDO> alarms = selectAlarmsByTimeRangeAndConditions(startTime, endTime, 
                reqVO.getAlarmCategoryId(), reqVO.getAlarmLevel());
        
        if (alarms.isEmpty()) {
            result.setAvgResponseTimeMinutes(0.0);
            result.setAvgHandleTimeMinutes(0.0);
            result.setAvgCloseTimeMinutes(0.0);
            result.setHandleRate(0.0);
            result.setCloseRate(0.0);
            result.setTimeoutPendingCount(0);
            result.setTimeoutHandlingCount(0);
            result.setManualInterventionCount(0);
            return result;
        }
        
        int totalCount = alarms.size();
        
        // 1. 计算平均响应时间（从创建到确认）
        result.setAvgResponseTimeMinutes(calculateAvgResponseTime(alarms));
        
        // 2. 计算平均处理时间（从确认到处理）
        result.setAvgHandleTimeMinutes(calculateAvgHandleTime(alarms));
        
        // 3. 计算平均关闭时间（从创建到关闭）
        result.setAvgCloseTimeMinutes(calculateAvgCloseTime(alarms));
        
        // 4. 计算处理率
        long handledCount = alarms.stream()
                .filter(alarm -> alarm.getHandleTime() != null)
                .count();
        result.setHandleRate(totalCount > 0 ? (handledCount * 100.0 / totalCount) : 0.0);
        
        // 5. 计算关闭率
        long closedCount = alarms.stream()
                .filter(alarm -> AlarmStatusEnum.CLOSED.getStatus().equals(alarm.getAlarmStatus()))
                .count();
        result.setCloseRate(totalCount > 0 ? (closedCount * 100.0 / totalCount) : 0.0);
        
        // 6. 统计超时未确认告警数
        result.setTimeoutPendingCount(countTimeoutPendingAlarms(alarms));
        
        // 7. 统计超时未处理告警数
        result.setTimeoutHandlingCount(countTimeoutHandlingAlarms(alarms));
        
        // 8. 统计需人工介入联动数
        result.setManualInterventionCount(
                linkageExecutionMapper.selectListNeedManualIntervention().size());
        
        return result;
    }

    // ========== 私有辅助方法 ==========

    /**
     * 计算平均响应时间和处理时间
     */
    private void calculateAverageResponseAndHandleTime(AlarmStatisticsRespVO result, 
                                                        LocalDateTime startTime, 
                                                        LocalDateTime endTime) {
        List<AlarmDO> alarms = alarmMapper.selectListByTimeRange(startTime, endTime);
        
        if (alarms.isEmpty()) {
            result.setAvgResponseTimeMinutes(0.0);
            result.setAvgHandleTimeMinutes(0.0);
            return;
        }
        
        result.setAvgResponseTimeMinutes(calculateAvgResponseTime(alarms));
        result.setAvgHandleTimeMinutes(calculateAvgHandleTime(alarms));
    }

    /**
     * 计算平均响应时间（从创建到确认）
     */
    private Double calculateAvgResponseTime(List<AlarmDO> alarms) {
        List<Long> responseTimes = alarms.stream()
                .filter(alarm -> alarm.getAcknowledgeTime() != null)
                .map(alarm -> ChronoUnit.MINUTES.between(alarm.getCreateTime(), alarm.getAcknowledgeTime()))
                .collect(Collectors.toList());
        
        if (responseTimes.isEmpty()) {
            return 0.0;
        }
        
        return responseTimes.stream()
                .mapToLong(Long::longValue)
                .average()
                .orElse(0.0);
    }

    /**
     * 计算平均处理时间（从确认到处理）
     */
    private Double calculateAvgHandleTime(List<AlarmDO> alarms) {
        List<Long> handleTimes = alarms.stream()
                .filter(alarm -> alarm.getAcknowledgeTime() != null && alarm.getHandleTime() != null)
                .map(alarm -> ChronoUnit.MINUTES.between(alarm.getAcknowledgeTime(), alarm.getHandleTime()))
                .collect(Collectors.toList());
        
        if (handleTimes.isEmpty()) {
            return 0.0;
        }
        
        return handleTimes.stream()
                .mapToLong(Long::longValue)
                .average()
                .orElse(0.0);
    }

    /**
     * 计算平均关闭时间（从创建到关闭）
     */
    private Double calculateAvgCloseTime(List<AlarmDO> alarms) {
        List<Long> closeTimes = alarms.stream()
                .filter(alarm -> alarm.getCloseTime() != null)
                .map(alarm -> ChronoUnit.MINUTES.between(alarm.getCreateTime(), alarm.getCloseTime()))
                .collect(Collectors.toList());
        
        if (closeTimes.isEmpty()) {
            return 0.0;
        }
        
        return closeTimes.stream()
                .mapToLong(Long::longValue)
                .average()
                .orElse(0.0);
    }


    /**
     * 根据日期和条件查询告警
     */
    private List<AlarmDO> selectAlarmsByDateAndConditions(LocalDateTime dayStart, 
                                                           LocalDateTime dayEnd, 
                                                           AlarmTrendReqVO reqVO) {
        LambdaQueryWrapper<AlarmDO> wrapper = new LambdaQueryWrapper<AlarmDO>()
                .ge(AlarmDO::getCreateTime, dayStart)
                .le(AlarmDO::getCreateTime, dayEnd);
        
        if (reqVO.getAlarmTypeId() != null) {
            wrapper.eq(AlarmDO::getAlarmTypeId, reqVO.getAlarmTypeId());
        }
        if (reqVO.getAlarmCategoryId() != null) {
            wrapper.eq(AlarmDO::getAlarmCategoryId, reqVO.getAlarmCategoryId());
        }
        if (reqVO.getAlarmLevel() != null) {
            wrapper.eq(AlarmDO::getAlarmLevel, reqVO.getAlarmLevel());
        }
        
        return alarmMapper.selectList(wrapper);
    }

    /**
     * 根据时间范围和分类查询告警
     */
    private List<AlarmDO> selectAlarmsByTimeRangeAndCategory(LocalDateTime startTime, 
                                                              LocalDateTime endTime, 
                                                              Long alarmCategoryId) {
        LambdaQueryWrapper<AlarmDO> wrapper = new LambdaQueryWrapper<AlarmDO>()
                .ge(AlarmDO::getCreateTime, startTime)
                .le(AlarmDO::getCreateTime, endTime);
        
        if (alarmCategoryId != null) {
            wrapper.eq(AlarmDO::getAlarmCategoryId, alarmCategoryId);
        }
        
        return alarmMapper.selectList(wrapper);
    }

    /**
     * 根据时间范围和条件查询告警
     */
    private List<AlarmDO> selectAlarmsByTimeRangeAndConditions(LocalDateTime startTime, 
                                                                LocalDateTime endTime, 
                                                                Long alarmCategoryId, 
                                                                String alarmLevel) {
        LambdaQueryWrapper<AlarmDO> wrapper = new LambdaQueryWrapper<AlarmDO>()
                .ge(AlarmDO::getCreateTime, startTime)
                .le(AlarmDO::getCreateTime, endTime);
        
        if (alarmCategoryId != null) {
            wrapper.eq(AlarmDO::getAlarmCategoryId, alarmCategoryId);
        }
        if (alarmLevel != null) {
            wrapper.eq(AlarmDO::getAlarmLevel, alarmLevel);
        }
        
        return alarmMapper.selectList(wrapper);
    }

    /**
     * 按告警类型计算分布
     */
    private List<AlarmDistributionRespVO> calculateDistributionByType(List<AlarmDO> alarms, int totalCount) {
        Map<String, Long> typeCounts = alarms.stream()
                .filter(alarm -> alarm.getAlarmTypePath() != null)
                .collect(Collectors.groupingBy(
                        alarm -> {
                            // 取告警类型路径的第一级作为分类名称
                            String path = alarm.getAlarmTypePath();
                            int index = path.indexOf(" > ");
                            return index > 0 ? path.substring(0, index) : path;
                        },
                        Collectors.counting()
                ));
        
        return typeCounts.entrySet().stream()
                .map(entry -> {
                    AlarmDistributionRespVO vo = new AlarmDistributionRespVO();
                    vo.setName(entry.getKey());
                    vo.setCount(entry.getValue().intValue());
                    vo.setPercentage(totalCount > 0 ? (entry.getValue() * 100.0 / totalCount) : 0.0);
                    return vo;
                })
                .sorted(Comparator.comparing(AlarmDistributionRespVO::getCount).reversed())
                .collect(Collectors.toList());
    }

    /**
     * 按告警级别计算分布
     */
    private List<AlarmDistributionRespVO> calculateDistributionByLevel(List<AlarmDO> alarms, int totalCount) {
        Map<String, Long> levelCounts = alarms.stream()
                .collect(Collectors.groupingBy(AlarmDO::getAlarmLevel, Collectors.counting()));
        
        // 按级别优先级排序
        List<String> levelOrder = Arrays.asList(
                AlarmLevelEnum.EMERGENCY.getLevel(),
                AlarmLevelEnum.CRITICAL.getLevel(),
                AlarmLevelEnum.WARNING.getLevel(),
                AlarmLevelEnum.INFO.getLevel()
        );
        
        return levelOrder.stream()
                .filter(levelCounts::containsKey)
                .map(level -> {
                    AlarmDistributionRespVO vo = new AlarmDistributionRespVO();
                    vo.setCode(level);
                    vo.setName(getLevelDisplayName(level));
                    vo.setCount(levelCounts.get(level).intValue());
                    vo.setPercentage(totalCount > 0 ? (levelCounts.get(level) * 100.0 / totalCount) : 0.0);
                    return vo;
                })
                .collect(Collectors.toList());
    }

    /**
     * 按位置计算分布
     */
    private List<AlarmDistributionRespVO> calculateDistributionByLocation(List<AlarmDO> alarms, int totalCount) {
        Map<String, Long> locationCounts = alarms.stream()
                .filter(alarm -> alarm.getLocationName() != null)
                .collect(Collectors.groupingBy(
                        alarm -> {
                            // 取位置名称的第一级作为区域
                            String location = alarm.getLocationName();
                            int index = location.indexOf(" > ");
                            return index > 0 ? location.substring(0, index) : location;
                        },
                        Collectors.counting()
                ));
        
        return locationCounts.entrySet().stream()
                .map(entry -> {
                    AlarmDistributionRespVO vo = new AlarmDistributionRespVO();
                    vo.setName(entry.getKey());
                    vo.setCount(entry.getValue().intValue());
                    vo.setPercentage(totalCount > 0 ? (entry.getValue() * 100.0 / totalCount) : 0.0);
                    return vo;
                })
                .sorted(Comparator.comparing(AlarmDistributionRespVO::getCount).reversed())
                .collect(Collectors.toList());
    }

    /**
     * 按状态计算分布
     */
    private List<AlarmDistributionRespVO> calculateDistributionByStatus(List<AlarmDO> alarms, int totalCount) {
        Map<String, Long> statusCounts = alarms.stream()
                .collect(Collectors.groupingBy(AlarmDO::getAlarmStatus, Collectors.counting()));
        
        // 按状态顺序排序
        List<String> statusOrder = Arrays.asList(
                AlarmStatusEnum.PENDING.getStatus(),
                AlarmStatusEnum.ACKNOWLEDGED.getStatus(),
                AlarmStatusEnum.HANDLING.getStatus(),
                AlarmStatusEnum.CLOSED.getStatus()
        );
        
        return statusOrder.stream()
                .filter(statusCounts::containsKey)
                .map(status -> {
                    AlarmDistributionRespVO vo = new AlarmDistributionRespVO();
                    vo.setCode(status);
                    vo.setName(getStatusDisplayName(status));
                    vo.setCount(statusCounts.get(status).intValue());
                    vo.setPercentage(totalCount > 0 ? (statusCounts.get(status) * 100.0 / totalCount) : 0.0);
                    return vo;
                })
                .collect(Collectors.toList());
    }


    /**
     * 统计超时未确认告警数
     * 
     * 根据告警级别判断超时时间：
     * - 紧急：1分钟
     * - 严重：5分钟
     * - 警告：15分钟
     * - 信息：30分钟
     */
    private Integer countTimeoutPendingAlarms(List<AlarmDO> alarms) {
        LocalDateTime now = LocalDateTime.now();
        
        return (int) alarms.stream()
                .filter(alarm -> AlarmStatusEnum.PENDING.getStatus().equals(alarm.getAlarmStatus()))
                .filter(alarm -> {
                    long minutesSinceCreation = ChronoUnit.MINUTES.between(alarm.getCreateTime(), now);
                    int threshold = getEscalationThreshold(alarm.getAlarmLevel());
                    return minutesSinceCreation >= threshold;
                })
                .count();
    }

    /**
     * 统计超时未处理告警数
     * 
     * 已确认但超过30分钟未处理的告警
     */
    private Integer countTimeoutHandlingAlarms(List<AlarmDO> alarms) {
        LocalDateTime now = LocalDateTime.now();
        final int HANDLING_TIMEOUT_MINUTES = 30;
        
        return (int) alarms.stream()
                .filter(alarm -> AlarmStatusEnum.ACKNOWLEDGED.getStatus().equals(alarm.getAlarmStatus()))
                .filter(alarm -> alarm.getAcknowledgeTime() != null)
                .filter(alarm -> {
                    long minutesSinceAcknowledge = ChronoUnit.MINUTES.between(alarm.getAcknowledgeTime(), now);
                    return minutesSinceAcknowledge >= HANDLING_TIMEOUT_MINUTES;
                })
                .count();
    }

    /**
     * 获取告警升级时间阈值（分钟）
     */
    private int getEscalationThreshold(String alarmLevel) {
        if (AlarmLevelEnum.EMERGENCY.getLevel().equals(alarmLevel)) {
            return 1;
        } else if (AlarmLevelEnum.CRITICAL.getLevel().equals(alarmLevel)) {
            return 5;
        } else if (AlarmLevelEnum.WARNING.getLevel().equals(alarmLevel)) {
            return 15;
        } else {
            return 30; // INFO
        }
    }

    /**
     * 获取告警级别显示名称
     */
    private String getLevelDisplayName(String level) {
        if (AlarmLevelEnum.EMERGENCY.getLevel().equals(level)) {
            return "紧急";
        } else if (AlarmLevelEnum.CRITICAL.getLevel().equals(level)) {
            return "严重";
        } else if (AlarmLevelEnum.WARNING.getLevel().equals(level)) {
            return "警告";
        } else if (AlarmLevelEnum.INFO.getLevel().equals(level)) {
            return "信息";
        }
        return level;
    }

    /**
     * 获取告警状态显示名称
     */
    private String getStatusDisplayName(String status) {
        if (AlarmStatusEnum.PENDING.getStatus().equals(status)) {
            return "待确认";
        } else if (AlarmStatusEnum.ACKNOWLEDGED.getStatus().equals(status)) {
            return "已确认";
        } else if (AlarmStatusEnum.HANDLING.getStatus().equals(status)) {
            return "处理中";
        } else if (AlarmStatusEnum.CLOSED.getStatus().equals(status)) {
            return "已关闭";
        }
        return status;
    }

}
