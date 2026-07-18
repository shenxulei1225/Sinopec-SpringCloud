package cn.cheers.x.alarm.service.alarm;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.cheers.x.framework.common.pojo.PageResult;
import cn.cheers.x.framework.common.util.object.BeanUtils;
import cn.cheers.x.alarm.controller.admin.vo.alarm.*;
import cn.cheers.x.alarm.controller.admin.vo.type.AlarmTypeCategoryVO;
import cn.cheers.x.alarm.controller.admin.vo.type.AlarmTypeEntityVO;
import cn.cheers.x.alarm.controller.admin.vo.type.AlarmTypeModelVO;
import cn.cheers.x.alarm.convert.AlarmConvert;
import cn.cheers.x.alarm.dal.dataobject.AlarmAttachmentDO;
import cn.cheers.x.alarm.dal.dataobject.AlarmDO;
import cn.cheers.x.alarm.dal.dataobject.LinkageExecutionDO;
import cn.cheers.x.alarm.dal.mysql.AlarmAttachmentMapper;
import cn.cheers.x.alarm.dal.mysql.AlarmMapper;
import cn.cheers.x.alarm.dal.mysql.LinkageExecutionMapper;
import cn.cheers.x.alarm.enums.AlarmCloseReasonEnum;
import cn.cheers.x.alarm.enums.AlarmHandleResultEnum;
import cn.cheers.x.alarm.enums.AlarmLevelEnum;
import cn.cheers.x.alarm.enums.AlarmSourceEnum;
import cn.cheers.x.alarm.enums.AlarmStatusEnum;
import cn.cheers.x.alarm.framework.cache.AlarmQueryCacheService;
import cn.cheers.x.alarm.service.audit.AlarmAuditService;
import cn.cheers.x.alarm.service.notify.NotificationService;
import cn.cheers.x.alarm.service.type.AlarmTypeService;
import cn.cheers.x.system.api.user.AdminUserApi;
import cn.cheers.x.system.api.user.dto.AdminUserRespDTO;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static cn.cheers.x.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.cheers.x.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;
import static cn.cheers.x.alarm.enums.ErrorCodeConstants.*;

/**
 * 告警核心服务实现类
 *
 * @author 告警管理模块
 */
@Service
@Validated
@Slf4j
public class AlarmServiceImpl implements AlarmService {

    /**
     * 告警抑制时间窗口（分钟）
     */
    private static final int SUPPRESSION_WINDOW_MINUTES = 5;

    /**
     * 告警编码前缀
     */
    private static final String ALARM_CODE_PREFIX = "ALM";

    /**
     * 告警编码序号 Redis Key 前缀
     */
    private static final String ALARM_CODE_SEQ_KEY_PREFIX = "alarm:code:seq:";

    @Resource
    private AlarmMapper alarmMapper;

    @Resource
    private AlarmAttachmentMapper alarmAttachmentMapper;

    @Resource
    private LinkageExecutionMapper linkageExecutionMapper;

    @Resource
    private AlarmTypeService alarmTypeService;

    @Resource
    private AlarmAuditService alarmAuditService;

    @Resource
    private AdminUserApi adminUserApi;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private NotificationService notificationService;

    @Resource
    private cn.cheers.x.alarm.service.websocket.AlarmWebSocketService alarmWebSocketService;

    @Resource
    private AlarmQueryCacheService alarmQueryCacheService;

    // ========== 告警触发相关 ==========

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AlarmRespVO triggerAlarm(AlarmTriggerReqVO triggerReqVO) {
        // 1. 校验告警类型是否存在
        validateAlarmTypeExists(triggerReqVO.getAlarmTypeId());
        
        // 2. 校验告警级别是否有效
        validateAlarmLevel(triggerReqVO.getAlarmLevel());

        // 3. 检查告警抑制
        AlarmDO existingAlarm = checkSuppression(triggerReqVO.getDeviceId(), triggerReqVO.getAlarmTypeId());
        if (existingAlarm != null) {
            // 告警被抑制，更新触发次数
            incrementTriggerCount(existingAlarm.getId());
            log.info("[triggerAlarm][告警被抑制] deviceId={}, alarmTypeId={}, existingAlarmId={}",
                    triggerReqVO.getDeviceId(), triggerReqVO.getAlarmTypeId(), existingAlarm.getId());
            return AlarmConvert.INSTANCE.convert(existingAlarm);
        }

        // 4. 获取告警类型信息
        AlarmTypeEntityVO alarmTypeEntity = alarmTypeService.getAlarmTypeEntity(triggerReqVO.getAlarmTypeId());
        String alarmTypePath = alarmTypeService.getAlarmTypePath(triggerReqVO.getAlarmTypeId());

        // 5. 创建告警
        AlarmDO alarm = buildAlarmDO(triggerReqVO, alarmTypeEntity, alarmTypePath);
        alarmMapper.insert(alarm);

        // 6. 记录审计日志（BR-BIZ-008）
        alarmAuditService.logAlarmCreate(alarm, getClientIpAddress());

        // 7. 推送新告警消息（FR-004：实时告警推送）
        alarmWebSocketService.pushNewAlarm(alarm);

        // 8. 清除相关缓存（性能优化：新告警会影响统计数据）
        alarmQueryCacheService.onAlarmStatusChange(alarm.getId());

        log.info("[triggerAlarm][告警触发成功] alarmId={}, alarmCode={}, deviceId={}, alarmTypeId={}",
                alarm.getId(), alarm.getAlarmCode(), triggerReqVO.getDeviceId(), triggerReqVO.getAlarmTypeId());

        return AlarmConvert.INSTANCE.convert(alarm);
    }

