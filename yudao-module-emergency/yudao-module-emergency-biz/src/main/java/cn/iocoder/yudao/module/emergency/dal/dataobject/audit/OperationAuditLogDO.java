package cn.iocoder.yudao.module.emergency.dal.dataobject.audit;

import cn.iocoder.yudao.module.emergency.dal.dataobject.EmergencyBaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * 用户操作审计日志 DO
 *
 * @author 芋道源码
 */
@TableName("emergency_operation_audit_log")
@KeySequence("emergency_operation_audit_log_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OperationAuditLogDO extends EmergencyBaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名称
     */
    private String userName;

    /**
     * 操作类型（CREATE创建/UPDATE更新/DELETE删除/QUERY查询/EXPORT导出/IMPORT导入等）
     */
    private String operationType;

    /**
     * 业务模块（event事件/response响应/task任务/plan预案/command指令/dispatch调度/statistics统计/report报送/organization组织）
     */
    private String businessModule;

    /**
     * 业务对象ID
     */
    private Long businessId;

    /**
     * 业务对象名称
     */
    private String businessName;

    /**
     * 操作内容
     */
    private String operationContent;

    /**
     * 操作前数据（JSON格式）
     */
    private String beforeData;

    /**
     * 操作后数据（JSON格式）
     */
    private String afterData;

    /**
     * 操作IP
     */
    private String operationIp;

    /**
     * 操作时间
     */
    private java.time.LocalDateTime operationTime;

    /**
     * 操作结果（SUCCESS成功/FAILURE失败）
     */
    private String operationResult;

    /**
     * 错误信息（操作失败时记录）
     */
    private String errorMessage;

    /**
     * 请求路径
     */
    private String requestPath;

    /**
     * 请求方法（GET/POST/PUT/DELETE等）
     */
    private String requestMethod;
}








