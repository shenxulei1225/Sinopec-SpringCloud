package cn.iocoder.yudao.module.emergency.job;

import cn.hutool.core.util.StrUtil;
import cn.cheers.x.framework.tenant.core.util.TenantUtils;
import cn.iocoder.yudao.module.emergency.service.alert.CommandStepTimeoutAlertService;
import com.xxl.job.core.handler.annotation.XxlJob;
import com.xxl.job.core.context.XxlJobHelper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 指令步骤超时告警定时任务
 * 
 * <p>这是租户级任务，每个租户可以独立配置执行周期。</p>
 * <p>XXL-Job 任务创建时需要传入 tenant_id 参数。</p>
 * 
 * <h3>任务配置示例</h3>
 * <ul>
 *   <li>任务名称：commandStepTimeoutAlertJob</li>
 *   <li>任务参数：租户ID，如 "1"</li>
 *   <li>执行周期：由租户自行配置（建议每分钟）</li>
 * </ul>
 * 
 * @author yudao
 */
@Component
@Slf4j
public class CommandStepTimeoutAlertJob {

    @Resource
    private CommandStepTimeoutAlertService commandStepTimeoutAlertService;

    /**
     * 执行指令步骤超时告警检测任务
     */
    @XxlJob("commandStepTimeoutAlertJob")
    public void execute() {
        // 1. 获取租户 ID
        String param = XxlJobHelper.getJobParam();
        if (StrUtil.isBlank(param)) {
            XxlJobHelper.handleFail("缺少租户参数 tenant_id");
            return;
        }
        
        Long tenantId;
        try {
            tenantId = Long.parseLong(param.trim());
        } catch (NumberFormatException e) {
            XxlJobHelper.handleFail("租户参数格式错误: " + param);
            return;
        }
        
        // 2. 在租户上下文中执行
        TenantUtils.execute(tenantId, () -> {
            log.info("[commandStepTimeoutAlertJob][开始执行超时告警检测，tenantId={}]", tenantId);
            commandStepTimeoutAlertService.scheduledCheckTimeoutAlerts();
            XxlJobHelper.handleSuccess("超时告警检测任务执行完成");
        });
    }
}
