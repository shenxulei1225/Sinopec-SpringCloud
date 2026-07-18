package cn.cheers.x.module.dynamicbusiness.dal.dataobject.dynamictable;

import cn.cheers.x.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 动态 SQL 执行审计日志 DO
 * 
 * <p>业务含义：记录所有动态 SQL 的执行情况，用于安全审计和问题排查。</p>
 * 
 * <p>记录内容包括：</p>
 * <ul>
 *   <li>执行的表名和操作类型</li>
 *   <li>完整的 SQL 语句和参数</li>
 *   <li>执行结果（成功/失败）</li>
 *   <li>影响的行数</li>
 *   <li>操作人信息</li>
 * </ul>
 * 
 * <p>业务规则：BR-STG-035</p>
 * 
 * @author yudao
 */
@TableName("dynamic_dynamic_sql_audit_log")
@KeySequence("dynamic_dynamic_sql_audit_log_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DynamicSqlAuditLogDO extends TenantBaseDO {

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 操作的表名
     */
    private String tableName;

    /**
     * 操作类型（SELECT/INSERT/UPDATE/DELETE/CREATE_TABLE/ALTER_TABLE）
     */
    private String operationType;

    /**
     * 执行的 SQL 语句
     */
    private String executedSql;

    /**
     * SQL 参数（JSON 格式）
     */
    private String parameters;

    /**
     * 是否执行成功
     */
    private Boolean success;

    /**
     * 错误信息（执行失败时记录）
     */
    private String errorMessage;

    /**
     * 影响的行数
     */
    private Integer affectedRows;

    /**
     * 执行时间
     */
    private LocalDateTime executionTime;

    /**
     * 操作人ID
     */
    private Long operatorId;

    /**
     * 操作人名称
     */
    private String operatorName;

    /**
     * 客户端 IP 地址
     */
    private String clientIp;

    /**
     * 请求 ID（用于关联请求链路）
     */
    private String requestId;
}
