package cn.cheers.x.module.dynamicbusiness.service.action.execution;

/**
 * 动作执行能力受理一次调用后的立即回执。
 */
public record ActionExecutionReceipt(long actionExecutionId, String status) {
}
