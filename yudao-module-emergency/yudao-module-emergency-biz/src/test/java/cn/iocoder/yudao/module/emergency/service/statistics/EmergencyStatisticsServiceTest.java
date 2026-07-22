package cn.iocoder.yudao.module.emergency.service.statistics;

import cn.iocoder.yudao.module.emergency.controller.admin.statistics.vo.*;
import cn.iocoder.yudao.module.emergency.framework.test.BasePostgresDbUnitTest;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;

import jakarta.annotation.Resource;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 应急统计分析服务测试
 */
@Import(EmergencyStatisticsServiceImpl.class)
class EmergencyStatisticsServiceTest extends BasePostgresDbUnitTest {

    @Resource
    private EmergencyStatisticsService statisticsService;

    @Test
    public void testGetStatisticsOverview() {
        // 测试统计概览
        LocalDateTime startTime = LocalDateTime.of(2023, 1, 1, 0, 0);
        LocalDateTime endTime = LocalDateTime.of(2023, 12, 31, 23, 59);

        StatisticsOverviewRespVO result = statisticsService.getStatisticsOverview(startTime, endTime);

        assertNotNull(result);
        assertEquals(156, result.getTotalEvents());
        assertEquals(142, result.getTotalResponses());
        assertEquals(456, result.getTotalTasks());
        assertEquals(234, result.getTotalResourceDispatches());
        assertEquals("healthy", result.getSystemHealthStatus());
    }

    @Test
    public void testGetEventStatistics() {
        // 测试事件统计
        EventStatisticsReqVO reqVO = new EventStatisticsReqVO();
        reqVO.setStartTime(LocalDateTime.of(2023, 1, 1, 0, 0));
        reqVO.setEndTime(LocalDateTime.of(2023, 12, 31, 23, 59));

        EventStatisticsRespVO result = statisticsService.getEventStatistics(reqVO);

        assertNotNull(result);
        assertNotNull(result.getTypeDistribution());
        assertNotNull(result.getLevelDistribution());
        assertNotNull(result.getMonthlyTrend());
        assertTrue(result.getTotalEvents() >= 0);
    }

    @Test
    public void testGetResponseStatistics() {
        // 测试响应统计
        ResponseStatisticsReqVO reqVO = new ResponseStatisticsReqVO();
        reqVO.setStartTime(LocalDateTime.of(2023, 1, 1, 0, 0));
        reqVO.setEndTime(LocalDateTime.of(2023, 12, 31, 23, 59));

        ResponseStatisticsRespVO result = statisticsService.getResponseStatistics(reqVO);

        assertNotNull(result);
        assertNotNull(result.getLevelDistribution());
        assertNotNull(result.getTimeTrend());
        assertTrue(result.getTotalResponses() >= 0);
    }

    @Test
    public void testGetResourceStatistics() {
        // 测试资源统计
        ResourceStatisticsReqVO reqVO = new ResourceStatisticsReqVO();
        reqVO.setStartTime(LocalDateTime.of(2023, 1, 1, 0, 0));
        reqVO.setEndTime(LocalDateTime.of(2023, 12, 31, 23, 59));

        ResourceStatisticsRespVO result = statisticsService.getResourceStatistics(reqVO);

        assertNotNull(result);
        assertNotNull(result.getTypeUsage());
        assertTrue(result.getTotalDispatches() >= 0);
        assertTrue(result.getDispatchSuccessRate() >= 0);
    }

    @Test
    public void testGetEffectivenessStatistics() {
        // 测试效果统计
        EffectivenessStatisticsReqVO reqVO = new EffectivenessStatisticsReqVO();
        reqVO.setStartTime(LocalDateTime.of(2023, 1, 1, 0, 0));
        reqVO.setEndTime(LocalDateTime.of(2023, 12, 31, 23, 59));

        EffectivenessStatisticsRespVO result = statisticsService.getEffectivenessStatistics(reqVO);

        assertNotNull(result);
        assertNotNull(result.getTypeEffectiveness());
        assertNotNull(result.getLevelEffectiveness());
        assertNotNull(result.getTimeDistribution());
        assertTrue(result.getTotalEvents() >= 0);
        assertTrue(result.getSuccessRate() >= 0);
    }
}




