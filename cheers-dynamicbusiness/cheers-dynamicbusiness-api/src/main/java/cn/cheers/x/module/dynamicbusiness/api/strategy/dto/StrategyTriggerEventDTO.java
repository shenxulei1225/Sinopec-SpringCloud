package cn.cheers.x.module.dynamicbusiness.api.strategy.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 策略运行时收到的一次事情：哪类事件、带哪些已点名的数。
 * <p>不负责拆报文。没有账本编号时由策略条件决定是否命中，运行时不猜。
 */
@Data
public class StrategyTriggerEventDTO {

    /** 采集结果到了 */
    public static final String EVENT_COLLECTION_RECEIVED = "COLLECTION_RECEIVED";

    /** 人点了开始 / 到点了 */
    public static final String EVENT_EXECUTION_START = "EXECUTION_START";

    @NotBlank
    private String eventType;

    private Long executionRecordId;

    private String entityTypeCode;

    private Long receivedAtEpochMs;

    private String messageKind;

    private String protocolQualify;

    private List<String> qualifyErrors = new ArrayList<>();

    private String summary;

    private Map<String, Object> fields = new LinkedHashMap<>();

    /** 是哪条任务；人点了开始时必填 */
    private Long taskDefinitionId;

    /** 本次执行对应的 L4 计划点 id；写入执行账 pending_execution_id */
    private String scheduleSlotId;

    private String modelCode;

    private String executionName;

    private Object standardSnapshot;

    private List<Map<String, Object>> steps = new ArrayList<>();

    private String protocolVersion;

    private String logicalDeviceId;

    private List<Map<String, Object>> dispatchActions = new ArrayList<>();

    /** pending / in_progress / completed / fault */
    private String executionStatus;

    private List<Map<String, Object>> stepUpdates = new ArrayList<>();

    private Long alarmId;

    private List<Long> recipientUserIds = new ArrayList<>();
}
