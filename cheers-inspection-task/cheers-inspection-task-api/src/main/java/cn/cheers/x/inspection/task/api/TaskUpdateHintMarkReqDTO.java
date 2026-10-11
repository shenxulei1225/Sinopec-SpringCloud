package cn.cheers.x.inspection.task.api;

import lombok.Data;

/**
 * 动作库或检查项刚保存的前后内容。
 * 巡检任务服务据此给用到这次改动的已生成任务记下「需要更新」。
 */
@Data
public class TaskUpdateHintMarkReqDTO {

    /** action 或 inspection_item */
    private String entityTypeCode;

    private Long entityId;

    private String code;

    private String name;

    /** 保存前的参数清单或步骤树，JSON 文本 */
    private String beforeJson;

    /** 保存后的参数清单或步骤树，JSON 文本 */
    private String afterJson;
}
