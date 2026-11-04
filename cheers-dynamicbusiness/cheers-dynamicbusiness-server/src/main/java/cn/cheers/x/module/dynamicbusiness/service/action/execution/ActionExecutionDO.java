package cn.cheers.x.module.dynamicbusiness.service.action.execution;

import cn.cheers.x.framework.mybatis.core.type.JsonbMapTypeHandler;
import cn.cheers.x.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 实际动作调用记录。动作定义仍在 action 数据目录，本表只记录运行事实。
 */
@TableName(value = "dynamic_action_execution", autoResultMap = true)
@Data
@EqualsAndHashCode(callSuper = true)
public class ActionExecutionDO extends TenantBaseDO {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String idempotencyKey;
    private String callerType;
    private String callerReference;
    private Long facilityId;
    private Long actionId;
    private String targetEntityTypeCode;
    private Long targetEntityId;
    @TableField(typeHandler = JsonbMapTypeHandler.class)
    private Map<String, Object> parameters;
    private String status;
    private Integer attempts;
    private LocalDateTime availableAt;
    private LocalDateTime startedAt;
    private LocalDateTime finishedAt;
    private String errorCode;
    private String errorMessage;
}
