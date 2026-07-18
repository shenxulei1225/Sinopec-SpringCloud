package cn.cheers.x.alarm.service.linkage.executor;

import cn.hutool.core.util.StrUtil;
import cn.cheers.x.alarm.enums.LinkageActionTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 门禁控制执行器
 * 
 * <p>负责门禁设备联动控制，支持的操作：</p>
 * <ul>
 *   <li>OPEN - 开门</li>
 *   <li>CLOSE - 关门</li>
 *   <li>LOCK - 锁定</li>
 *   <li>UNLOCK - 解锁</li>
 * </ul>
 * 
 * <p>动作配置示例：</p>
 * <pre>
 * {
 *   "type": "ACCESS_CONTROL",
 *   "doorId": 1,
 *   "doorName": "门禁-B区2号入口",
 *   "action": "OPEN"
 * }
 * </pre>
 *
 * @author 告警管理模块
 */
@Slf4j
@Component
public class AccessControlExecutor extends AbstractLinkageActionExecutor {

    @Override
    public LinkageActionTypeEnum getActionType() {
        return LinkageActionTypeEnum.ACCESS_CONTROL;
    }

    @Override
    public int getPriority() {
        // 门禁控制优先级较高（仅次于消防）
        return 80;
    }

    @Override
    protected String validateConfig(LinkageActionContext context) {
        // 验证门禁ID
        Long doorId = context.getConfigLong("doorId");
        if (doorId == null) {
            return "门禁ID不能为空";
        }
        
        // 验证控制动作
        String action = context.getConfigString("action");
        if (StrUtil.isBlank(action)) {
            return "控制动作不能为空";
        }
        
        // 验证动作类型
        if (!isValidAction(action)) {
            return "不支持的控制动作：" + action;
        }
        
        return null;
    }

    @Override
    protected boolean doCanExecute(LinkageActionContext context) {
        // 检查门禁设备是否可用
        Long doorId = context.getConfigLong("doorId");
        if (doorId == null) {
            return false;
        }
        
        // TODO: 实际实现需要检查门禁设备状态
        return true;
    }

    @Override
    protected LinkageActionResult doExecute(LinkageActionContext context) {
        try {
            // 1. 解析配置
            Long doorId = context.getConfigLong("doorId");
            String doorName = context.getConfigString("doorName");
            String action = context.getConfigString("action");
            
            // 2. 执行门禁控制
            boolean success = executeAccessControl(doorId, doorName, action, context);
            
            // 3. 返回结果
            if (success) {
                String resultMessage = String.format("门禁控制成功：%s %s", 
                        doorName != null ? doorName : "门禁" + doorId,
                        getActionDescription(action));
                return LinkageActionResult.success(resultMessage, doorId, doorName);
            } else {
                return LinkageActionResult.failed("门禁控制失败");
            }
            
        } catch (Exception e) {
            log.error("[门禁控制执行器] 执行异常", e);
            return LinkageActionResult.failed("门禁控制异常：" + e.getMessage());
        }
    }

    @Override
    protected LinkageActionResult doDryRun(LinkageActionContext context) {
        Long doorId = context.getConfigLong("doorId");
        String doorName = context.getConfigString("doorName");
        String action = context.getConfigString("action");
        
        return LinkageActionResult.dryRunSuccess(
                String.format("模拟门禁控制：%s %s", 
                        doorName != null ? doorName : "门禁" + doorId,
                        getActionDescription(action)));
    }

    /**
     * 执行门禁控制
     *
     * @param doorId   门禁ID
     * @param doorName 门禁名称
     * @param action   控制动作
     * @param context  执行上下文
     * @return 是否执行成功
     */
    private boolean executeAccessControl(Long doorId, String doorName, 
                                         String action, LinkageActionContext context) {
        // TODO: 实际实现需要调用门禁控制服务
        // 这里预留接口，后续集成门禁控制系统
        
        log.info("[门禁控制执行器] 执行门禁控制: doorId={}, doorName={}, action={}", 
                doorId, doorName, action);
        
        // 模拟执行成功
        switch (action.toUpperCase()) {
            case "OPEN":
                log.info("[门禁控制执行器] 开门: {}", doorName);
                return true;
            case "CLOSE":
                log.info("[门禁控制执行器] 关门: {}", doorName);
                return true;
            case "LOCK":
                log.info("[门禁控制执行器] 锁定: {}", doorName);
                return true;
            case "UNLOCK":
                log.info("[门禁控制执行器] 解锁: {}", doorName);
                return true;
            default:
                log.warn("[门禁控制执行器] 不支持的控制动作: {}", action);
                return false;
        }
    }

    /**
     * 判断是否为有效的控制动作
     *
     * @param action 控制动作
     * @return 是否有效
     */
    private boolean isValidAction(String action) {
        if (StrUtil.isBlank(action)) {
            return false;
        }
        String upperAction = action.toUpperCase();
        return "OPEN".equals(upperAction) || 
               "CLOSE".equals(upperAction) || 
               "LOCK".equals(upperAction) ||
               "UNLOCK".equals(upperAction);
    }

    /**
     * 获取动作描述
     *
     * @param action 控制动作
     * @return 动作描述
     */
    private String getActionDescription(String action) {
        if (StrUtil.isBlank(action)) {
            return "未知操作";
        }
        switch (action.toUpperCase()) {
            case "OPEN":
                return "开门";
            case "CLOSE":
                return "关门";
            case "LOCK":
                return "锁定";
            case "UNLOCK":
                return "解锁";
            default:
                return action;
        }
    }

}
