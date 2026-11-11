package cn.cheers.x.module.dynamicbusiness.service.ledgerstate;

/**
 * 当前页上的一条可执行转移。按钮文案用目标状态名。
 */
public record LedgerTransitionView(
        String toState,
        String toStateName,
        String requiredFields,
        String traceTemplate
) {
}
