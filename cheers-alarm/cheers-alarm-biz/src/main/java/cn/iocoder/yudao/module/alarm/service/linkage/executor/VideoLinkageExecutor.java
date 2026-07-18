package cn.iocoder.yudao.module.alarm.service.linkage.executor;

import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.module.alarm.enums.LinkageActionTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 视频联动执行器
 * 
 * <p>负责视频监控联动，支持的操作：</p>
 * <ul>
 *   <li>POPUP - 弹出视频窗口</li>
 *   <li>PRESET - 转到预置位</li>
 *   <li>RECORD - 开始录像</li>
 *   <li>SNAPSHOT - 抓拍图片</li>
 * </ul>
 * 
 * <p>动作配置示例：</p>
 * <pre>
 * {
 *   "type": "VIDEO_LINKAGE",
 *   "cameraId": 1,
 *   "cameraName": "摄像头-B区2号",
 *   "action": "POPUP",
 *   "preset": 1
 * }
 * </pre>
 *
 * @author 告警管理模块
 */
@Slf4j
@Component
public class VideoLinkageExecutor extends AbstractLinkageActionExecutor {

    @Override
    public LinkageActionTypeEnum getActionType() {
        return LinkageActionTypeEnum.VIDEO_LINKAGE;
    }

    @Override
    public int getPriority() {
        // 视频联动优先级中等
        return 40;
    }

    @Override
    protected String validateConfig(LinkageActionContext context) {
        // 验证摄像头ID
        Long cameraId = context.getConfigLong("cameraId");
        if (cameraId == null) {
            return "摄像头ID不能为空";
        }
        
        // 验证联动动作
        String action = context.getConfigString("action");
        if (StrUtil.isBlank(action)) {
            return "联动动作不能为空";
        }
        
        // 验证动作类型
        if (!isValidAction(action)) {
            return "不支持的联动动作：" + action;
        }
        
        // 如果是预置位动作，验证预置位号
        if ("PRESET".equalsIgnoreCase(action)) {
            Integer preset = context.getConfigInteger("preset");
            if (preset == null || preset < 1) {
                return "预置位号无效";
            }
        }
        
        return null;
    }

    @Override
    protected boolean doCanExecute(LinkageActionContext context) {
        // 检查摄像头是否可用
        Long cameraId = context.getConfigLong("cameraId");
        if (cameraId == null) {
            return false;
        }
        
        // TODO: 实际实现需要检查摄像头状态
        return true;
    }

    @Override
    protected LinkageActionResult doExecute(LinkageActionContext context) {
        try {
            // 1. 解析配置
            Long cameraId = context.getConfigLong("cameraId");
            String cameraName = context.getConfigString("cameraName");
            String action = context.getConfigString("action");
            Integer preset = context.getConfigInteger("preset");
            
            // 2. 执行视频联动
            boolean success = executeVideoLinkage(cameraId, cameraName, action, preset, context);
            
            // 3. 返回结果
            if (success) {
                String resultMessage = String.format("视频联动成功：%s %s", 
                        cameraName != null ? cameraName : "摄像头" + cameraId,
                        getActionDescription(action, preset));
                return LinkageActionResult.success(resultMessage, cameraId, cameraName);
            } else {
                return LinkageActionResult.failed("视频联动失败");
            }
            
        } catch (Exception e) {
            log.error("[视频联动执行器] 执行异常", e);
            return LinkageActionResult.failed("视频联动异常：" + e.getMessage());
        }
    }

    @Override
    protected LinkageActionResult doDryRun(LinkageActionContext context) {
        Long cameraId = context.getConfigLong("cameraId");
        String cameraName = context.getConfigString("cameraName");
        String action = context.getConfigString("action");
        Integer preset = context.getConfigInteger("preset");
        
        return LinkageActionResult.dryRunSuccess(
                String.format("模拟视频联动：%s %s", 
                        cameraName != null ? cameraName : "摄像头" + cameraId,
                        getActionDescription(action, preset)));
    }

    /**
     * 执行视频联动
     *
     * @param cameraId   摄像头ID
     * @param cameraName 摄像头名称
     * @param action     联动动作
     * @param preset     预置位号
     * @param context    执行上下文
     * @return 是否执行成功
     */
    private boolean executeVideoLinkage(Long cameraId, String cameraName, 
                                        String action, Integer preset,
                                        LinkageActionContext context) {
        // TODO: 实际实现需要调用视频监控服务
        // 这里预留接口，后续集成视频监控平台
        
        log.info("[视频联动执行器] 执行视频联动: cameraId={}, cameraName={}, action={}, preset={}", 
                cameraId, cameraName, action, preset);
        
        // 模拟执行成功
        switch (action.toUpperCase()) {
            case "POPUP":
                log.info("[视频联动执行器] 弹出视频窗口: {}", cameraName);
                return true;
            case "PRESET":
                log.info("[视频联动执行器] 转到预置位: {} -> 预置位{}", cameraName, preset);
                return true;
            case "RECORD":
                log.info("[视频联动执行器] 开始录像: {}", cameraName);
                return true;
            case "SNAPSHOT":
                log.info("[视频联动执行器] 抓拍图片: {}", cameraName);
                return true;
            default:
                log.warn("[视频联动执行器] 不支持的联动动作: {}", action);
                return false;
        }
    }

    /**
     * 判断是否为有效的联动动作
     *
     * @param action 联动动作
     * @return 是否有效
     */
    private boolean isValidAction(String action) {
        if (StrUtil.isBlank(action)) {
            return false;
        }
        String upperAction = action.toUpperCase();
        return "POPUP".equals(upperAction) || 
               "PRESET".equals(upperAction) || 
               "RECORD".equals(upperAction) ||
               "SNAPSHOT".equals(upperAction);
    }

    /**
     * 获取动作描述
     *
     * @param action 联动动作
     * @param preset 预置位号
     * @return 动作描述
     */
    private String getActionDescription(String action, Integer preset) {
        if (StrUtil.isBlank(action)) {
            return "未知操作";
        }
        switch (action.toUpperCase()) {
            case "POPUP":
                return "弹出视频窗口";
            case "PRESET":
                return "转到预置位" + (preset != null ? preset : "");
            case "RECORD":
                return "开始录像";
            case "SNAPSHOT":
                return "抓拍图片";
            default:
                return action;
        }
    }

}