    /**
     * 构建告警 DO 对象（系统自动触发）
     */
    private AlarmDO buildAlarmDO(AlarmTriggerReqVO triggerReqVO, AlarmTypeEntityVO alarmTypeEntity, String alarmTypePath) {
        AlarmDO alarm = new AlarmDO();
        alarm.setAlarmCode(generateAlarmCode());
        alarm.setAlarmTypeId(triggerReqVO.getAlarmTypeId());
        alarm.setAlarmCategoryId(alarmTypeEntity != null ? alarmTypeEntity.getCategoryId() : null);
        alarm.setAlarmModelId(alarmTypeEntity != null ? alarmTypeEntity.getModelId() : null);
        alarm.setAlarmTypePath(alarmTypePath);
        alarm.setAlarmLevel(triggerReqVO.getAlarmLevel());
        alarm.setAlarmStatus(AlarmStatusEnum.PENDING.getStatus());
        alarm.setAlarmSource(AlarmSourceEnum.SYSTEM.getSource());
        alarm.setAlarmContent(triggerReqVO.getAlarmContent());
        alarm.setDeviceId(triggerReqVO.getDeviceId());
        alarm.setDeviceName(triggerReqVO.getDeviceName());
        alarm.setLocationId(triggerReqVO.getLocationId());
        alarm.setLocationName(triggerReqVO.getLocationName());
        alarm.setTriggerValue(triggerReqVO.getTriggerValue());
        alarm.setThresholdValue(triggerReqVO.getThresholdValue());
        alarm.setTriggerCount(1);
        alarm.setRuleId(triggerReqVO.getRuleId());
        alarm.setEscalationLevel(0);
        return alarm;
    }

    @Override
    public AlarmDO checkSuppression(Long deviceId, Long alarmTypeId) {
        if (deviceId == null || alarmTypeId == null) {
            return null;
        }
        // 查询5分钟内同设备同类型的活跃告警
        LocalDateTime startTime = LocalDateTime.now().minusMinutes(SUPPRESSION_WINDOW_MINUTES);
        return alarmMapper.selectRecentAlarm(deviceId, alarmTypeId, startTime);
    }

    @Override
    public void incrementTriggerCount(Long alarmId) {
        AlarmDO alarm = alarmMapper.selectById(alarmId);
        if (alarm != null) {
            alarm.setTriggerCount(alarm.getTriggerCount() + 1);
            alarmMapper.updateById(alarm);
        }
    }

    @Override
    public String generateAlarmCode() {
        // 格式：ALM-YYYYMMDD-XXXXX
        String dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String seqKey = ALARM_CODE_SEQ_KEY_PREFIX + dateStr;
        
        // 使用 Redis 自增获取序号
        Long seq = stringRedisTemplate.opsForValue().increment(seqKey);
        if (seq != null && seq == 1L) {
            // 第一次创建，设置过期时间为2天（确保跨天后仍能正常工作）
            stringRedisTemplate.expire(seqKey, 2, TimeUnit.DAYS);
        }
        
        // 格式化序号为5位数字
        String seqStr = String.format("%05d", seq != null ? seq : 1);
        return ALARM_CODE_PREFIX + "-" + dateStr + "-" + seqStr;
    }

    // ========== 校验方法 ==========

    /**
     * 校验告警类型是否存在
     */
    private void validateAlarmTypeExists(Long alarmTypeId) {
        if (alarmTypeId == null) {
            throw exception(ALARM_TYPE_NOT_EXISTS);
        }
        if (!alarmTypeService.existsAlarmType(alarmTypeId)) {
            throw exception(ALARM_TYPE_NOT_EXISTS);
        }
    }

