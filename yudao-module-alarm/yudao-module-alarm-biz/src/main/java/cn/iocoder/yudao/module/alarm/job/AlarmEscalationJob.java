package cn.iocoder.yudao.module.alarm.job;

import cn.hutool.core.util.StrUtil;
import cn.cheers.x.framework.tenant.core.util.TenantUtils;
import cn.iocoder.yudao.module.alarm.service.alarm.AlarmService;
import com.xxl.job.core.handler.annotation.XxlJob;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 告警升级定时任务
 * 
 * <p>定时检查未确认的告警，对超时未确认的告警进行升级处理。</p>
 * 
 * <p>业务规则（BR-BIZ-002）：
 * 告警未在规定时间内确认时，系统保持原有告警级别不变，
 * 但通过更强烈的界面闪烁和加大告警音量来提醒值班员，
 * 同时可通知更高级别人员。
 * </p>
 * 
 * <p>升级超时时间：
 * <ul>
 *   <li>信息级别：30分钟</li>
 *   <li>警告级别：15分钟</li>
 *   <li>严重级别：5分钟</li>
 *   <li>紧急级别：1分钟</li>
 * </ul>
 * </p>
 * 
 * <p>需求引用：FR-003, BR-BIZ-002</p>
 * 
 * <p>使用说明：在 XXL-Job 管理平台配置任务时，需要传入租户ID作为参数。
 * 任务会在指定租户的上下文中执行告警升级检查。</p>
 *
 * @author 告警管理模块
 */
@Component
@Slf4j
public class AlarmEscalationJob {

    @Resource
    private AlarmService alarmService;

    /**
     * 定时检查并升级超时未确认的告警
     * 
     * <p>在 XXL-Job 管理平台配置任务时，需要传入租户ID作为参数（param）。
     * 例如：param = "1" 表示在租户1的上下文中执行。</p>
     * 
     * @param param 租户ID（必填）
     * @return 执行结果描述
     */
    @XxlJob("alarmEscalationJob")
    public String execute(String param) {
        // 1. 解析租户ID参数
        if (StrUtil.isBlank(param)) {
            log.error("[AlarmEscalationJob] 缺少租户ID参数");
            return "失败：缺少租户ID参数";
        }
        
        Long tenantId;
        try {
            tenantId = Long.parseLong(param.trim());
        } catch (NumberFormatException e) {
            log.error("[AlarmEscalationJob] 租户ID参数格式错误: {}", param);
            return "失败：租户ID参数格式错误";
        }

        log.info("[AlarmEscalationJob] 开始执行告警升级检查任务，租户ID: {}", tenantId);
        long startTime = System.currentTimeMillis();

        try {
            // 2. 在指定租户上下文中执行告警升级检查
            TenantUtils.execute(tenantId, () -> {
                alarmService.checkAndEscalateAlarms();
            });
            
            long duration = System.currentTimeMillis() - startTime;
            log.info("[AlarmEscalationJob] 告警升级检查任务执行完成，租户ID: {}，耗时 {} ms", tenantId, duration);
            return "成功：耗时 " + duration + " ms";
        } catch (Exception e) {
            log.error("[AlarmEscalationJob] 告警升级检查任务执行失败，租户ID: {}", tenantId, e);
            return "失败：" + e.getMessage();
        }
    }

}
