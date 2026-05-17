package cn.iocoder.yudao.module.alarm.service.linkage.executor;

import cn.iocoder.yudao.module.alarm.enums.LinkageActionTypeEnum;

/**
 * 联动动作执行器接口
 * 
 * <p>采用策略模式，支持多种联动动作类型的扩展</p>
 * 
 * <p>支持的动作类型：</p>
 * <ul>
 *   <li>NOTIFICATION - 发送通知</li>
 *   <li>DEVICE_CONTROL - 设备控制</li>
 *   <li>VIDEO_LINKAGE - 视频联动</li>
 *   <li>ACCESS_CONTROL - 门禁控制</li>
 *   <li>FIRE_CONTROL - 消防控制</li>
 *   <li>WORK_ORDER - 创建工单</li>
 *   <li>SCRIPT - 执行脚本</li>
 *   <li>API_CALL - 调用API</li>
 * </ul>
 *
 * @author 告警管理模块
 */
public interface LinkageActionExecutor {

    /**
     * 获取执行器支持的动作类型
     *
     * @return 动作类型枚举
     */
    LinkageActionTypeEnum getActionType();

    /**
     * 执行联动动作
     *
     * @param context 执行上下文
     * @return 执行结果
     */
    LinkageActionResult execute(LinkageActionContext context);

    /**
     * 检查是否可以执行
     * 
     * <p>在执行前检查前置条件，如设备是否可用、权限是否满足等</p>
     *
     * @param context 执行上下文
     * @return 是否可以执行
     */
    boolean canExecute(LinkageActionContext context);

    /**
     * 获取执行器优先级
     * 
     * <p>数值越大优先级越高，消防控制应具有最高优先级</p>
     *
     * @return 优先级
     */
    default int getPriority() {
        return 0;
    }

    /**
     * 是否支持模拟执行（干运行）
     * 
     * <p>用于测试联动规则时不实际控制设备</p>
     *
     * @return 是否支持模拟执行
     */
    default boolean supportsDryRun() {
        return true;
    }

}
