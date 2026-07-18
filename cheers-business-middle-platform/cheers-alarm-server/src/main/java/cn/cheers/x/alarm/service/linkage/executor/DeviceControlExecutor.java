package cn.cheers.x.alarm.service.linkage.executor;

import cn.hutool.core.util.StrUtil;
import cn.cheers.x.alarm.enums.LinkageActionTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 设备控制执行器
 * 
 * <p>负责控制风机、水泵等设备，支持的操作：</p>
 * <ul>
 *   <li>START - 启动设备</li>
 *   <li>STOP - 停止设备</li>
 *   <li>ADJUST - 调节设备（如调速）</li>
 * </ul>
 * 
 * <p>动作配置示例：</p>
 * <pre>
 * {
 *   "type": "DEVICE_CONTROL",
 *   "deviceId": 1,
 *   "deviceName": "潜水泵-B区2号",
 *   "action": "START",
 *   "params": {"power": "2.2kW"}
 * }
 * </pre>
 *
 * @author 告警管理模块
 */
@Slf4j
@Component
public class DeviceControlExecutor extends AbstractLinkageActionExecutor {

    @Override
    public LinkageActionTypeEnum getActionType() {
        return LinkageActionTypeEnum.DEVICE_CONTROL;
    }

    @Override
    public int getPriority() {
        // 设备控制优先级中等
        return 50;
    }

    @Override
    protected String validateConfig(LinkageActionContext context) {
        // 验证设备ID
        Long deviceId = context.getConfigLong("deviceId");
        if (deviceId == null) {
            return "设备ID不能为空";
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
        // 检查设备是否可用
        Long deviceId = context.getConfigLong("deviceId");
        if (deviceId == null) {
            return false;
        }
        
        // TODO: 实际实现需要检查设备状态
        // 这里预留接口，后续集成设备管理服务
        return true;
    }

    @Override
    protected LinkageActionResult doExecute(LinkageActionContext context) {
        try {
            // 1. 解析配置
            Long deviceId = context.getConfigLong("deviceId");
            String deviceName = context.getConfigString("deviceName");
            String action = context.getConfigString("action");
            
            // 2. 执行设备控制
            boolean success = executeDeviceControl(deviceId, deviceName, action, context);
            
            // 3. 返回结果
            if (success) {
                String resultMessage = String.format("设备控制成功：%s %s", 
                        deviceName != null ? deviceName : "设备" + deviceId,
                        getActionDescription(action));
                return LinkageActionResult.success(resultMessage, deviceId, deviceName);
            } else {
                return LinkageActionResult.failed("设备控制失败");
            }
            
        } catch (Exception e) {
            log.error("[设备控制执行器] 执行异常", e);
            return LinkageActionResult.failed("设备控制异常：" + e.getMessage());
        }
    }

    @Override
    protected LinkageActionResult doDryRun(LinkageActionContext context) {
        Long deviceId = context.getConfigLong("deviceId");
        String deviceName = context.getConfigString("deviceName");
        String action = context.getConfigString("action");
        
        return LinkageActionResult.dryRunSuccess(
                String.format("模拟设备控制：%s %s", 
                        deviceName != null ? deviceName : "设备" + deviceId,
                        getActionDescription(action)));
    }

    /**
     * 执行设备控制
     *
     * @param deviceId   设备ID
     * @param deviceName 设备名称
     * @param action     控制动作
     * @param context    执行上下文
     * @return 是否执行成功
     */
    private boolean executeDeviceControl(Long deviceId, String deviceName, 
                                         String action, LinkageActionContext context) {
        // TODO: 实际实现需要调用设备控制服务
        // 这里预留接口，后续集成设备控制协议（Modbus、OPC UA等）
        
        log.info("[设备控制执行器] 执行设备控制: deviceId={}, deviceName={}, action={}", 
                deviceId, deviceName, action);
        
        // 模拟执行成功
        switch (action.toUpperCase()) {
            case "START":
                log.info("[设备控制执行器] 启动设备: {}", deviceName);
                return true;
            case "STOP":
                log.info("[设备控制执行器] 停止设备: {}", deviceName);
                return true;
            case "ADJUST":
                log.info("[设备控制执行器] 调节设备: {}", deviceName);
                return true;
            default:
                log.warn("[设备控制执行器] 不支持的控制动作: {}", action);
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
        return "START".equals(upperAction) || 
               "STOP".equals(upperAction) || 
               "ADJUST".equals(upperAction);
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
            case "START":
                return "启动";
            case "STOP":
                return "停止";
            case "ADJUST":
                return "调节";
            default:
                return action;
        }
    }

}
