package cn.iocoder.yudao.module.emergency.dal.mysql.dispatch;

import cn.cheers.x.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.emergency.controller.admin.statistics.vo.ResourceUsageStatisticsReqVO;
import cn.iocoder.yudao.module.emergency.controller.admin.statistics.vo.ResourceUsageStatisticsRespVO;
import cn.iocoder.yudao.module.emergency.dal.dataobject.dispatch.ResourceDispatchDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface ResourceDispatchMapper extends BaseMapperX<ResourceDispatchDO> {

    /**
     * 根据 ID 更新状态
     * 当状态为 'recovered' 时，自动设置 recovered_time
     *
     * @param id     调度 ID
     * @param status 新状态
     */
    void updateStatusById(@Param("id") Long id, @Param("status") String status);

    /**
     * 根据响应 ID 查询调度记录列表
     *
     * @param responseId 响应 ID
     * @return 调度记录列表
     */
    List<ResourceDispatchDO> selectByResponseId(@Param("responseId") Long responseId);

    // 统计相关方法
    Map<String, Object> calculateDispatchStats(@Param("reqVO") ResourceUsageStatisticsReqVO reqVO);

    List<ResourceUsageStatisticsRespVO.ResourceTypeStatsVO> countByResourceType(@Param("reqVO") ResourceUsageStatisticsReqVO reqVO);

    List<ResourceUsageStatisticsRespVO.LevelResourceStatsVO> countByResponseLevel(@Param("reqVO") ResourceUsageStatisticsReqVO reqVO);

    List<ResourceUsageStatisticsRespVO.TopResourceVO> findTopResources(@Param("reqVO") ResourceUsageStatisticsReqVO reqVO);

    List<ResourceUsageStatisticsRespVO.ResourceUtilizationVO> calculateUtilization(@Param("reqVO") ResourceUsageStatisticsReqVO reqVO);
}

