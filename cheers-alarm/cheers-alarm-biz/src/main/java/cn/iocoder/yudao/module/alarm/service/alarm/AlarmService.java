package cn.iocoder.yudao.module.alarm.service.alarm;

import cn.cheers.x.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.alarm.controller.admin.vo.alarm.*;
import cn.iocoder.yudao.module.alarm.dal.dataobject.AlarmDO;
import jakarta.validation.Valid;

import java.util.List;

/**
 * 告警核心服务接口
 * 
 * <p>负责告警的创建、查询、状态管理和生命周期控制。</p>
 * 
 * <p>告警生命周期状态流转：
 * <pre>
 * PENDING（待确认）→ ACKNOWLEDGED（已确认）→ HANDLING（处理中）→ CLOSED（已关闭）
 * </pre>
 * </p>
 * 
 * <p>核心功能：
 * <ul>
 *   <li>告警触发：系统自动触发告警（FR-001）</li>
 *   <li>人工上报：值班员手动上报告警（FR-002）</li>
 *   <li>实时告警查询：查询当前活跃的告警（FR-004）</li>
 *   <li>历史告警查询：查询已关闭的告警（FR-005）</li>
 *   <li>告警生命周期管理：确认、处理、关闭（FR-009）</li>
 * </ul>
 * </p>
 * 
 * <p>业务规则：
 * <ul>
 *   <li>BR-BIZ-001：同一设备5分钟内相同类型告警抑制</li>
 *   <li>BR-BIZ-002：告警升级规则（超时未确认时增强提醒）</li>
 *   <li>BR-STA-001：告警状态只能按顺序流转</li>
 *   <li>BR-STA-002：告警关闭前必须填写关闭原因</li>
 *   <li>BR-VAL-003：人工上报告警内容长度10-500字</li>
 * </ul>
 * </p>
 *
 * @author 告警管理模块
 */
public interface AlarmService {

    // ========== 告警触发相关 ==========

    /**
     * 系统自动触发告警
     * 
     * <p>当设备监测数据超过预设阈值时，系统自动触发告警。
     * 触发前会进行告警抑制检查（BR-BIZ-001：5分钟内相同告警抑制）。</p>
     *
     * @param triggerReqVO 告警触发请求
     * @return 告警信息，如果被抑制则返回原告警信息
     */
    AlarmRespVO triggerAlarm(@Valid AlarmTriggerReqVO triggerReqVO);

    /**
     * 人工上报告警
     * 
     * <p>值班员手动上报告警，支持上传附件（最多5个，单个不超过10MB）。
     * 告警内容长度必须在10-500字之间（BR-VAL-003）。</p>
     *
     * @param reportReqVO 人工上报请求
     * @return 告警信息
     */
    AlarmRespVO reportAlarm(@Valid AlarmReportReqVO reportReqVO);

    // ========== 告警生命周期管理 ==========

    /**
     * 确认告警
     * 
     * <p>将告警状态从 PENDING 更新为 ACKNOWLEDGED。
     * 只有待确认状态的告警可以确认（BR-STA-001）。</p>
     *
     * @param id              告警ID
     * @param acknowledgeReqVO 确认请求
     */
    void acknowledgeAlarm(Long id, @Valid AlarmAcknowledgeReqVO acknowledgeReqVO);

    /**
     * 处理告警
     * 
     * <p>将告警状态从 ACKNOWLEDGED 更新为 HANDLING。
     * 只有已确认状态的告警可以处理（BR-STA-001）。
     * 处理时必须填写处理措施和处理结果。</p>
     *
     * @param id          告警ID
     * @param handleReqVO 处理请求
     */
    void handleAlarm(Long id, @Valid AlarmHandleReqVO handleReqVO);

    /**
     * 关闭告警
     * 
     * <p>将告警状态从 HANDLING 更新为 CLOSED。
     * 只有处理中状态的告警可以关闭（BR-STA-001）。
     * 关闭时必须填写关闭原因（BR-STA-002）。</p>
     *
     * @param id         告警ID
     * @param closeReqVO 关闭请求
     */
    void closeAlarm(Long id, @Valid AlarmCloseReqVO closeReqVO);

    // ========== 告警查询相关 ==========

    /**
     * 分页查询实时告警列表
     * 
     * <p>查询当前所有未关闭的告警，按创建时间倒序排列。</p>
     *
     * @param pageReqVO 分页查询条件
     * @return 实时告警分页结果
     */
    PageResult<AlarmRespVO> getRealTimeAlarmPage(AlarmPageReqVO pageReqVO);

