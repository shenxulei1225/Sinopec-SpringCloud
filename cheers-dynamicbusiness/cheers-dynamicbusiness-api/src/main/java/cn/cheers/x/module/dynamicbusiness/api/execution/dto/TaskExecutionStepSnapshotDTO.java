package cn.cheers.x.module.dynamicbusiness.api.execution.dto;

import lombok.Data;

import java.util.Map;

/**
 * 一次执行账上的一步，给巡检在拍照上报后找到同检查项的视频分析步。
 * <p>source 是生成任务时冻结的动作归属、识别能力和协议参与方式。
 * <p>不负责写入，也不根据步骤标题推断动作归属。
 */
@Data
public class TaskExecutionStepSnapshotDTO {

    private Long id;
    private String stepCode;
    private String stepTitle;
    private String stepStatus;
    private Long parentId;
    private Map<String, Object> source;
    private Map<String, Object> resultPayload;
}
