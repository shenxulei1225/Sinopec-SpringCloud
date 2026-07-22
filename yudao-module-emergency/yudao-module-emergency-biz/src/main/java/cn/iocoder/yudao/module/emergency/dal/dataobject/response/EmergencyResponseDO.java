package cn.iocoder.yudao.module.emergency.dal.dataobject.response;

import cn.iocoder.yudao.module.emergency.dal.dataobject.EmergencyBaseDO;
import cn.iocoder.yudao.module.emergency.framework.mybatis.PostgreSQLJsonbTypeHandler;
import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.util.Map;

/**
 * 应急响应
 */
@TableName(value = "emergency_response", autoResultMap = true)
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmergencyResponseDO extends EmergencyBaseDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 响应编号（唯一）
     */
    @TableField("response_no")
    private String responseNo;

    @TableField("event_id")
    private Long eventId;

    /**
     * 事件名称
     */
    @TableField("event_name")
    private String eventName;

    @TableField("response_level")
    private String responseLevel;

    @TableField("plan_id")
    private Long planId;

    /**
     * 预案名称
     */
    @TableField("plan_name")
    private String planName;

    /**
     * 启动信息（JSONB）
     */
    @TableField(value = "start_info", typeHandler = PostgreSQLJsonbTypeHandler.class, insertStrategy = FieldStrategy.NOT_NULL)
    private Map<String, Object> startInfo;

    /**
     * 执行信息（JSONB）
     */
    @TableField(value = "execution_info", typeHandler = PostgreSQLJsonbTypeHandler.class)
    private Map<String, Object> executionInfo;

    /**
     * 跟踪信息（JSONB）
     */
    @TableField(value = "tracking_info", typeHandler = PostgreSQLJsonbTypeHandler.class)
    private Map<String, Object> trackingInfo;

    /**
     * 结束信息（JSONB）
     */
    @TableField(value = "end_info", typeHandler = PostgreSQLJsonbTypeHandler.class)
    private Map<String, Object> endInfo;

    /**
     * 升级信息（JSONB）
     */
    @TableField(value = "upgrade_info", typeHandler = PostgreSQLJsonbTypeHandler.class)
    private Map<String, Object> upgradeInfo;

    /**
     * 取消信息（JSONB）
     */
    @TableField(value = "cancel_info", typeHandler = PostgreSQLJsonbTypeHandler.class)
    private Map<String, Object> cancelInfo;

    /**
     * 历史记录（JSONB）
     */
    @TableField(typeHandler = PostgreSQLJsonbTypeHandler.class)
    private Map<String, Object> history;

    /**
     * 协调信息（JSONB）
     */
    @TableField(value = "coordination_info", typeHandler = PostgreSQLJsonbTypeHandler.class)
    private Map<String, Object> coordinationInfo;

    /**
     * 响应状态：pending/executing/tracking/ended/cancelled
     */
    private String status;
}
