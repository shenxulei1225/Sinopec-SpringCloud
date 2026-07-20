package cn.iocoder.yudao.module.emergency.service.statistics;

import cn.iocoder.yudao.module.emergency.controller.admin.statistics.vo.*;
import cn.iocoder.yudao.module.emergency.dal.dataobject.event.EmergencyEventDO;
import cn.iocoder.yudao.module.emergency.dal.dataobject.response.EmergencyResponseDO;
import cn.iocoder.yudao.module.emergency.dal.mysql.event.EmergencyEventMapper;
import cn.iocoder.yudao.module.emergency.dal.mysql.response.EmergencyResponseMapper;
import cn.iocoder.yudao.module.emergency.dal.mysql.task.EmergencyTaskMapper;
import cn.iocoder.yudao.module.emergency.dal.mysql.dispatch.ResourceDispatchMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 应急统计分析 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Slf4j
public class EmergencyStatisticsServiceImpl implements EmergencyStatisticsService {

    @Resource
    private EmergencyEventMapper eventMapper;

    @Resource
    private EmergencyResponseMapper responseMapper;

    @Resource
    private EmergencyTaskMapper taskMapper;

    @Resource
    private ResourceDispatchMapper resourceDispatchMapper;

    @Override
    public EventStatisticsRespVO getEventStatistics(EventStatisticsReqVO reqVO) {
        EventStatisticsRespVO respVO = new EventStatisticsRespVO();

        // 获取基础数据
        List<EmergencyEventDO> events = getFilteredEvents(reqVO);

        // 计算基础统计
        respVO.setTotalEvents(events.size());
        long handledEvents = events.stream()
                .filter(event -> "handled".equals(event.getStatus()) || "closed".equals(event.getStatus()))
                .count();
        respVO.setHandledEvents((int) handledEvents);
        respVO.setHandlingRate(events.isEmpty() ? 0.0 : (double) handledEvents / events.size() * 100);

        // 计算类型分布
        respVO.setTypeDistribution(calculateTypeDistribution(events));

        // 计算级别分布
        respVO.setLevelDistribution(calculateLevelDistribution(events));

        // 计算月份趋势
        respVO.setMonthlyTrend(calculateMonthlyTrend(events, reqVO.getStartTime(), reqVO.getEndTime()));

        // 计算响应时间统计
        calculateResponseTimeStatistics(respVO, events);

        return respVO;
    }

    @Override
    public ResponseStatisticsRespVO getResponseStatistics(ResponseStatisticsReqVO reqVO) {
        ResponseStatisticsRespVO respVO = new ResponseStatisticsRespVO();

        // 获取响应数据
        List<EmergencyResponseDO> responses = getFilteredResponses(reqVO);

        // 计算基础统计
        respVO.setTotalResponses(responses.size());
        long endedResponses = responses.stream()
                .filter(response -> "ended".equals(response.getStatus()) || "cancelled".equals(response.getStatus()))
                .count();
        respVO.setEndedResponses((int) endedResponses);
        respVO.setCompletionRate(responses.isEmpty() ? 0.0 : (double) endedResponses / responses.size() * 100);

        // 计算级别分布
        respVO.setLevelDistribution(calculateResponseLevelDistribution(responses));

        // 计算时间统计（简化实现）
        respVO.setAverageStartTime(15.0); // 模拟数据
        respVO.setAverageDuration(120.0); // 模拟数据
        respVO.setMaxDuration(480); // 模拟数据

        // 时间趋势（简化实现）
        respVO.setTimeTrend(calculateResponseTimeTrend(responses, reqVO.getStartTime(), reqVO.getEndTime()));

        return respVO;
    }

    @Override
    public ResourceStatisticsRespVO getResourceStatistics(ResourceStatisticsReqVO reqVO) {
        ResourceStatisticsRespVO respVO = new ResourceStatisticsRespVO();

        // 获取调度记录（简化实现，使用模拟数据）
        respVO.setTotalDispatches(234);
        respVO.setCompletedDispatches(228);
        respVO.setDispatchSuccessRate(97.4);

        // 类型使用情况（模拟数据）
        respVO.setTypeUsage(Arrays.asList(
                createTypeUsageVO("vehicle", 89, 92.3, 85.6),
                createTypeUsageVO("personnel", 67, 78.5, 72.3),
                createTypeUsageVO("equipment", 45, 65.4, 68.9),
                createTypeUsageVO("material", 33, 55.7, 61.2)
        ));

        // 其他统计（模拟数据）
        respVO.setAverageUsageTime(85.6);
        respVO.setMaxUsageTime(360);
        respVO.setUtilizationTrend(Collections.emptyList());
        respVO.setStatusDistribution(Collections.emptyList());

        return respVO;
    }

