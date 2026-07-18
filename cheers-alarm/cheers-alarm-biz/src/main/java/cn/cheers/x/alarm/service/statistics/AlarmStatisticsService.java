package cn.cheers.x.alarm.service.statistics;

import cn.cheers.x.alarm.controller.admin.vo.statistics.*;

import java.util.List;

/**
 * 告警统计 Service 接口
 *
 * @author 告警管理模块
 */
public interface AlarmStatisticsService {

    /**
     * 获取实时告警统计
     * 
     * 包含：当前活跃告警数、今日新增/处理/关闭数、各级别告警数、平均响应/处理时间
     *
     * @return 实时告警统计数据
     */
    AlarmStatisticsRespVO getRealTimeStatistics();

    /**
     * 获取告警趋势数据
     * 
     * 按日期统计告警数量变化趋势
     *
     * @param reqVO 查询条件（时间范围、告警类型、级别等）
     * @return 告警趋势数据列表
     */
    List<AlarmTrendRespVO> getAlarmTrend(AlarmTrendReqVO reqVO);

    /**
     * 获取告警分布统计
     * 
     * 支持按类型、级别、位置、状态等维度统计告警分布
     *
     * @param reqVO 查询条件（分布类型、时间范围等）
     * @return 告警分布统计数据列表
     */
    List<AlarmDistributionRespVO> getAlarmDistribution(AlarmDistributionReqVO reqVO);

    /**
     * 获取告警处理效率指标
     * 
     * 包含：平均响应/处理/关闭时间、处理率、关闭率、超时告警数等
     *
     * @param reqVO 查询条件（时间范围、告警分类、级别等）
     * @return 告警处理效率指标
     */
    AlarmEfficiencyRespVO getProcessingEfficiency(AlarmEfficiencyReqVO reqVO);

}
