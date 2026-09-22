package cn.cheers.x.inspection.task.service.execution.scheduleboard;

import cn.cheers.x.framework.tenant.core.context.TenantContextHolder;

/**
 * 到点自动开跑定时任务无 HTTP 登录态：在 inspection 进程内临时忽略租户，便于 Feign 调 runtime / dynamicbusiness RPC。
 * <p>被调服务侧须对 {@code /rpc-api/**} 配置 tenant ignore（见各模块 application.yaml）。
 */
public final class InspectionScheduledJobContext {

    private InspectionScheduledJobContext() {
    }

    public static void runIgnoringTenant(Runnable action) {
        Long previousTenant = TenantContextHolder.getTenantId();
        boolean previousIgnore = TenantContextHolder.isIgnore();
        try {
            TenantContextHolder.setIgnore(true);
            action.run();
        } finally {
            TenantContextHolder.setIgnore(previousIgnore);
            TenantContextHolder.setTenantId(previousTenant);
        }
    }
}