    @Override
    public EffectivenessStatisticsRespVO getEffectivenessStatistics(EffectivenessStatisticsReqVO reqVO) {
        EffectivenessStatisticsRespVO respVO = new EffectivenessStatisticsRespVO();

        // 获取事件数据进行效果分析
        EventStatisticsReqVO eventReqVO = new EventStatisticsReqVO();
        eventReqVO.setStartTime(reqVO.getStartTime());
        eventReqVO.setEndTime(reqVO.getEndTime());
        eventReqVO.setEventType(reqVO.getEventType());
        List<EmergencyEventDO> events = getFilteredEvents(eventReqVO);

        // 计算基础统计
        respVO.setTotalEvents(events.size());
        long successfulEvents = events.stream()
                .filter(event -> "handled".equals(event.getStatus()) || "closed".equals(event.getStatus()))
                .count();
        respVO.setSuccessfulEvents((int) successfulEvents);
        respVO.setSuccessRate(events.isEmpty() ? 0.0 : (double) successfulEvents / events.size() * 100);

        // 时间统计（模拟数据）
        respVO.setAverageHandlingTime(95.6);
        respVO.setFastestHandlingTime(15);
        respVO.setSlowestHandlingTime(720);

        // 类型效果分析
        respVO.setTypeEffectiveness(calculateTypeEffectiveness(events));

        // 级别效果分析（模拟数据）
        respVO.setLevelEffectiveness(Arrays.asList(
                createLevelEffectivenessVO("I", 23, 22, 95.7, 45.3),
                createLevelEffectivenessVO("II", 67, 64, 95.5, 85.6),
                createLevelEffectivenessVO("III", 45, 42, 93.3, 120.4),
                createLevelEffectivenessVO("IV", 12, 11, 91.7, 180.2)
        ));

        // 时间分布（模拟数据）
        respVO.setTimeDistribution(Arrays.asList(
                createTimeDistributionVO("0-30分钟", 45, 28.8),
                createTimeDistributionVO("30-60分钟", 38, 24.4),
                createTimeDistributionVO("1-2小时", 32, 20.5),
                createTimeDistributionVO("2-4小时", 25, 16.0),
                createTimeDistributionVO("4小时以上", 16, 10.3)
        ));

        return respVO;
    }

    @Override
    public StatisticsOverviewRespVO getStatisticsOverview(LocalDateTime startTime, LocalDateTime endTime) {
        StatisticsOverviewRespVO respVO = new StatisticsOverviewRespVO();

        // 基础统计数据（可以从实际数据源获取）
        respVO.setTotalEvents(156);
        respVO.setActiveEvents(8);
        respVO.setTotalResponses(142);
        respVO.setActiveResponses(5);
        respVO.setTotalTasks(456);
        respVO.setActiveTasks(23);
        respVO.setTotalResourceDispatches(234);
        respVO.setActiveResourceDispatches(12);

        // 性能指标
        respVO.setAverageResponseTime(45.6);
        respVO.setEventHandlingRate(91.0);
        respVO.setResponseCompletionRate(94.4);
        respVO.setResourceUtilizationRate(78.5);
        respVO.setSystemHealthStatus("healthy");

        return respVO;
    }

    // ==================== 私有方法 ====================

    private List<EmergencyEventDO> getFilteredEvents(EventStatisticsReqVO reqVO) {
        // 简化实现，实际应该使用Mapper进行条件查询
        return eventMapper.selectList(); // 应该添加时间和类型筛选
    }

    private List<EmergencyResponseDO> getFilteredResponses(ResponseStatisticsReqVO reqVO) {
        // 简化实现，实际应该使用Mapper进行条件查询
        return responseMapper.selectList(); // 应该添加时间和级别筛选
    }

    private List<EventStatisticsRespVO.TypeDistributionVO> calculateTypeDistribution(List<EmergencyEventDO> events) {
        Map<Long, Long> typeCount = events.stream()
                .filter(event -> event.getEventType() != null)
                .collect(Collectors.groupingBy(EmergencyEventDO::getEventType, Collectors.counting()));

        long total = events.size();
        return typeCount.entrySet().stream()
                .map(entry -> {
                    EventStatisticsRespVO.TypeDistributionVO vo = new EventStatisticsRespVO.TypeDistributionVO();
                    vo.setEventType(String.valueOf(entry.getKey()));
                    vo.setCount(entry.getValue().intValue());
                    vo.setPercentage(total > 0 ? (double) entry.getValue() / total * 100 : 0);
                    return vo;
                })
                .collect(Collectors.toList());
    }

    private List<EventStatisticsRespVO.LevelDistributionVO> calculateLevelDistribution(List<EmergencyEventDO> events) {
        Map<String, Long> levelCount = events.stream()
                .filter(event -> event.getEventLevel() != null)
                .collect(Collectors.groupingBy(EmergencyEventDO::getEventLevel, Collectors.counting()));

        long total = events.size();
        return levelCount.entrySet().stream()
                .map(entry -> {
                    EventStatisticsRespVO.LevelDistributionVO vo = new EventStatisticsRespVO.LevelDistributionVO();
                    vo.setEventLevel(entry.getKey());
                    vo.setCount(entry.getValue().intValue());
                    vo.setPercentage(total > 0 ? (double) entry.getValue() / total * 100 : 0);
                    return vo;
                })
                .collect(Collectors.toList());
    }