    /**
     * 校验告警级别是否有效
     */
    private void validateAlarmLevel(String alarmLevel) {
        if (StrUtil.isBlank(alarmLevel)) {
            throw exception(ALARM_LEVEL_INVALID);
        }
        AlarmLevelEnum levelEnum = AlarmLevelEnum.getByLevel(alarmLevel);
        if (levelEnum == null) {
            throw exception(ALARM_LEVEL_INVALID);
        }
    }

    /**
     * 校验告警是否存在
     */
    private AlarmDO validateAlarmExists(Long id) {
        AlarmDO alarm = alarmMapper.selectById(id);
        if (alarm == null) {
            throw exception(ALARM_NOT_EXISTS);
        }
        return alarm;
    }

    /**
     * 校验告警是否已关闭
     */
    private void validateAlarmNotClosed(AlarmDO alarm) {
        if (AlarmStatusEnum.isClosed(alarm.getAlarmStatus())) {
            throw exception(ALARM_ALREADY_CLOSED);
        }
    }

    // ========== 子任务 7.3：人工上报 ==========

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AlarmRespVO reportAlarm(AlarmReportReqVO reportReqVO) {
        // 1. 校验告警类型是否存在
        validateAlarmTypeExists(reportReqVO.getAlarmTypeId());
        
        // 2. 校验告警级别是否有效
        validateAlarmLevel(reportReqVO.getAlarmLevel());
        
        // 3. 校验附件（BR-VAL-004：最多5个，单个不超过10MB）
        validateAttachments(reportReqVO.getAttachments());

        // 4. 获取告警类型信息
        AlarmTypeEntityVO alarmTypeEntity = alarmTypeService.getAlarmTypeEntity(reportReqVO.getAlarmTypeId());
        String alarmTypePath = alarmTypeService.getAlarmTypePath(reportReqVO.getAlarmTypeId());

        // 5. 创建告警
        AlarmDO alarm = buildAlarmDOFromReport(reportReqVO, alarmTypeEntity, alarmTypePath);
        alarmMapper.insert(alarm);

        // 6. 保存附件
        saveAttachments(alarm.getId(), reportReqVO.getAttachments());

        // 7. 记录审计日志（BR-BIZ-008）
        alarmAuditService.logAlarmCreate(alarm, getClientIpAddress());

        // 8. 推送新告警消息（FR-004：实时告警推送）
        alarmWebSocketService.pushNewAlarm(alarm);

        // 9. 清除相关缓存（性能优化：新告警会影响统计数据）
        alarmQueryCacheService.onAlarmStatusChange(alarm.getId());

        log.info("[reportAlarm][人工上报告警成功] alarmId={}, alarmCode={}, reportUserId={}",
                alarm.getId(), alarm.getAlarmCode(), getLoginUserId());

        return AlarmConvert.INSTANCE.convert(alarm);
    }

    /**
     * 构建告警 DO 对象（人工上报）
     */
    private AlarmDO buildAlarmDOFromReport(AlarmReportReqVO reportReqVO, AlarmTypeEntityVO alarmTypeEntity, String alarmTypePath) {
        AlarmDO alarm = AlarmConvert.INSTANCE.convert(reportReqVO);
        alarm.setAlarmCode(generateAlarmCode());
        alarm.setAlarmCategoryId(alarmTypeEntity != null ? alarmTypeEntity.getCategoryId() : null);
        alarm.setAlarmModelId(alarmTypeEntity != null ? alarmTypeEntity.getModelId() : null);
        alarm.setAlarmTypePath(alarmTypePath);
        alarm.setAlarmStatus(AlarmStatusEnum.PENDING.getStatus());
        alarm.setAlarmSource(AlarmSourceEnum.MANUAL.getSource());
        alarm.setTriggerCount(1);
        alarm.setEscalationLevel(0);
        // 设置设备名称和位置名称（可以通过设备服务和位置服务获取，这里暂时留空）
        // TODO: 调用设备服务获取设备名称
        // TODO: 调用位置服务获取位置名称
        return alarm;
    }

    /**
     * 校验附件（BR-VAL-004）
     * 
     * <p>业务规则：
     * <ul>
     *   <li>附件最多5个</li>
     *   <li>单个文件不超过10MB</li>
     *   <li>仅支持 jpg/png/mp4 格式</li>
     * </ul>
     * </p>
     */
    private void validateAttachments(List<AlarmReportReqVO.AttachmentVO> attachments) {
        if (CollUtil.isEmpty(attachments)) {
            return;
        }
        // 校验附件数量不超过5个
        if (attachments.size() > 5) {
            throw exception(ALARM_ATTACHMENT_COUNT_EXCEED);
        }
        // 校验每个附件
        long maxSize = 10 * 1024 * 1024L; // 10MB
        for (AlarmReportReqVO.AttachmentVO attachment : attachments) {
            // 校验附件大小不超过10MB
            if (attachment.getFileSize() != null && attachment.getFileSize() > maxSize) {
                throw exception(ALARM_ATTACHMENT_SIZE_EXCEED);
            }
            // 校验附件类型（仅支持 jpg/png/mp4 格式）
            validateAttachmentType(attachment.getFileName(), attachment.getFileType());
        }
    }

