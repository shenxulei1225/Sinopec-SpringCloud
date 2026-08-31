package cn.cheers.x.inspection.task.dal.dataobject.task;

import cn.cheers.x.framework.mybatis.core.dataobject.BaseDO;
import cn.cheers.x.inspection.task.model.task.InspectionContent;
import cn.cheers.x.inspection.task.model.task.ExecutionDeviceBinding;
import cn.cheers.x.inspection.task.model.task.ResourcePolicy;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

/**
 * 巡检任务 DO。
 *
 * <p>主键由 PostgreSQL 序列 {@code inspection_task_seq} 经 {@link KeySequence} 分配；
 * 禁止依赖「数据齐全」才生成 id——草稿仅有任务名也必须能落库。</p>
 */
@TableName(value = "inspection_task", autoResultMap = true)
@KeySequence("inspection_task_seq")
@Data
@EqualsAndHashCode(callSuper = true)
public class InspectionTaskDO extends BaseDO {

    @TableId
    private Long id;

    /**
     * 父任务ID。
     */
    private Long parentId;

    /**
     * 分类ID。
     */
    private Long categoryId;

    /**
     * 任务编码。
     */
    private String taskCode;

    /**
     * 任务名称。
     */
    private String taskName;

    /**
     * 状态。
     */
    private Integer status;

    /**
     * 是否启用。
     */
    private Boolean enabled;

    /**
     * 备注。
     */
    private String remark;

    // ==================== 继承配置 ====================

    /**
     * 是否继承父任务排期。
     */
    private Boolean inheritParentSchedule;

    /**
     * 是否继承父任务资源策略。
     */
    private Boolean inheritParentResourcePolicy;

    // ==================== 任务内容 ====================

    /**
     * 巡检内容（JSON 存储）。
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private InspectionContent inspectionContent;

    // ==================== 排期配置 ====================

    /**
     * 排期需求ID。
     */
    private Long scheduleRequirementId;

    /**
     * 排期策略ID。
     */
    private Long schedulePolicyId;

    /**
     * 资源策略（JSON 存储）。
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private ResourcePolicy resourcePolicy;

    /**
     * 执行设备绑定（JSON）：设备业务 id + 对接协议编码 + 逻辑设备标识。
     * <p>绑设备时写入；开跑只读此字段，不查台账。
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private ExecutionDeviceBinding executionDeviceBinding;

    // ==================== 编排结果 ====================

    /**
     * 当前激活的编排批次ID。
     */
    private Long activePlanId;

    /**
     * 历史编排批次ID列表。
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<Long> planIds = new ArrayList<>();

    // ==================== 路线快照（确认后写入） ====================

    /**
     * 已确认路网引用。
     */
    private String networkRef;

    /**
     * 已确认规划路线（JSON）。
     */
    private String plannedRoute;

    /**
     * 已确认路线时长估算（分钟）。
     */
    private Integer durationEstimateMinutes;

    /**
     * 巡检类型：HUMAN|GROUND_ROBOT|UAV。
     */
    private String inspectionType;

    /**
     * 关联路线方案台账 ID。
     */
    private Long routePlanId;

    /**
     * 排期预占后的运行时作业 id（启用验窗 / 让路 / 恢复）。
     */
    private String runtimeJobId;

    /**
     * 设备侧运行态（任务会话权威）：IDLE / DISPATCHED / RUNNING / COMPLETED / FAULT。
     * <p>开跑下发成功 → DISPATCHED；上行任务状态/故障回写后续状态。不存瞬时连接。
     */
    private String deviceRunStatus;

    /**
     * 最近一次设备上行时间（epoch millis）。
     */
    private Long deviceLastUplinkAt;

    /**
     * 最近一次设备上行外层 opcode。
     * <p>过渡字段；执行会话权威迁至任务模块执行记录后收敛。
     */
    private Integer deviceLastUplinkOpcode;
}
