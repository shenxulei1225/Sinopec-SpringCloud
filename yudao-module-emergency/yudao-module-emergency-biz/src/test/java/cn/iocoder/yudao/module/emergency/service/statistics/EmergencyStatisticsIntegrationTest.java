package cn.iocoder.yudao.module.emergency.service.statistics;

import cn.iocoder.yudao.module.emergency.framework.test.BasePostgresDbUnitTest;
import cn.iocoder.yudao.module.emergency.controller.admin.statistics.vo.*;
import cn.iocoder.yudao.module.emergency.dal.dataobject.event.EmergencyEventDO;
import cn.iocoder.yudao.module.emergency.dal.dataobject.response.EmergencyResponseDO;
import cn.iocoder.yudao.module.emergency.dal.dataobject.task.EmergencyTaskDO;
import cn.iocoder.yudao.module.emergency.dal.dataobject.dispatch.ResourceDispatchDO;
import cn.iocoder.yudao.module.emergency.dal.mysql.event.EmergencyEventMapper;
import cn.iocoder.yudao.module.emergency.dal.mysql.response.EmergencyResponseMapper;
import cn.iocoder.yudao.module.emergency.dal.mysql.task.EmergencyTaskMapper;
import cn.iocoder.yudao.module.emergency.dal.mysql.dispatch.ResourceDispatchMapper;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 统计分析集成测试
 * 
 * 测试覆盖：
 * 1. 统计概览查询（综合统计）
 * 2. 事件统计查询（类型分布、级别分布、月度趋势）
 * 3. 响应统计查询（级别分布、时间趋势）
 * 4. 资源统计查询（类型使用、调度成功率）
 * 5. 处置效果统计查询（类型效果、级别效果、时间分布）
 * 6. 时间范围查询验证
 * 7. 空数据场景处理
 * 
 * @author 应急管理系统
 */
@Import(EmergencyStatisticsServiceImpl.class)
@DisplayName("统计分析集成测试")
class EmergencyStatisticsIntegrationTest extends BasePostgresDbUnitTest {

    @Resource
    private EmergencyStatisticsService statisticsService;
    
    @Resource
    private EmergencyEventMapper eventMapper;
    
    @Resource
    private EmergencyResponseMapper responseMapper;
    
    @Resource
    private EmergencyTaskMapper taskMapper;
    
    @Resource
    private ResourceDispatchMapper dispatchMapper;

    private LocalDateTime testStartTime;
    private LocalDateTime testEndTime;

    @BeforeEach
    void setUp() {
        // 设置测试时间范围（最近30天）
        testEndTime = LocalDateTime.now();
        testStartTime = testEndTime.minusDays(30);
        
        // 清理测试数据（可选，根据实际情况决定）
        // cleanTestData();
        
        // 准备测试数据
        prepareTestData();
    }

    @Test
    @DisplayName("统计概览：应返回综合统计数据")
    void testGetStatisticsOverview() {
        // 执行统计概览查询
        StatisticsOverviewRespVO result = statisticsService.getStatisticsOverview(testStartTime, testEndTime);
        
        // 验证结果不为空
        assertNotNull(result, "统计概览结果不应为空");
        
        // 验证基本字段存在
        assertNotNull(result.getTotalEvents(), "总事件数不应为空");
        assertNotNull(result.getTotalResponses(), "总响应数不应为空");
        assertNotNull(result.getTotalTasks(), "总任务数不应为空");
        assertNotNull(result.getTotalResourceDispatches(), "总资源调度数不应为空");
        
        // 验证数值非负
        assertTrue(result.getTotalEvents() >= 0, "总事件数应非负");
        assertTrue(result.getTotalResponses() >= 0, "总响应数应非负");
        assertTrue(result.getTotalTasks() >= 0, "总任务数应非负");
        assertTrue(result.getTotalResourceDispatches() >= 0, "总资源调度数应非负");
        
        // 验证系统健康状态
        assertNotNull(result.getSystemHealthStatus(), "系统健康状态不应为空");
    }

    @Test
    @DisplayName("事件统计：应返回事件相关的统计数据")
    void testGetEventStatistics() {
        // 准备请求参数
        EventStatisticsReqVO reqVO = new EventStatisticsReqVO();
        reqVO.setStartTime(testStartTime);
        reqVO.setEndTime(testEndTime);
        
        // 执行事件统计查询
        EventStatisticsRespVO result = statisticsService.getEventStatistics(reqVO);
        
        // 验证结果不为空
        assertNotNull(result, "事件统计结果不应为空");
        
        // 验证基本字段
        assertNotNull(result.getTotalEvents(), "总事件数不应为空");
        assertTrue(result.getTotalEvents() >= 0, "总事件数应非负");
        
        // 验证分布数据（可能为空，但字段应存在）
        assertNotNull(result.getTypeDistribution(), "类型分布不应为空");
        assertNotNull(result.getLevelDistribution(), "级别分布不应为空");
        assertNotNull(result.getMonthlyTrend(), "月度趋势不应为空");
    }