    /**
     * 分页查询历史告警列表
     * 
     * <p>查询所有告警（包括已关闭），支持多条件筛选。
     * 时间范围不超过1年（BR-VAL-005）。</p>
     *
     * @param pageReqVO 分页查询条件
     * @return 历史告警分页结果
     */
    PageResult<AlarmRespVO> getHistoricalAlarmPage(AlarmPageReqVO pageReqVO);

    /**
     * 获取告警详情
     * 
     * <p>获取告警的完整信息，包括附件列表和联动执行记录。</p>
     *
     * @param id 告警ID
     * @return 告警详情
     */
    AlarmDetailRespVO getAlarmDetail(Long id);

    /**
     * 根据ID获取告警
     *
     * @param id 告警ID
     * @return 告警信息
     */
    AlarmDO getAlarm(Long id);

    /**
     * 根据告警编码获取告警
     *
     * @param code 告警编码
     * @return 告警信息
     */
    AlarmDO getAlarmByCode(String code);

    // ========== 告警抑制相关 ==========

    /**
     * 检查告警是否应该被抑制
     * 
     * <p>同一设备在5分钟内触发相同类型告警，后续告警应被抑制（BR-BIZ-001）。</p>
     *
     * @param deviceId    设备ID
     * @param alarmTypeId 告警类型ID
     * @return 如果应该被抑制返回原告警，否则返回 null
     */
    AlarmDO checkSuppression(Long deviceId, Long alarmTypeId);

    /**
     * 更新告警触发次数
     * 
     * <p>当告警被抑制时，更新原告警的触发次数。</p>
     *
     * @param alarmId 告警ID
     */
    void incrementTriggerCount(Long alarmId);

    // ========== 告警升级相关 ==========

    /**
     * 检查并升级超时未确认的告警
     * 
     * <p>告警未在规定时间内确认时，系统保持原有告警级别不变，
     * 但通过更强烈的界面闪烁和加大告警音量来提醒值班员（BR-BIZ-002）。</p>
     * 
     * <p>升级超时时间：
     * <ul>
     *   <li>信息级别：30分钟</li>
     *   <li>警告级别：15分钟</li>
     *   <li>严重级别：5分钟</li>
     *   <li>紧急级别：1分钟</li>
     * </ul>
     * </p>
     */
    void checkAndEscalateAlarms();

    // ========== 告警编码生成 ==========

    /**
     * 生成告警编码
     * 
     * <p>格式：ALM-YYYYMMDD-XXXXX，其中 XXXXX 为当天的序号。</p>
     *
     * @return 告警编码
     */
    String generateAlarmCode();

    // ========== 批量操作相关 ==========

    /**
     * 批量确认告警
     * 
     * <p>批量将多个告警状态从 PENDING 更新为 ACKNOWLEDGED。</p>
     *
     * @param ids              告警ID列表
     * @param acknowledgeReqVO 确认请求
     */
    void batchAcknowledgeAlarms(List<Long> ids, @Valid AlarmAcknowledgeReqVO acknowledgeReqVO);

    /**
     * 批量关闭告警
     * 
     * <p>批量将多个告警状态更新为 CLOSED。
     * 只有处理中状态的告警可以关闭。</p>
     *
     * @param ids        告警ID列表
     * @param closeReqVO 关闭请求
     */
    void batchCloseAlarms(List<Long> ids, @Valid AlarmCloseReqVO closeReqVO);

    // ========== 统计查询相关 ==========

    /**
     * 获取实时告警数量统计
     * 
     * <p>按告警级别统计当前未关闭的告警数量。</p>
     *
     * @return 各级别告警数量
     */
    AlarmCountByLevelVO getRealTimeAlarmCountByLevel();

    /**
     * 获取今日告警统计
     * 
     * <p>统计今日新增告警数量和已处理告警数量。</p>
     *
     * @return 今日告警统计
     */
    TodayAlarmStatisticsVO getTodayAlarmStatistics();

    /**
     * 告警数量统计 VO（按级别）
     */
    @lombok.Data
    class AlarmCountByLevelVO {
        /** 信息级别告警数量 */
        private Long infoCount;
        /** 警告级别告警数量 */
        private Long warningCount;
        /** 严重级别告警数量 */
        private Long criticalCount;
        /** 紧急级别告警数量 */
        private Long emergencyCount;
        /** 总数量 */
        private Long totalCount;
    }

    /**
     * 今日告警统计 VO
     */
    @lombok.Data
    class TodayAlarmStatisticsVO {
        /** 今日新增告警数量 */
        private Long newCount;
        /** 今日已处理告警数量 */
        private Long handledCount;
        /** 今日已关闭告警数量 */
        private Long closedCount;
        /** 当前活跃告警数量 */
        private Long activeCount;
    }

}
