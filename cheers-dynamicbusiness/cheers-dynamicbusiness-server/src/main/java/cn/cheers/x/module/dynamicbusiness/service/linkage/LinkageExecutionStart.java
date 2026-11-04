package cn.cheers.x.module.dynamicbusiness.service.linkage;

/**
 * 联动执行幂等创建结果。
 */
public record LinkageExecutionStart(long executionId, boolean created) {
}
