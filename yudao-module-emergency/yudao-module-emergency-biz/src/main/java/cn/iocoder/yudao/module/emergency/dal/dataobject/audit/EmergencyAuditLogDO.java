package cn.iocoder.yudao.module.emergency.dal.dataobject.audit;

import cn.cheers.x.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 应急管理系统审计日志
 */
@TableName(value = "emergency_audit_log", autoResultMap = true)
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmergencyAuditLogDO extends TenantBaseDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 关联的事件ID
     */
    private Long eventId;

    /**
     * 关联的响应ID
     */
    private Long responseId;

    /**
     * 关联的任务ID
     */
    private Long taskId;

    /**
     * 操作类型：create/update/delete/confirm/assess/start_response/close等
     */
    private String operationType;

    /**
     * 操作模块：event/response/task/resource/plan/command等
     */
    private String operationModule;

    /**
     * 操作内容描述
     */
    private String operationContent;

    /**
     * 操作人ID
     */
    private Long operatorId;

    /**
     * 操作人姓名（快照）
     */
    private String operatorName;

    /**
     * 操作时间
     */
    private LocalDateTime operationTime;

    /**
     * 操作结果：success/failure
     */
    private String operationResult;

    /**
     * 错误信息（操作失败时）
     */
    private String errorMessage;

    /**
     * 请求IP地址
     */
    private String requestIp;

    /**
     * 用户代理信息
     */
    private String userAgent;

    /**
     * 额外信息（JSON格式）
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> extraInfo;
}



