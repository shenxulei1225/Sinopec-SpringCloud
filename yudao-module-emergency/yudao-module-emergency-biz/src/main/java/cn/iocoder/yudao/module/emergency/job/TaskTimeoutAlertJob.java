package cn.iocoder.yudao.module.emergency.job;

import cn.hutool.core.util.StrUtil;
import cn.cheers.x.framework.tenant.core.util.TenantUtils;
import cn.iocoder.yudao.module.emergency.service.alert.TaskTimeoutAlertService;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 任务超时告警定时任务（镜像指令步骤超时 Job）。
 *
 * <p>XXL-Job 配置：</p>
 * <ul>
 *   <li>JobHandler：{@code taskTimeoutAlertJob}</li>
 *   <li>任务参数：租户 ID，如 {@code 1}</li>
 *   <li>建议周期：每分钟</li>
 * </ul>
 */
@Component
@Slf4j
public class TaskTimeoutAlertJob {

    @Resource
    private TaskTimeoutAlertService taskTimeoutAlertService;

    @XxlJob("taskTimeoutAlertJob")
    public void execute() {
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

        TenantUtils.execute(tenantId, () -> {
            log.info("[taskTimeoutAlertJob] start tenantId={}", tenantId);
            taskTimeoutAlertService.checkAndProcessTimeoutAlerts();
            XxlJobHelper.handleSuccess("任务超时告警检测完成");
        });
    }
}