    @Test
    @DisplayName("响应统计：应返回响应相关的统计数据")
    void testGetResponseStatistics() {
        // 准备请求参数
        ResponseStatisticsReqVO reqVO = new ResponseStatisticsReqVO();
        reqVO.setStartTime(testStartTime);
        reqVO.setEndTime(testEndTime);
        
        // 执行响应统计查询
        ResponseStatisticsRespVO result = statisticsService.getResponseStatistics(reqVO);
        
        // 验证结果不为空
        assertNotNull(result, "响应统计结果不应为空");
        
        // 验证基本字段
        assertNotNull(result.getTotalResponses(), "总响应数不应为空");
        assertTrue(result.getTotalResponses() >= 0, "总响应数应非负");
        
        // 验证分布数据
        assertNotNull(result.getLevelDistribution(), "级别分布不应为空");
        assertNotNull(result.getTimeTrend(), "时间趋势不应为空");
    }

    @Test
    @DisplayName("资源统计：应返回资源使用相关的统计数据")
    void testGetResourceStatistics() {
        // 准备请求参数
        ResourceStatisticsReqVO reqVO = new ResourceStatisticsReqVO();
        reqVO.setStartTime(testStartTime);
        reqVO.setEndTime(testEndTime);
        
        // 执行资源统计查询
        ResourceStatisticsRespVO result = statisticsService.getResourceStatistics(reqVO);
        
        // 验证结果不为空
        assertNotNull(result, "资源统计结果不应为空");
        
        // 验证基本字段
        assertNotNull(result.getTotalDispatches(), "总调度数不应为空");
        assertTrue(result.getTotalDispatches() >= 0, "总调度数应非负");
        
        // 验证成功率（0-100之间）
        assertNotNull(result.getDispatchSuccessRate(), "调度成功率不应为空");
        assertTrue(result.getDispatchSuccessRate() >= 0 && result.getDispatchSuccessRate() <= 100, 
                "调度成功率应在0-100之间");
        
        // 验证类型使用数据
        assertNotNull(result.getTypeUsage(), "类型使用数据不应为空");
    }

    @Test
    @DisplayName("处置效果统计：应返回处置效果相关的统计数据")
    void testGetEffectivenessStatistics() {
        // 准备请求参数
        EffectivenessStatisticsReqVO reqVO = new EffectivenessStatisticsReqVO();
        reqVO.setStartTime(testStartTime);
        reqVO.setEndTime(testEndTime);
        
        // 执行处置效果统计查询
        EffectivenessStatisticsRespVO result = statisticsService.getEffectivenessStatistics(reqVO);
        
        // 验证结果不为空
        assertNotNull(result, "处置效果统计结果不应为空");
        
        // 验证基本字段
        assertNotNull(result.getTotalEvents(), "总事件数不应为空");
        assertTrue(result.getTotalEvents() >= 0, "总事件数应非负");
        
        // 验证成功率（0-100之间）
        assertNotNull(result.getSuccessRate(), "成功率不应为空");
        assertTrue(result.getSuccessRate() >= 0 && result.getSuccessRate() <= 100, 
                "成功率应在0-100之间");
        
        // 验证分布数据
        assertNotNull(result.getTypeEffectiveness(), "类型效果数据不应为空");
        assertNotNull(result.getLevelEffectiveness(), "级别效果数据不应为空");
        assertNotNull(result.getTimeDistribution(), "时间分布数据不应为空");
    }

    @Test
    @DisplayName("时间范围查询：不同时间范围应返回不同结果")
    void testTimeRangeQuery() {
        // 查询最近7天
        LocalDateTime recent7DaysStart = testEndTime.minusDays(7);
        StatisticsOverviewRespVO recent7Days = statisticsService.getStatisticsOverview(
                recent7DaysStart, testEndTime);
        
        // 查询最近30天
        StatisticsOverviewRespVO recent30Days = statisticsService.getStatisticsOverview(
                testStartTime, testEndTime);
        
        // 验证结果不为空
        assertNotNull(recent7Days, "最近7天统计不应为空");
        assertNotNull(recent30Days, "最近30天统计不应为空");
        
        // 验证30天的数据应该大于等于7天的数据（因为时间范围更大）
        assertTrue(recent30Days.getTotalEvents() >= recent7Days.getTotalEvents(), 
                "30天的总事件数应大于等于7天的总事件数");
    }

