package cn.iocoder.yudao.module.emergency.dal.mysql.response;

import cn.cheers.x.framework.mybatis.core.mapper.BaseMapperX;
import cn.cheers.x.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.emergency.controller.admin.statistics.vo.*;
import cn.iocoder.yudao.module.emergency.dal.dataobject.response.EmergencyResponseDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface EmergencyResponseMapper extends BaseMapperX<EmergencyResponseDO> {

    /** 未结束：pending / executing / tracking（不含 ended、cancelled） */
    List<String> ACTIVE_STATUSES = List.of("pending", "executing", "tracking");

    default EmergencyResponseDO selectByResponseNo(String responseNo) {
        return selectOne("response_no", responseNo);
    }

    /**
     * 按事件取一条响应：优先最新未结束；若无则取最新任意一条。
     * 禁止裸 selectOne(event_id)——历史双写会导致 TooManyResultsException。
     */
    default EmergencyResponseDO selectByEventId(Long eventId) {
        if (eventId == null) {
            return null;
        }
        EmergencyResponseDO active = selectLatestActiveByEventId(eventId);
        if (active != null) {
            return active;
        }
        return selectOne(new LambdaQueryWrapperX<EmergencyResponseDO>()
                .eq(EmergencyResponseDO::getEventId, eventId)
                .orderByDesc(EmergencyResponseDO::getId)
                .last("LIMIT 1"));
    }

    /** 该事件是否已有进行中的响应（写路径防重） */
    default EmergencyResponseDO selectLatestActiveByEventId(Long eventId) {
        if (eventId == null) {
            return null;
        }
        return selectOne(new LambdaQueryWrapperX<EmergencyResponseDO>()
                .eq(EmergencyResponseDO::getEventId, eventId)
                .in(EmergencyResponseDO::getStatus, ACTIVE_STATUSES)
                .orderByDesc(EmergencyResponseDO::getId)
                .last("LIMIT 1"));
    }

    // 统计相关方法
    List<Map<String, Object>> calculateAverageResponseTimes(@Param("reqVO") ResponseTimeStatisticsReqVO reqVO);

    List<ResponseTimeStatisticsRespVO.LevelTimeStatsVO> calculateResponseTimesByLevel(@Param("reqVO") ResponseTimeStatisticsReqVO reqVO);

    List<ResponseTimeStatisticsRespVO.TypeTimeStatsVO> calculateResponseTimesByType(@Param("reqVO") ResponseTimeStatisticsReqVO reqVO);

    List<ResponseTimeStatisticsRespVO.TimeRangeCountVO> calculateTimeDistribution(@Param("reqVO") ResponseTimeStatisticsReqVO reqVO);

    Long countSuccessfulResponses(@Param("reqVO") HandlingEffectStatisticsReqVO reqVO);

    Long countTotalResponses(@Param("reqVO") HandlingEffectStatisticsReqVO reqVO);

    List<HandlingEffectStatisticsRespVO.LevelSuccessRateVO> calculateSuccessRateByLevel(@Param("reqVO") HandlingEffectStatisticsReqVO reqVO);

    List<HandlingEffectStatisticsRespVO.TypeSuccessRateVO> calculateSuccessRateByType(@Param("reqVO") HandlingEffectStatisticsReqVO reqVO);

    List<HandlingEffectStatisticsRespVO.UpgradeStatsVO> calculateUpgradeStats(@Param("reqVO") HandlingEffectStatisticsReqVO reqVO);

    List<HandlingEffectStatisticsRespVO.CancelStatsVO> calculateCancelStats(@Param("reqVO") HandlingEffectStatisticsReqVO reqVO);

    List<HandlingEffectStatisticsRespVO.TimeEfficiencyVO> calculateTimeEfficiency(@Param("reqVO") HandlingEffectStatisticsReqVO reqVO);
}

