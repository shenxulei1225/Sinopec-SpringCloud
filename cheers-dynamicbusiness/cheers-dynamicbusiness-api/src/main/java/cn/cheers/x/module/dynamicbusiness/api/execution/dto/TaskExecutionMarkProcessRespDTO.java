package cn.cheers.x.module.dynamicbusiness.api.execution.dto;

import lombok.Data;

import java.util.Map;

/**
 * 改完一条已有事件后，把这一行现在的名称、状态和字段交还给调用方。
 * 页面通知用这一行发出。找不到这一条时 updated 为假，其余字段空着。
 */
@Data
public class TaskExecutionMarkProcessRespDTO {

    /** 这一行确实存在。状态没被回执改回去时也是真。 */
    private Boolean updated;

    private String messageKind;

    private String summary;

    private String eventStatus;

    private String outcome;

    private String msgId;

    /** 主题里的设备编号。这一行记下发时没带设备编号就空着。 */
    private String deviceId;

    private String direction;

    private Long receivedAtEpochMs;

    private Map<String, Object> fields;
}
