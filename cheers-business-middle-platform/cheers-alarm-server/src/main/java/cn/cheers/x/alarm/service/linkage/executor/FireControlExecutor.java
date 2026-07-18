package cn.cheers.x.alarm.service.linkage.executor;

import cn.hutool.core.collection.CollUtil;
import cn.cheers.x.alarm.enums.LinkageActionTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 消防控制执行器
 * 
 * <p>负责消防设施联动控制，具有最高优先级</p>
 * 
 * <p>支持的消防设备：</p>
 * <ul>
 *   <li>SPRINKLER - 喷淋系统</li>
 *   <li>EXHAUST_FAN - 排烟系统</li>
 *   <li>ALARM_BELL - 声光报警</li>
 *   <li>FIRE_DOOR - 防火门</li>
 *   <li>VALVE - 可燃物输送阀门</li>
 * </ul>
 * 
 * <p>支持的操作：</p>
 * <ul>
 *   <li>START - 启动</li>
 *   <li>STOP - 停止</li>
 *   <li>OPEN - 打开</li>
 *   <li>CLOSE - 关闭</li>
 * </ul>
 * 
 * <p>动作配置示例：</p>
 * <pre>
 * {
 *   "type": "FIRE_CONTROL",
 *   "actions": [
 *     {"device": "SPRINKLER", "action": "START"},
 *     {"device": "EXHAUST_FAN", "action": "START"},
 *     {"device": "ALARM_BELL", "action": "START"},
 *     {"device": "VALVE", "action": "CLOSE"}
 *   ]
 * }
 * </pre>
 * 
 * <p>业务规则：BR-BIZ-004 消防设施控制必须优先于其他联动动作执行</p>
 *
 * @author 告警管理模块
 */
@Slf4j
@Component
public class FireControlExecutor extends AbstractLinkageActionExecutor {

    @Override
    public LinkageActionTypeEnum getActionType() {
        return LinkageActionTypeEnum.FIRE_CONTROL;
    }