    /**
     * 校验附件类型
     * 
     * <p>仅支持 jpg/png/mp4 格式（BR-VAL-004）</p>
     */
    private void validateAttachmentType(String fileName, String fileType) {
        // 支持的文件扩展名
        String[] allowedExtensions = {"jpg", "jpeg", "png", "mp4"};
        // 支持的文件类型
        String[] allowedFileTypes = {"IMAGE", "VIDEO"};
        
        // 校验文件类型
        if (StrUtil.isNotBlank(fileType)) {
            boolean validFileType = false;
            for (String allowed : allowedFileTypes) {
                if (allowed.equalsIgnoreCase(fileType)) {
                    validFileType = true;
                    break;
                }
            }
            if (!validFileType) {
                throw exception(ALARM_ATTACHMENT_TYPE_INVALID);
            }
        }
        
        // 校验文件扩展名
        if (StrUtil.isNotBlank(fileName)) {
            String extension = getFileExtension(fileName);
            if (StrUtil.isNotBlank(extension)) {
                boolean validExtension = false;
                for (String allowed : allowedExtensions) {
                    if (allowed.equalsIgnoreCase(extension)) {
                        validExtension = true;
                        break;
                    }
                }
                if (!validExtension) {
                    throw exception(ALARM_ATTACHMENT_TYPE_INVALID);
                }
            }
        }
    }