    @Test
    @DisplayName("空数据场景：无数据时应返回空结果而非异常")
    void testEmptyDataScenario() {
        // 查询一个没有数据的时间范围（未来时间）
        LocalDateTime futureStart = testEndTime.plusDays(1);
        LocalDateTime futureEnd = testEndTime.plusDays(7);
        
        // 执行查询（不应抛出异常）
        assertDoesNotThrow(() -> {
            StatisticsOverviewRespVO result = statisticsService.getStatisticsOverview(futureStart, futureEnd);
            assertNotNull(result, "即使无数据，结果也不应为null");
            // 验证数值应为0或空
            assertEquals(0, result.getTotalEvents(), "无数据时总事件数应为0");
        }, "无数据时查询不应抛出异常");
    }

    @Test
    @DisplayName("综合统计：多个统计接口应返回一致的数据")
    void testConsistencyAcrossStatistics() {
        // 获取统计概览
        StatisticsOverviewRespVO overview = statisticsService.getStatisticsOverview(testStartTime, testEndTime);
        
        // 获取事件统计
        EventStatisticsReqVO eventReq = new EventStatisticsReqVO();
        eventReq.setStartTime(testStartTime);
        eventReq.setEndTime(testEndTime);
        EventStatisticsRespVO eventStats = statisticsService.getEventStatistics(eventReq);
        
        // 获取响应统计
        ResponseStatisticsReqVO responseReq = new ResponseStatisticsReqVO();
        responseReq.setStartTime(testStartTime);
        responseReq.setEndTime(testEndTime);
        ResponseStatisticsRespVO responseStats = statisticsService.getResponseStatistics(responseReq);
        
        // 验证数据一致性（概览中的总数应该与各统计中的总数一致或相关）
        assertNotNull(overview, "统计概览不应为空");
        assertNotNull(eventStats, "事件统计不应为空");
        assertNotNull(responseStats, "响应统计不应为空");
        
        // 验证概览中的总事件数应该等于事件统计中的总事件数
        assertEquals(overview.getTotalEvents(), eventStats.getTotalEvents(), 
                "概览中的总事件数应与事件统计中的总事件数一致");
        
        // 验证概览中的总响应数应该等于响应统计中的总响应数
        assertEquals(overview.getTotalResponses(), responseStats.getTotalResponses(), 
                "概览中的总响应数应与响应统计中的总响应数一致");
    }

    /**
     * 准备测试数据
     */
    private void prepareTestData() {
        // 创建测试事件
        EmergencyEventDO event1 = EmergencyEventDO.builder()
                .eventCode("EVT-STAT-001")
                .eventType(1L)
                .status("closed")
                .discoveredAt(testStartTime.plusDays(1))
                .build();
        eventMapper.insert(event1);

        EmergencyEventDO event2 = EmergencyEventDO.builder()
                .eventCode("EVT-STAT-002")
                .eventType(2L)
                .status("closed")
                .discoveredAt(testStartTime.plusDays(5))
                .build();
        eventMapper.insert(event2);

        // 创建测试响应
        EmergencyResponseDO response1 = EmergencyResponseDO.builder()
                .eventId(event1.getId())
                .responseLevel("II")
                .status("completed")
                .build();
        responseMapper.insert(response1);

        EmergencyResponseDO response2 = EmergencyResponseDO.builder()
                .eventId(event2.getId())
                .responseLevel("III")
                .status("completed")
                .build();
        responseMapper.insert(response2);

        // 创建测试任务
        EmergencyTaskDO task1 = EmergencyTaskDO.builder()
                .eventId(event1.getId())
                .responseId(response1.getId())
                .taskCode("TASK-STAT-001")
                .title("测试任务1")
                .status("completed")
                .build();
        taskMapper.insert(task1);

        EmergencyTaskDO task2 = EmergencyTaskDO.builder()
                .eventId(event2.getId())
                .responseId(response2.getId())
                .taskCode("TASK-STAT-002")
                .title("测试任务2")
                .status("completed")
                .build();
        taskMapper.insert(task2);

        // 创建测试资源调度
        ResourceDispatchDO dispatch1 = ResourceDispatchDO.builder()
                .eventId(event1.getId())
                .responseId(response1.getId())
                .resourceId(100L)
                .status("completed")
                .build();
        dispatchMapper.insert(dispatch1);

        ResourceDispatchDO dispatch2 = ResourceDispatchDO.builder()
                .eventId(event2.getId())
                .responseId(response2.getId())
                .resourceId(200L)
                .status("completed")
                .build();
        dispatchMapper.insert(dispatch2);
    }
}