    private List<EventStatisticsRespVO.MonthlyTrendVO> calculateMonthlyTrend(List<EmergencyEventDO> events,
                                                                             LocalDateTime startTime,
                                                                             LocalDateTime endTime) {
        // 简化实现，返回空列表
        return Collections.emptyList();
    }

    private void calculateResponseTimeStatistics(EventStatisticsRespVO respVO, List<EmergencyEventDO> events) {
        // 简化实现，设置模拟数据
        respVO.setAverageResponseTime(45.5);
        respVO.setMaxResponseTime(180);
        respVO.setMinResponseTime(5);
    }

    private List<ResponseStatisticsRespVO.LevelDistributionVO> calculateResponseLevelDistribution(List<EmergencyResponseDO> responses) {
        Map<String, Long> levelCount = responses.stream()
                .filter(response -> response.getResponseLevel() != null)
                .collect(Collectors.groupingBy(EmergencyResponseDO::getResponseLevel, Collectors.counting()));

        long total = responses.size();
        return levelCount.entrySet().stream()
                .map(entry -> {
                    ResponseStatisticsRespVO.LevelDistributionVO vo = new ResponseStatisticsRespVO.LevelDistributionVO();
                    vo.setResponseLevel(entry.getKey());
                    vo.setCount(entry.getValue().intValue());
                    vo.setPercentage(total > 0 ? (double) entry.getValue() / total * 100 : 0);
                    return vo;
                })
                .collect(Collectors.toList());
    }

    private List<ResponseStatisticsRespVO.TimeTrendVO> calculateResponseTimeTrend(List<EmergencyResponseDO> responses,
                                                                                   LocalDateTime startTime,
                                                                                   LocalDateTime endTime) {
        // 简化实现，返回空列表
        return Collections.emptyList();
    }

    private ResourceStatisticsRespVO.TypeUsageVO createTypeUsageVO(String type, int count, double avgTime, double rate) {
        ResourceStatisticsRespVO.TypeUsageVO vo = new ResourceStatisticsRespVO.TypeUsageVO();
        vo.setResourceType(type);
        vo.setDispatchCount(count);
        vo.setAvgUsageTime(avgTime);
        vo.setUtilizationRate(rate);
        return vo;
    }

    private List<EffectivenessStatisticsRespVO.TypeEffectivenessVO> calculateTypeEffectiveness(List<EmergencyEventDO> events) {
        Map<Long, List<EmergencyEventDO>> typeGroups = events.stream()
                .filter(event -> event.getEventType() != null)
                .collect(Collectors.groupingBy(EmergencyEventDO::getEventType));

        return typeGroups.entrySet().stream()
                .map(entry -> {
                    List<EmergencyEventDO> typeEvents = entry.getValue();
                    long successful = typeEvents.stream()
                            .filter(event -> "handled".equals(event.getStatus()) || "closed".equals(event.getStatus()))
                            .count();

                    EffectivenessStatisticsRespVO.TypeEffectivenessVO vo = new EffectivenessStatisticsRespVO.TypeEffectivenessVO();
                    vo.setEventType(String.valueOf(entry.getKey()));
                    vo.setTotalEvents(typeEvents.size());
                    vo.setSuccessfulEvents((int) successful);
                    vo.setSuccessRate(typeEvents.size() > 0 ? (double) successful / typeEvents.size() * 100 : 0);
                    vo.setAvgHandlingTime(88.3); // 模拟数据
                    return vo;
                })
                .collect(Collectors.toList());
    }

    private EffectivenessStatisticsRespVO.LevelEffectivenessVO createLevelEffectivenessVO(String level, int total, int successful, double rate, double avgTime) {
        EffectivenessStatisticsRespVO.LevelEffectivenessVO vo = new EffectivenessStatisticsRespVO.LevelEffectivenessVO();
        vo.setResponseLevel(level);
        vo.setTotalEvents(total);
        vo.setSuccessfulEvents(successful);
        vo.setSuccessRate(rate);
        vo.setAvgHandlingTime(avgTime);
        return vo;
    }

    private EffectivenessStatisticsRespVO.TimeDistributionVO createTimeDistributionVO(String range, int count, double percentage) {
        EffectivenessStatisticsRespVO.TimeDistributionVO vo = new EffectivenessStatisticsRespVO.TimeDistributionVO();
        vo.setTimeRange(range);
        vo.setEventCount(count);
        vo.setPercentage(percentage);
        return vo;
    }
}
