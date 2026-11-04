package cn.cheers.x.module.dynamicbusiness.service.action.execution;

import java.util.Map;

/**
 * 调用方提交给动作执行能力的标准请求。
 */
public record ActionExecutionRequest(
        String idempotencyKey,
        String callerType,
        String callerReference,
        long facilityId,
        long actionId,
        String targetEntityTypeCode,
        long targetEntityId,
        Map<String, Object> parameters
) {
}
