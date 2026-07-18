package cn.iocoder.yudao.module.alarm.dal.mysql;

import cn.cheers.x.framework.common.pojo.PageResult;
import cn.cheers.x.framework.mybatis.core.mapper.BaseMapperX;
import cn.cheers.x.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.alarm.controller.admin.vo.alarm.AlarmPageReqVO;
import cn.iocoder.yudao.module.alarm.dal.dataobject.AlarmDO;
import cn.iocoder.yudao.module.alarm.enums.AlarmStatusEnum;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 告警 Mapper
 *
 * @author 告警管理模块
 */
@Mapper
public interface AlarmMapper extends BaseMapperX<AlarmDO> {

    /**
     * 分页查询告警列表
     *
     * @param reqVO 查询条件
     * @return 告警分页结果
     */
    default PageResult<AlarmDO> selectPage(AlarmPageReqVO reqVO) {
        LambdaQueryWrapperX<AlarmDO> wrapper = new LambdaQueryWrapperX<AlarmDO>()
                .likeIfPresent(AlarmDO::getAlarmCode, reqVO.getAlarmCode())
                .inIfPresent(AlarmDO::getAlarmTypeId, reqVO.getAlarmTypeIds())
                .inIfPresent(AlarmDO::getAlarmCategoryId, reqVO.getAlarmCategoryIds())
                .inIfPresent(AlarmDO::getAlarmLevel, reqVO.getAlarmLevels())
                .inIfPresent(AlarmDO::getAlarmStatus, reqVO.getAlarmStatuses())
                .eqIfPresent(AlarmDO::getAlarmSource, reqVO.getAlarmSource())
                .eqIfPresent(AlarmDO::getDeviceId, reqVO.getDeviceId())
                .eqIfPresent(AlarmDO::getLocationId, reqVO.getLocationId())
                .likeIfPresent(AlarmDO::getAlarmContent, reqVO.getAlarmContentKeyword())
                .geIfPresent(AlarmDO::getCreateTime, reqVO.getCreateTimeStart())
                .leIfPresent(AlarmDO::getCreateTime, reqVO.getCreateTimeEnd())
                .orderByDesc(AlarmDO::getCreateTime);
        
        // 如果只查询实时告警（未关闭）
        if (Boolean.TRUE.equals(reqVO.getRealTimeOnly())) {
            wrapper.ne(AlarmDO::getAlarmStatus, AlarmStatusEnum.CLOSED.getStatus());
        }
        
        return selectPage(reqVO, wrapper);
    }

    /**
     * 根据告警编码查询告警
     *
     * @param alarmCode 告警编码
     * @return 告警信息
     */
    default AlarmDO selectByAlarmCode(String alarmCode) {
        return selectOne(AlarmDO::getAlarmCode, alarmCode);
    }

    /**
     * 查询实时告警列表（未关闭的告警）
     *
     * @return 实时告警列表
     */
    default List<AlarmDO> selectRealTimeAlarms() {
        return selectList(new LambdaQueryWrapperX<AlarmDO>()
                .ne(AlarmDO::getAlarmStatus, AlarmStatusEnum.CLOSED.getStatus())
                .orderByDesc(AlarmDO::getCreateTime));
    }

    /**
     * 根据设备ID和告警类型查询最近的告警（用于告警抑制检查）
     *
     * @param deviceId    设备ID
     * @param alarmTypeId 告警类型ID
     * @param startTime   开始时间（用于5分钟窗口检查）
     * @return 告警信息
     */
    default AlarmDO selectRecentAlarm(Long deviceId, Long alarmTypeId, LocalDateTime startTime) {
        return selectOne(new LambdaQueryWrapperX<AlarmDO>()
                .eq(AlarmDO::getDeviceId, deviceId)
                .eq(AlarmDO::getAlarmTypeId, alarmTypeId)
                .ne(AlarmDO::getAlarmStatus, AlarmStatusEnum.CLOSED.getStatus())
                .ge(AlarmDO::getCreateTime, startTime)
                .orderByDesc(AlarmDO::getCreateTime)
                .last("LIMIT 1"));
    }

