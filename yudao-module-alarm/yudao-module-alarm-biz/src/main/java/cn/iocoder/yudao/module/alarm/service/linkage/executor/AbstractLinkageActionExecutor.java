package cn.iocoder.yudao.module.alarm.service.linkage.executor;

import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.module.alarm.enums.LinkageActionTypeEnum;
import lombok.extern.slf4j.Slf4j;

/**
 * 联动动作执行器抽象基类
 * 
 * <p>提供通用的执行逻辑和模板方法</p>
 *
 * @author 告警管理模块
 */
@Slf4j
public abstract class AbstractLinkageActionExecutor implements LinkageActionExecutor {

    @Override
    public LinkageActionResult execute(LinkageActionContext context) {
        long startTime = System.currentTimeMillis();
        
        try {
            // 1. 前置检查
            if (!canExecute(context)) {
                return LinkageActionResult.failed("执行条件不满足", false);
            }
            
            // 2. 参数验证
            String validationError = validateConfig(context);
            if (StrUtil.isNotBlank(validationError)) {
                return LinkageActionResult.failed("配置验证失败：" + validationError, false);
            }
            
            // 3. 执行动作
            LinkageActionResult result;
            if (context.isDryRun()) {
                result = doDryRun(context);
            } else {
                result = doExecute(context);
            }
            
            // 4. 设置执行耗时
            result.setExecutionTimeMs(System.currentTimeMillis() - startTime);
            
            // 5. 记录日志
            if (result.isSuccess()) {
                log.info("[{}] 联动动作执行成功: alarmId={}, actionType={}, result={}", 
                        getActionType().getName(), 
                        context.getAlarm() != null ? context.getAlarm().getId() : null,
                        getActionType().getType(),
                        result.getResultMessage());
            } else {
                log.warn("[{}] 联动动作执行失败: alarmId={}, actionType={}, error={}", 
                        getActionType().getName(),
                        context.getAlarm() != null ? context.getAlarm().getId() : null,
                        getActionType().getType(),
                        result.getErrorMessage());
            }
            
            return result;
            
        } catch (Exception e) {
            log.error("[{}] 联动动作执行异常: alarmId={}, actionType={}", 
                    getActionType().getName(),
                    context.getAlarm() != null ? context.getAlarm().getId() : null,
                    getActionType().getType(), e);
            
            LinkageActionResult result = LinkageActionResult.failed("执行异常：" + e.getMessage());
            result.setExecutionTimeMs(System.currentTimeMillis() - startTime);
            return result;
        }
    }

    @Override
    public boolean canExecute(LinkageActionContext context) {
        // 默认实现：检查基本条件
        if (context == null) {
            return false;
        }
        if (StrUtil.isBlank(context.getActionConfig()) && context.getActionConfigMap() == null) {
            return false;
        }
        return doCanExecute(context);
    }

    /**
     * 验证动作配置
     * 
     * <p>子类可以重写此方法进行特定的配置验证</p>
     *
     * @param context 执行上下文
     * @return 验证错误信息，为空表示验证通过
     */
    protected String validateConfig(LinkageActionContext context) {
        return null;
    }

    /**
     * 执行联动动作（模板方法）
     * 
     * <p>子类必须实现此方法</p>
     *
     * @param context 执行上下文
     * @return 执行结果
     */
    protected abstract LinkageActionResult doExecute(LinkageActionContext context);

    /**
     * 模拟执行联动动作
     * 
     * <p>默认实现返回模拟成功，子类可以重写</p>
     *
     * @param context 执行上下文
     * @return 模拟执行结果
     */
    protected LinkageActionResult doDryRun(LinkageActionContext context) {
        return LinkageActionResult.dryRunSuccess(getActionType().getName() + " 模拟执行成功");
    }

    /**
     * 检查是否可以执行（子类实现）
     * 
     * <p>默认返回true，子类可以重写进行特定检查</p>
     *
     * @param context 执行上下文
     * @return 是否可以执行
     */
    protected boolean doCanExecute(LinkageActionContext context) {
        return true;
    }

    /**
     * 获取动作类型名称
     *
     * @return 动作类型名称
     */
    protected String getActionTypeName() {
        return getActionType().getName();
    }

}
