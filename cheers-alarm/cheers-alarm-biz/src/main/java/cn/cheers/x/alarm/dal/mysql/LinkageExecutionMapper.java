package cn.cheers.x.alarm.dal.mysql;

import cn.cheers.x.framework.common.pojo.PageResult;
import cn.cheers.x.framework.mybatis.core.mapper.BaseMapperX;
import cn.cheers.x.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.cheers.x.alarm.controller.admin.vo.linkage.LinkageExecutionPageReqVO;
import cn.cheers.x.alarm.dal.dataobject.LinkageExecutionDO;
import cn.cheers.x.alarm.enums.LinkageExecutionStatusEnum;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 联动执行记录 Mapper
 *
 * @author 告警管理模块
 */
@Mapper
public interface LinkageExecutionMapper extends BaseMapperX<LinkageExecutionDO> {

    /**
     * 分页查询联动执行记录
     *
     * @param reqVO 查询条件
     * @return 联动执行记录分页结果
     */
    default PageResult<LinkageExecutionDO> selectPage(LinkageExecutionPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<LinkageExecutionDO>()
                .eqIfPresent(LinkageExecutionDO::getAlarmId, reqVO.getAlarmId())
                .eqIfPresent(LinkageExecutionDO::getLinkageRuleId, reqVO.getLinkageRuleId())
                .eqIfPresent(LinkageExecutionDO::getActionType, reqVO.getActionType())
                .eqIfPresent(LinkageExecutionDO::getExecutionStatus, reqVO.getExecutionStatus())
                .eqIfPresent(LinkageExecutionDO::getManualIntervention, reqVO.getManualIntervention())
                .betweenIfPresent(LinkageExecutionDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(LinkageExecutionDO::getCreateTime));
    }

    /**
     * 根据告警ID查询联动执行记录
     *
     * @param alarmId 告警ID
     * @return 联动执行记录列表
     */
    default List<LinkageExecutionDO> selectListByAlarmId(Long alarmId) {
        return selectList(new LambdaQueryWrapperX<LinkageExecutionDO>()
                .eq(LinkageExecutionDO::getAlarmId, alarmId)
                .orderByAsc(LinkageExecutionDO::getCreateTime));
    }

    /**
     * 根据联动规则ID查询执行记录
     *
     * @param linkageRuleId 联动规则ID
     * @return 联动执行记录列表
     */
    default List<LinkageExecutionDO> selectListByLinkageRuleId(Long linkageRuleId) {
        return selectList(new LambdaQueryWrapperX<LinkageExecutionDO>()
                .eq(LinkageExecutionDO::getLinkageRuleId, linkageRuleId)
                .orderByDesc(LinkageExecutionDO::getCreateTime));
    }

    /**
     * 查询需要人工介入的执行记录
     *
     * @return 需要人工介入的执行记录列表
     */
    default List<LinkageExecutionDO> selectListNeedManualIntervention() {
        return selectList(new LambdaQueryWrapperX<LinkageExecutionDO>()
                .eq(LinkageExecutionDO::getManualIntervention, true)
                .eq(LinkageExecutionDO::getExecutionStatus, LinkageExecutionStatusEnum.FAILED.getStatus())
                .orderByDesc(LinkageExecutionDO::getCreateTime));
    }

    /**
     * 查询待重试的执行记录
     *
     * @param maxRetryCount 最大重试次数
     * @return 待重试的执行记录列表
     */
    default List<LinkageExecutionDO> selectListNeedRetry(Integer maxRetryCount) {
        return selectList(new LambdaQueryWrapperX<LinkageExecutionDO>()
                .eq(LinkageExecutionDO::getExecutionStatus, LinkageExecutionStatusEnum.RETRY.getStatus())
                .lt(LinkageExecutionDO::getRetryCount, maxRetryCount)
                .eq(LinkageExecutionDO::getManualIntervention, false)
                .orderByAsc(LinkageExecutionDO::getCreateTime));
    }

    /**
     * 根据执行状态查询执行记录数量
     *
     * @param executionStatus 执行状态
     * @return 执行记录数量
     */
    default Long selectCountByStatus(String executionStatus) {
        return selectCount(LinkageExecutionDO::getExecutionStatus, executionStatus);
    }

    /**
     * 查询指定时间范围内的执行记录
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 执行记录列表
     */
    default List<LinkageExecutionDO> selectListByTimeRange(LocalDateTime startTime, LocalDateTime endTime) {
        return selectList(new LambdaQueryWrapperX<LinkageExecutionDO>()
                .ge(LinkageExecutionDO::getCreateTime, startTime)
                .le(LinkageExecutionDO::getCreateTime, endTime)
                .orderByDesc(LinkageExecutionDO::getCreateTime));
    }

    /**
     * 根据告警ID和联动规则ID查询执行记录
     *
     * @param alarmId       告警ID
     * @param linkageRuleId 联动规则ID
     * @return 执行记录列表
     */
    default List<LinkageExecutionDO> selectListByAlarmIdAndLinkageRuleId(Long alarmId, Long linkageRuleId) {
        return selectList(new LambdaQueryWrapperX<LinkageExecutionDO>()
                .eq(LinkageExecutionDO::getAlarmId, alarmId)
                .eq(LinkageExecutionDO::getLinkageRuleId, linkageRuleId)
                .orderByAsc(LinkageExecutionDO::getCreateTime));
    }

    /**
     * 根据目标设备ID查询执行记录
     *
     * @param targetDeviceId 目标设备ID
     * @return 执行记录列表
     */
    default List<LinkageExecutionDO> selectListByTargetDeviceId(Long targetDeviceId) {
        return selectList(new LambdaQueryWrapperX<LinkageExecutionDO>()
                .eq(LinkageExecutionDO::getTargetDeviceId, targetDeviceId)
                .orderByDesc(LinkageExecutionDO::getCreateTime));
    }

    /**
     * 统计成功执行的记录数量
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 成功执行的记录数量
     */
    default Long selectSuccessCountByTimeRange(LocalDateTime startTime, LocalDateTime endTime) {
        return selectCount(new LambdaQueryWrapperX<LinkageExecutionDO>()
                .eq(LinkageExecutionDO::getExecutionStatus, LinkageExecutionStatusEnum.SUCCESS.getStatus())
                .ge(LinkageExecutionDO::getCreateTime, startTime)
                .le(LinkageExecutionDO::getCreateTime, endTime));
    }

    /**
     * 统计失败执行的记录数量
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 失败执行的记录数量
     */
    default Long selectFailedCountByTimeRange(LocalDateTime startTime, LocalDateTime endTime) {
        return selectCount(new LambdaQueryWrapperX<LinkageExecutionDO>()
                .eq(LinkageExecutionDO::getExecutionStatus, LinkageExecutionStatusEnum.FAILED.getStatus())
                .ge(LinkageExecutionDO::getCreateTime, startTime)
                .le(LinkageExecutionDO::getCreateTime, endTime));
    }

}