    /**
     * 查询需要升级的告警（超时未确认的告警）
     *
     * @param status       告警状态
     * @param beforeTime   超时时间点
     * @param escalationLevel 当前升级级别
     * @return 需要升级的告警列表
     */
    default List<AlarmDO> selectAlarmsNeedEscalation(String status, LocalDateTime beforeTime, Integer escalationLevel) {
        return selectList(new LambdaQueryWrapperX<AlarmDO>()
                .eq(AlarmDO::getAlarmStatus, status)
                .le(AlarmDO::getCreateTime, beforeTime)
                .eq(AlarmDO::getEscalationLevel, escalationLevel));
    }

    /**
     * 根据告警级别查询实时告警数量
     *
     * @param alarmLevel 告警级别
     * @return 告警数量
     */
    default Long selectCountByLevel(String alarmLevel) {
        return selectCount(new LambdaQueryWrapperX<AlarmDO>()
                .eq(AlarmDO::getAlarmLevel, alarmLevel)
                .ne(AlarmDO::getAlarmStatus, AlarmStatusEnum.CLOSED.getStatus()));
    }

    /**
     * 根据告警状态查询告警数量
     *
     * @param alarmStatus 告警状态
     * @return 告警数量
     */
    default Long selectCountByStatus(String alarmStatus) {
        return selectCount(AlarmDO::getAlarmStatus, alarmStatus);
    }

    /**
     * 查询指定时间范围内的告警列表
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 告警列表
     */
    default List<AlarmDO> selectListByTimeRange(LocalDateTime startTime, LocalDateTime endTime) {
        return selectList(new LambdaQueryWrapperX<AlarmDO>()
                .ge(AlarmDO::getCreateTime, startTime)
                .le(AlarmDO::getCreateTime, endTime)
                .orderByDesc(AlarmDO::getCreateTime));
    }

    /**
     * 根据规则ID查询告警列表
     *
     * @param ruleId 规则ID
     * @return 告警列表
     */
    default List<AlarmDO> selectListByRuleId(Long ruleId) {
        return selectList(AlarmDO::getRuleId, ruleId);
    }

    /**
     * 统计指定级别的实时告警数量
     *
     * @param alarmLevel 告警级别
     * @return 告警数量
     */
    default Long countRealTimeAlarmByLevel(String alarmLevel) {
        return selectCount(new LambdaQueryWrapperX<AlarmDO>()
                .eq(AlarmDO::getAlarmLevel, alarmLevel)
                .ne(AlarmDO::getAlarmStatus, AlarmStatusEnum.CLOSED.getStatus()));
    }

    /**
     * 统计指定时间范围内创建的告警数量
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 告警数量
     */
    default Long countByCreateTimeBetween(LocalDateTime startTime, LocalDateTime endTime) {
        return selectCount(new LambdaQueryWrapperX<AlarmDO>()
                .ge(AlarmDO::getCreateTime, startTime)
                .lt(AlarmDO::getCreateTime, endTime));
    }

    /**
     * 统计指定时间范围内已处理的告警数量（状态为 HANDLING 或 CLOSED）
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 告警数量
     */
    default Long countHandledByTimeBetween(LocalDateTime startTime, LocalDateTime endTime) {
        return selectCount(new LambdaQueryWrapperX<AlarmDO>()
                .ge(AlarmDO::getHandleTime, startTime)
                .lt(AlarmDO::getHandleTime, endTime)
                .isNotNull(AlarmDO::getHandleTime));
    }

    /**
     * 统计指定时间范围内已关闭的告警数量
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 告警数量
     */
    default Long countClosedByTimeBetween(LocalDateTime startTime, LocalDateTime endTime) {
        return selectCount(new LambdaQueryWrapperX<AlarmDO>()
                .ge(AlarmDO::getCloseTime, startTime)
                .lt(AlarmDO::getCloseTime, endTime)
                .eq(AlarmDO::getAlarmStatus, AlarmStatusEnum.CLOSED.getStatus()));
    }

    /**
     * 统计当前活跃告警数量（未关闭）
     *
     * @return 告警数量
     */
    default Long countActiveAlarms() {
        return selectCount(new LambdaQueryWrapperX<AlarmDO>()
                .ne(AlarmDO::getAlarmStatus, AlarmStatusEnum.CLOSED.getStatus()));
    }

}
