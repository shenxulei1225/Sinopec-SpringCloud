package cn.iocoder.yudao.module.emergency.service.statistics;

import cn.iocoder.yudao.module.emergency.controller.admin.statistics.vo.*;

import java.time.LocalDateTime;

/**
 * 应急统计分析 Service 接口
 *
 * @author 芋道源码
 */
public interface EmergencyStatisticsService {

    /**
     * 获取事件统计数据
     *
     * @param reqVO 统计请求参数
     * @return 事件统计结果
     */
    EventStatisticsRespVO getEventStatistics(EventStatisticsReqVO reqVO);

    /**
     * 获取响应统计数据
     *
     * @param reqVO 统计请求参数
     * @return 响应统计结果
     */
    ResponseStatisticsRespVO getResponseStatistics(ResponseStatisticsReqVO reqVO);

    /**
     * 获取资源统计数据
     *
     * @param reqVO 统计请求参数
     * @return 资源统计结果
     */
    ResourceStatisticsRespVO getResourceStatistics(ResourceStatisticsReqVO reqVO);

    /**
     * 获取处置效果统计数据
     *
     * @param reqVO 统计请求参数
     * @return 处置效果统计结果
     */
    EffectivenessStatisticsRespVO getEffectivenessStatistics(EffectivenessStatisticsReqVO reqVO);

    /**
     * 获取综合统计概览
     *
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 综合统计概览
     */
    StatisticsOverviewRespVO getStatisticsOverview(LocalDateTime startTime, LocalDateTime endTime);
}




