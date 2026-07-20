package cn.iocoder.yudao.module.emergency.dal.mysql.response;

import cn.cheers.x.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.emergency.controller.admin.statistics.vo.*;
import cn.iocoder.yudao.module.emergency.dal.dataobject.response.EmergencyResponseDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface EmergencyResponseMapper extends BaseMapperX<EmergencyResponseDO> {

    default EmergencyResponseDO selectByResponseNo(String responseNo) {
        return selectOne("response_no", responseNo);
    }

    default EmergencyResponseDO selectByEventId(Long eventId) {
        return selectOne("event_id", eventId);
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