    @Override
    public int getPriority() {
        // 消防控制具有最高优先级
        return 100;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected String validateConfig(LinkageActionContext context) {
        // 验证消防动作配置
        Object actionsObj = context.getActionConfigMap().get("actions");
        if (actionsObj == null) {
            return "消防动作配置不能为空";
        }
        
        if (!(actionsObj instanceof List)) {
            return "消防动作配置格式错误";
        }
        
        List<Map<String, Object>> actions = (List<Map<String, Object>>) actionsObj;
        if (CollUtil.isEmpty(actions)) {
            return "消防动作列表不能为空";
        }
        
        // 验证每个动作配置
        for (Map<String, Object> action : actions) {
            String device = (String) action.get("device");
            String actionType = (String) action.get("action");
            
            if (device == null || actionType == null) {
                return "消防动作配置缺少必要字段（device/action）";
            }
            
            if (!isValidDevice(device)) {
                return "不支持的消防设备类型：" + device;
            }
            
            if (!isValidAction(actionType)) {
                return "不支持的消防控制动作：" + actionType;
            }
        }
        
        return null;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected LinkageActionResult doExecute(LinkageActionContext context) {
        try {
            // 1. 解析配置
            List<Map<String, Object>> actions = 
                    (List<Map<String, Object>>) context.getActionConfigMap().get("actions");
            
            // 2. 执行消防控制
            int successCount = 0;
            int failCount = 0;
            StringBuilder resultBuilder = new StringBuilder();
            
            for (Map<String, Object> action : actions) {
                String device = (String) action.get("device");
                String actionType = (String) action.get("action");
                Long deviceId = action.get("deviceId") != null ? 
                        Long.parseLong(action.get("deviceId").toString()) : null;
                
                try {
                    boolean success = executeFireControl(device, actionType, deviceId, context);
                    if (success) {
                        successCount++;
                        resultBuilder.append(getDeviceName(device)).append(":成功 ");
                    } else {
                        failCount++;
                        resultBuilder.append(getDeviceName(device)).append(":失败 ");
                    }
                } catch (Exception e) {
                    failCount++;
                    resultBuilder.append(getDeviceName(device)).append(":异常 ");
                    log.error("[消防控制执行器] 执行失败: device={}, action={}, error={}", 
                            device, actionType, e.getMessage());
                }
            }
            
            // 3. 返回结果
            if (failCount == 0) {
                return LinkageActionResult.success(
                        String.format("消防联动执行成功，共%d个设备", successCount));
            } else if (successCount > 0) {
                // 部分成功，仍然标记为成功，但记录详情
                return LinkageActionResult.success(
                        String.format("消防联动部分成功：%s", resultBuilder.toString().trim()));
            } else {
                // 全部失败，需要人工介入
                return LinkageActionResult.failedWithManualIntervention(
                        "消防联动全部失败，需要人工介入", true);
            }
            
        } catch (Exception e) {
            log.error("[消防控制执行器] 执行异常", e);
            return LinkageActionResult.failedWithManualIntervention(
                    "消防联动异常：" + e.getMessage(), true);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    protected LinkageActionResult doDryRun(LinkageActionContext context) {
        List<Map<String, Object>> actions = 
                (List<Map<String, Object>>) context.getActionConfigMap().get("actions");
        
        StringBuilder sb = new StringBuilder("模拟消防联动：");
        for (Map<String, Object> action : actions) {
            String device = (String) action.get("device");
            String actionType = (String) action.get("action");
            sb.append(getDeviceName(device)).append("-").append(getActionName(actionType)).append(" ");
        }
        
        return LinkageActionResult.dryRunSuccess(sb.toString().trim());
    }

    /**
     * 执行消防控制
     *
     * @param device    设备类型
     * @param action    控制动作
     * @param deviceId  设备ID
     * @param context   执行上下文
     * @return 是否执行成功
     */
    private boolean executeFireControl(String device, String action, 
                                       Long deviceId, LinkageActionContext context) {
        // TODO: 实际实现需要调用消防控制服务
        // 这里预留接口，后续集成消防控制系统
        
        log.info("[消防控制执行器] 执行消防控制: device={}, action={}, deviceId={}", 
                device, action, deviceId);
        
        // 模拟执行成功
        switch (device.toUpperCase()) {
            case "SPRINKLER":
                log.info("[消防控制执行器] 喷淋系统 {}", getActionName(action));
                return true;
            case "EXHAUST_FAN":
                log.info("[消防控制执行器] 排烟系统 {}", getActionName(action));
                return true;
            case "ALARM_BELL":
                log.info("[消防控制执行器] 声光报警 {}", getActionName(action));
                return true;
            case "FIRE_DOOR":
                log.info("[消防控制执行器] 防火门 {}", getActionName(action));
                return true;
            case "VALVE":
                log.info("[消防控制执行器] 可燃物阀门 {}", getActionName(action));
                return true;
            default:
                log.warn("[消防控制执行器] 不支持的消防设备: {}", device);
                return false;
        }
    }

    /**
     * 判断是否为有效的消防设备类型
     *
     * @param device 设备类型
     * @return 是否有效
     */
    private boolean isValidDevice(String device) {
        if (device == null) {
            return false;
        }
        String upperDevice = device.toUpperCase();
        return "SPRINKLER".equals(upperDevice) || 
               "EXHAUST_FAN".equals(upperDevice) || 
               "ALARM_BELL".equals(upperDevice) ||
               "FIRE_DOOR".equals(upperDevice) ||
               "VALVE".equals(upperDevice);
    }

    /**
     * 判断是否为有效的控制动作
     *
     * @param action 控制动作
     * @return 是否有效
     */
    private boolean isValidAction(String action) {
        if (action == null) {
            return false;
        }
        String upperAction = action.toUpperCase();
        return "START".equals(upperAction) || 
               "STOP".equals(upperAction) || 
               "OPEN".equals(upperAction) ||
               "CLOSE".equals(upperAction);
    }

    /**
     * 获取设备名称
     *
     * @param device 设备类型
     * @return 设备名称
     */
    private String getDeviceName(String device) {
        if (device == null) {
            return "未知设备";
        }
        switch (device.toUpperCase()) {
            case "SPRINKLER":
                return "喷淋系统";
            case "EXHAUST_FAN":
                return "排烟系统";
            case "ALARM_BELL":
                return "声光报警";
            case "FIRE_DOOR":
                return "防火门";
            case "VALVE":
                return "可燃物阀门";
            default:
                return device;
        }
    }

    /**
     * 获取动作名称
     *
     * @param action 控制动作
     * @return 动作名称
     */
    private String getActionName(String action) {
        if (action == null) {
            return "未知操作";
        }
        switch (action.toUpperCase()) {
            case "START":
                return "启动";
            case "STOP":
                return "停止";
            case "OPEN":
                return "打开";
            case "CLOSE":
                return "关闭";
            default:
                return action;
        }
    }

}
