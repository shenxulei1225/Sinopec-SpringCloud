package cn.iocoder.yudao.module.inspection.task.model.task;

import lombok.Data;

/**
 * 资源策略。
 *
 * <p>定义任务在调度时的资源选择与兜底规则。</p>
 */
@Data
public class ResourcePolicy {

    /**
     * 策略模式。
     *
     * <ul>
     *     <li>1 - 优先资源池</li>
     *     <li>2 - 指定资源</li>
     *     <li>3 - 允许切换</li>
     * </ul>
     */
    private Integer policyMode;

    /**
     * 优先资源池 ID。
     */
    private Long preferredPoolId;

    /**
     * 指定资源 ID。
     */
    private Long specifiedResourceId;

    /**
     * 指定资源编码。
     */
    private String specifiedResourceCode;

    /**
     * 是否允许切换。
     */
    private Boolean allowSwitch;

    /**
     * 切换条件描述。
     */
    private String switchCondition;

    /**
     * 兜底规则描述。
     */
    private String fallbackRule;
}