    /**
     * 获取文件扩展名
     */
    private String getFileExtension(String fileName) {
        if (StrUtil.isBlank(fileName)) {
            return null;
        }
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex > 0 && lastDotIndex < fileName.length() - 1) {
            return fileName.substring(lastDotIndex + 1);
        }
        return null;
    }

    /**
     * 保存附件
     */
    private void saveAttachments(Long alarmId, List<AlarmReportReqVO.AttachmentVO> attachments) {
        if (CollUtil.isEmpty(attachments)) {
            return;
        }
        List<AlarmAttachmentDO> attachmentDOs = AlarmConvert.INSTANCE.convertAttachmentReqList(attachments);
        for (AlarmAttachmentDO attachmentDO : attachmentDOs) {
            attachmentDO.setAlarmId(alarmId);
            alarmAttachmentMapper.insert(attachmentDO);
        }
    }

    // ========== 子任务 7.4：生命周期管理 ==========

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void acknowledgeAlarm(Long id, AlarmAcknowledgeReqVO acknowledgeReqVO) {
        // 1. 校验告警是否存在
        AlarmDO alarm = validateAlarmExists(id);
        
        // 2. 校验告警是否已关闭
        validateAlarmNotClosed(alarm);
        
        // 3. 校验状态是否可以确认（BR-STA-001：只有待确认状态可以确认）
        if (!AlarmStatusEnum.canAcknowledge(alarm.getAlarmStatus())) {
            throw exception(ALARM_STATUS_CANNOT_ACKNOWLEDGE);
        }

        // 4. 获取当前用户信息
        Long userId = getLoginUserId();
        String userName = getUserName(userId);

        // 5. 更新告警状态
        alarm.setAlarmStatus(AlarmStatusEnum.ACKNOWLEDGED.getStatus());
        alarm.setAcknowledgeTime(LocalDateTime.now());
        alarm.setAcknowledgeUserId(userId);
        alarm.setAcknowledgeUserName(userName);
        alarm.setAcknowledgeRemark(acknowledgeReqVO != null ? acknowledgeReqVO.getAcknowledgeRemark() : null);
        alarmMapper.updateById(alarm);

        // 6. 记录审计日志（BR-BIZ-008）
        String remark = acknowledgeReqVO != null ? acknowledgeReqVO.getAcknowledgeRemark() : null;
        alarmAuditService.logAlarmAcknowledge(alarm, userId, userName, remark, getClientIpAddress());

        // 7. 推送告警状态更新消息（FR-004：告警状态更新推送）
        alarmWebSocketService.pushAlarmStatusUpdate(alarm, "ACKNOWLEDGE", userName, remark);

        // 8. 清除相关缓存（性能优化）
        alarmQueryCacheService.onAlarmStatusChange(id);

        log.info("[acknowledgeAlarm][告警确认成功] alarmId={}, userId={}", id, userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void handleAlarm(Long id, AlarmHandleReqVO handleReqVO) {
        // 1. 校验告警是否存在
        AlarmDO alarm = validateAlarmExists(id);
        
        // 2. 校验告警是否已关闭
        validateAlarmNotClosed(alarm);
        
        // 3. 校验状态是否可以处理（BR-STA-001：只有已确认状态可以处理）
        if (!AlarmStatusEnum.canHandle(alarm.getAlarmStatus())) {
            throw exception(ALARM_STATUS_CANNOT_HANDLE);
        }
        
        // 4. 校验处理结果是否有效
        validateHandleResult(handleReqVO.getHandleResult());

        // 5. 获取当前用户信息
        Long userId = getLoginUserId();
        String userName = getUserName(userId);

        // 6. 更新告警状态
        alarm.setAlarmStatus(AlarmStatusEnum.HANDLING.getStatus());
        alarm.setHandleTime(LocalDateTime.now());
        alarm.setHandleUserId(userId);
        alarm.setHandleUserName(userName);
        alarm.setHandleMeasure(handleReqVO.getHandleMeasure());
        alarm.setHandleResult(handleReqVO.getHandleResult());
        alarmMapper.updateById(alarm);

        // 7. 记录审计日志（BR-BIZ-008）
        alarmAuditService.logAlarmHandle(alarm, userId, userName, 
                handleReqVO.getHandleMeasure(), handleReqVO.getHandleResult(), getClientIpAddress());

        // 8. 推送告警状态更新消息（FR-004：告警状态更新推送）
        alarmWebSocketService.pushAlarmStatusUpdate(alarm, "HANDLE", userName, handleReqVO.getHandleMeasure());

        // 9. 清除相关缓存（性能优化）
        alarmQueryCacheService.onAlarmStatusChange(id);

        log.info("[handleAlarm][告警处理成功] alarmId={}, userId={}, handleResult={}", 
                id, userId, handleReqVO.getHandleResult());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void closeAlarm(Long id, AlarmCloseReqVO closeReqVO) {
        // 1. 校验告警是否存在
        AlarmDO alarm = validateAlarmExists(id);
        
        // 2. 校验告警是否已关闭
        validateAlarmNotClosed(alarm);
        
        // 3. 校验状态是否可以关闭（BR-STA-001：只有处理中状态可以关闭）
        if (!AlarmStatusEnum.canClose(alarm.getAlarmStatus())) {
            throw exception(ALARM_STATUS_CANNOT_CLOSE);
        }
        
        // 4. 校验关闭原因是否有效（BR-STA-002：关闭前必须填写关闭原因）
        validateCloseReason(closeReqVO.getCloseReason());

        // 5. 获取当前用户信息
        Long userId = getLoginUserId();
        String userName = getUserName(userId);

        // 6. 计算持续时长
        long durationSeconds = ChronoUnit.SECONDS.between(alarm.getCreateTime(), LocalDateTime.now());

        // 7. 更新告警状态
        alarm.setAlarmStatus(AlarmStatusEnum.CLOSED.getStatus());
        alarm.setCloseTime(LocalDateTime.now());
        alarm.setCloseUserId(userId);
        alarm.setCloseUserName(userName);
        alarm.setCloseReason(closeReqVO.getCloseReason());
        alarm.setCloseRemark(closeReqVO.getCloseRemark());
        alarm.setDurationSeconds(durationSeconds);
        alarmMapper.updateById(alarm);

        // 8. 记录审计日志（BR-BIZ-008）
        alarmAuditService.logAlarmClose(alarm, userId, userName, 
                closeReqVO.getCloseReason(), closeReqVO.getCloseRemark(), getClientIpAddress());

        // 9. 推送告警状态更新消息（FR-004：告警状态更新推送）
        alarmWebSocketService.pushAlarmStatusUpdate(alarm, "CLOSE", userName, closeReqVO.getCloseRemark());

        // 10. 清除相关缓存（性能优化）
        alarmQueryCacheService.onAlarmStatusChange(id);

        log.info("[closeAlarm][告警关闭成功] alarmId={}, userId={}, closeReason={}, durationSeconds={}", 
                id, userId, closeReqVO.getCloseReason(), durationSeconds);
    }

    /**
     * 校验处理结果是否有效
     */
    private void validateHandleResult(String handleResult) {
        if (StrUtil.isBlank(handleResult)) {
            throw exception(ALARM_HANDLE_RESULT_INVALID);
        }
        AlarmHandleResultEnum resultEnum = AlarmHandleResultEnum.getByResult(handleResult);
        if (resultEnum == null) {
            throw exception(ALARM_HANDLE_RESULT_INVALID);
        }
    }

    /**
     * 校验关闭原因是否有效
     */
    private void validateCloseReason(String closeReason) {
        if (StrUtil.isBlank(closeReason)) {
            throw exception(ALARM_CLOSE_REASON_REQUIRED);
        }
        AlarmCloseReasonEnum reasonEnum = AlarmCloseReasonEnum.getByReason(closeReason);
        if (reasonEnum == null) {
            throw exception(ALARM_CLOSE_REASON_INVALID);
        }
    }

    /**
     * 获取用户名称
     */
    private String getUserName(Long userId) {
        if (userId == null) {
            return null;
        }
        try {
            cn.cheers.x.framework.common.pojo.CommonResult<AdminUserRespDTO> result = adminUserApi.getUser(userId);
            if (result != null && result.isSuccess() && result.getData() != null) {
                return result.getData().getNickname();
            }
        } catch (Exception e) {
            log.warn("[getUserName][获取用户信息失败] userId={}, error={}", userId, e.getMessage());
        }
        return null;
    }

    /**
     * 获取客户端 IP 地址
     */
    private String getClientIpAddress() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                // 优先从 X-Forwarded-For 头获取（代理场景）
                String ip = request.getHeader("X-Forwarded-For");
                if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
                    ip = request.getHeader("Proxy-Client-IP");
                }
                if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
                    ip = request.getHeader("WL-Proxy-Client-IP");
                }
                if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
                    ip = request.getHeader("HTTP_CLIENT_IP");
                }
                if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
                    ip = request.getHeader("HTTP_X_FORWARDED_FOR");
                }
                if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
                    ip = request.getRemoteAddr();
                }
                // 如果是多个代理，取第一个 IP
                if (ip != null && ip.contains(",")) {
                    ip = ip.split(",")[0].trim();
                }
                return ip;
            }
        } catch (Exception e) {
            log.warn("[getClientIpAddress][获取客户端IP失败] error={}", e.getMessage());
        }
        return null;
    }

    // ========== 子任务 7.5：查询功能 ==========

    @Override
    public PageResult<AlarmRespVO> getRealTimeAlarmPage(AlarmPageReqVO pageReqVO) {
        // 1. 设置只查询实时告警（未关闭）
        pageReqVO.setRealTimeOnly(true);
        
        // 2. 执行分页查询
        PageResult<AlarmDO> pageResult = alarmMapper.selectPage(pageReqVO);
        
        // 3. 转换为响应 VO
        PageResult<AlarmRespVO> result = AlarmConvert.INSTANCE.convertPage(pageResult);
        
        // 4. 为实时告警动态计算持续时间（未关闭的告警 durationSeconds 为 null）
        if (CollUtil.isNotEmpty(result.getList())) {
            LocalDateTime now = LocalDateTime.now();
            for (AlarmRespVO alarmVO : result.getList()) {
                if (alarmVO.getDurationSeconds() == null && alarmVO.getCreateTime() != null) {
                    alarmVO.setDurationSeconds(ChronoUnit.SECONDS.between(alarmVO.getCreateTime(), now));
                }
            }
        }
        
        log.debug("[getRealTimeAlarmPage][查询实时告警] total={}, pageNo={}, pageSize={}", 
                result.getTotal(), pageReqVO.getPageNo(), pageReqVO.getPageSize());
        
        return result;
    }

    @Override
    public PageResult<AlarmRespVO> getHistoricalAlarmPage(AlarmPageReqVO pageReqVO) {
        // 1. 校验时间范围（BR-VAL-005：历史告警查询时间范围不超过1年）
        validateTimeRange(pageReqVO.getCreateTimeStart(), pageReqVO.getCreateTimeEnd());
        
        // 2. 查询所有告警（包括已关闭）
        pageReqVO.setRealTimeOnly(false);
        PageResult<AlarmDO> pageResult = alarmMapper.selectPage(pageReqVO);
        
        // 3. 转换为响应 VO
        PageResult<AlarmRespVO> result = AlarmConvert.INSTANCE.convertPage(pageResult);
        
        // 4. 为未关闭的告警动态计算持续时间
        if (CollUtil.isNotEmpty(result.getList())) {
            LocalDateTime now = LocalDateTime.now();
            for (AlarmRespVO alarmVO : result.getList()) {
                if (alarmVO.getDurationSeconds() == null && alarmVO.getCreateTime() != null) {
                    alarmVO.setDurationSeconds(ChronoUnit.SECONDS.between(alarmVO.getCreateTime(), now));
                }
            }
        }
        
        log.debug("[getHistoricalAlarmPage][查询历史告警] total={}, pageNo={}, pageSize={}", 
                result.getTotal(), pageReqVO.getPageNo(), pageReqVO.getPageSize());
        
        return result;
    }

    /**
     * 校验时间范围（BR-VAL-005：历史告警查询时间范围不超过1年）
     */
    private void validateTimeRange(LocalDateTime startTime, LocalDateTime endTime) {
        if (startTime != null && endTime != null) {
            long daysBetween = ChronoUnit.DAYS.between(startTime, endTime);
            if (daysBetween > 365) {
                throw exception(ALARM_QUERY_TIME_RANGE_EXCEED);
            }
        }
    }

    @Override
    public AlarmDetailRespVO getAlarmDetail(Long id) {
        // 1. 尝试从缓存获取告警详情（性能优化）
        AlarmDetailRespVO cachedDetail = alarmQueryCacheService.getAlarmDetailCache(id);
        if (cachedDetail != null) {
            // 为未关闭的告警动态计算持续时间
            if (cachedDetail.getDurationSeconds() == null && cachedDetail.getCreateTime() != null) {
                cachedDetail.setDurationSeconds(ChronoUnit.SECONDS.between(cachedDetail.getCreateTime(), LocalDateTime.now()));
            }
            log.debug("[getAlarmDetail][从缓存获取告警详情] alarmId={}", id);
            return cachedDetail;
        }
        
        // 2. 查询告警基本信息
        AlarmDO alarm = validateAlarmExists(id);
        
        // 3. 转换为详情 VO
        AlarmDetailRespVO detailVO = AlarmConvert.INSTANCE.convertDetail(alarm);
        
        // 4. 为未关闭的告警动态计算持续时间
        if (detailVO.getDurationSeconds() == null && detailVO.getCreateTime() != null) {
            detailVO.setDurationSeconds(ChronoUnit.SECONDS.between(detailVO.getCreateTime(), LocalDateTime.now()));
        }
        
        // 5. 查询附件列表
        List<AlarmAttachmentDO> attachments = alarmAttachmentMapper.selectListByAlarmId(id);
        detailVO.setAttachments(AlarmConvert.INSTANCE.convertAttachmentList(attachments));
        
        // 6. 查询联动执行记录
        List<LinkageExecutionDO> linkageExecutions = linkageExecutionMapper.selectListByAlarmId(id);
        detailVO.setLinkageExecutions(AlarmConvert.INSTANCE.convertLinkageExecutionSimpleList(linkageExecutions));
        
        // 7. 设置缓存（性能优化）
        alarmQueryCacheService.setAlarmDetailCache(id, detailVO);
        
        log.debug("[getAlarmDetail][查询告警详情] alarmId={}, alarmCode={}", id, alarm.getAlarmCode());
        
        return detailVO;
    }

    @Override
    public AlarmDO getAlarm(Long id) {
        return alarmMapper.selectById(id);
    }

    @Override
    public AlarmDO getAlarmByCode(String code) {
        if (StrUtil.isBlank(code)) {
            return null;
        }
        return alarmMapper.selectByAlarmCode(code);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void checkAndEscalateAlarms() {
        log.info("[checkAndEscalateAlarms] 开始检查需要升级的告警");
        
        int escalatedCount = 0;
        LocalDateTime now = LocalDateTime.now();
        
        // 遍历所有告警级别，检查超时未确认的告警
        for (AlarmLevelEnum levelEnum : AlarmLevelEnum.values()) {
            // 计算该级别的超时时间点
            int timeoutMinutes = levelEnum.getEscalationTimeoutMinutes();
            LocalDateTime timeoutThreshold = now.minusMinutes(timeoutMinutes);
            
            // 查询该级别下超时未确认且未升级的告警
            List<AlarmDO> alarmsToEscalate = alarmMapper.selectList(
                    new cn.cheers.x.framework.mybatis.core.query.LambdaQueryWrapperX<AlarmDO>()
                            .eq(AlarmDO::getAlarmStatus, AlarmStatusEnum.PENDING.getStatus())
                            .eq(AlarmDO::getAlarmLevel, levelEnum.getLevel())
                            .le(AlarmDO::getCreateTime, timeoutThreshold)
                            .eq(AlarmDO::getEscalationLevel, 0) // 只处理未升级的告警
            );
            
            if (alarmsToEscalate.isEmpty()) {
                continue;
            }
            
            log.info("[checkAndEscalateAlarms] 发现 {} 条 {} 级别告警需要升级", 
                    alarmsToEscalate.size(), levelEnum.getName());
            
            // 对每条告警进行升级处理
            for (AlarmDO alarm : alarmsToEscalate) {
                try {
                    escalateAlarm(alarm);
                    escalatedCount++;
                } catch (Exception e) {
                    log.error("[checkAndEscalateAlarms] 告警升级失败: alarmId={}, error={}", 
                            alarm.getId(), e.getMessage());
                }
            }
        }
        
        log.info("[checkAndEscalateAlarms] 告警升级检查完成，共升级 {} 条告警", escalatedCount);
    }

    /**
     * 升级单条告警
     * 
     * <p>业务规则（BR-BIZ-002）：
     * 告警升级时保持原有告警级别不变，但通过更强烈的界面闪烁和加大告警音量来提醒值班员，
     * 同时通知更高级别人员。</p>
     *
     * @param alarm 需要升级的告警
     */
    private void escalateAlarm(AlarmDO alarm) {
        log.info("[escalateAlarm] 开始升级告警: alarmId={}, alarmCode={}, level={}", 
                alarm.getId(), alarm.getAlarmCode(), alarm.getAlarmLevel());
        
        // 1. 更新告警升级状态
        alarm.setEscalationLevel(alarm.getEscalationLevel() + 1);
        alarm.setEscalationTime(LocalDateTime.now());
        alarmMapper.updateById(alarm);
        
        // 2. 记录审计日志（BR-BIZ-008）
        alarmAuditService.logAlarmEscalate(alarm);
        
        // 3. 发送升级通知（BR-BIZ-002：通知更高级别人员）
        try {
            notificationService.sendEscalationNotification(alarm);
        } catch (Exception e) {
            log.error("[escalateAlarm] 发送升级通知失败: alarmId={}, error={}", 
                    alarm.getId(), e.getMessage());
        }
        
        // 4. 推送告警升级消息（FR-004：告警升级推送）
        alarmWebSocketService.pushAlarmEscalation(alarm);
        
        log.info("[escalateAlarm] 告警升级完成: alarmId={}, escalationLevel={}", 
                alarm.getId(), alarm.getEscalationLevel());
    }

    // ========== 批量操作相关 ==========

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchAcknowledgeAlarms(List<Long> ids, AlarmAcknowledgeReqVO acknowledgeReqVO) {
        if (CollUtil.isEmpty(ids)) {
            return;
        }
        for (Long id : ids) {
            try {
                acknowledgeAlarm(id, acknowledgeReqVO);
            } catch (Exception e) {
                log.warn("[batchAcknowledgeAlarms][批量确认告警失败] alarmId={}, error={}", id, e.getMessage());
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchCloseAlarms(List<Long> ids, AlarmCloseReqVO closeReqVO) {
        if (CollUtil.isEmpty(ids)) {
            return;
        }
        for (Long id : ids) {
            try {
                closeAlarm(id, closeReqVO);
            } catch (Exception e) {
                log.warn("[batchCloseAlarms][批量关闭告警失败] alarmId={}, error={}", id, e.getMessage());
            }
        }
    }

    // ========== 统计查询相关 ==========

    @Override
    public AlarmCountByLevelVO getRealTimeAlarmCountByLevel() {
        AlarmCountByLevelVO result = new AlarmCountByLevelVO();
        
        // 查询各级别的实时告警数量
        result.setInfoCount(alarmMapper.countRealTimeAlarmByLevel(AlarmLevelEnum.INFO.getLevel()));
        result.setWarningCount(alarmMapper.countRealTimeAlarmByLevel(AlarmLevelEnum.WARNING.getLevel()));
        result.setCriticalCount(alarmMapper.countRealTimeAlarmByLevel(AlarmLevelEnum.CRITICAL.getLevel()));
        result.setEmergencyCount(alarmMapper.countRealTimeAlarmByLevel(AlarmLevelEnum.EMERGENCY.getLevel()));
        
        // 计算总数
        result.setTotalCount(result.getInfoCount() + result.getWarningCount() 
                + result.getCriticalCount() + result.getEmergencyCount());
        
        return result;
    }

    @Override
    public TodayAlarmStatisticsVO getTodayAlarmStatistics() {
        TodayAlarmStatisticsVO result = new TodayAlarmStatisticsVO();
        
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        LocalDateTime todayEnd = todayStart.plusDays(1);
        
        // 今日新增告警数量
        result.setNewCount(alarmMapper.countByCreateTimeBetween(todayStart, todayEnd));
        
        // 今日已处理告警数量（状态为 HANDLING 或 CLOSED）
        result.setHandledCount(alarmMapper.countHandledByTimeBetween(todayStart, todayEnd));
        
        // 今日已关闭告警数量
        result.setClosedCount(alarmMapper.countClosedByTimeBetween(todayStart, todayEnd));
        
        // 当前活跃告警数量（未关闭）
        result.setActiveCount(alarmMapper.countActiveAlarms());
        
        return result;
    }

}
