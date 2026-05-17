package cn.iocoder.yudao.module.inspection.task.dal.dataobject.schedule;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 巡检任务计划点资源分配 DO。
 *
 * <p>表示某个计划点分配的具体资源。</p>
 *
 * <p>一张计划点可以分配多个资源（如机器人1+机器人2同时巡检，资源1+资源2协同执行）。</p>
 *
 * <p>分配方式枚举：</p>
 * <ul>
 *     <li>1-固定分配：由规则中指定的固定资源</li>
 *     <li>2-资源池自动分配：智能调度从资源池中自动选择</li>
 *     <li>3-人工指定：用户手动选择的资源</li>
 *     <li>4-重排替换：重排时替换的新资源</li>
 * </ul>
 */
@TableName("inspection_task_schedule_resource")
@Data
@EqualsAndHashCode(callSuper = true)
public class InspectionTaskScheduleResourceDO extends BaseDO {

    /**
     * 主键 ID。
     */
    @TableId
    private Long id;

    /**
     * 计划点 ID。
     *
     * <p>关联 {@link InspectionTaskScheduleDO}。</p>
     */
    private Long scheduleId;

    /**
     * 资源类型。
     *
     * <p>例如：robot（巡检机器人）、vehicle（车辆）、drone（无人机）等。</p>
     */
    private String resourceType;

    /**
     * 资源 ID。
     */
    private Long resourceId;

    /**
     * 资源编码。
     */
    private String resourceCode;

    /**
     * 资源名称。
     */
    private String resourceName;

    /**
     * 是否为主资源。
     *
     * <p>同一计划点可能有多个资源，标识其中主次关系。</p>
     */
    private Boolean isPrimary;

    /**
     * 分配方式。
     *
     * <p>枚举：1-固定分配、2-资源池自动分配、3-人工指定、4-重排替换</p>
     */
    private Integer assignType;

    /**
     * 分配开始时间。
     */
    private LocalDateTime assignStartTime;

    /**
     * 分配结束时间。
     */
    private LocalDateTime assignEndTime;
}
