package cn.cheers.x.inspection.task.api.dto;

import lombok.Data;

import java.util.Map;

/**
 * 已生成待执行账上的一步动作。监控下发时交给协议网关按对照填进指令包。
 * <p>不负责：自己编操作码或包内序号。
 */
@Data
public class PatrolGeneratedTaskActionDTO {

    private Long actionId;

    private String actionCode;

    private Map<String, Object> params;
}
