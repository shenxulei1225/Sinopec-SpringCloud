package cn.cheers.x.module.dynamicbusiness.dal.dataobject.dynamictable;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import java.time.LocalDateTime;

/**
 * 动态表审计日志 DO
 * 
 * <p>记录动态表的所有结构变更操作，包括创建表、添加列、废弃列等。</p>
 * 
 * <p>业务规则：BR-STG-032 - 所有表结构变更必须记录审计日志</p>
 * 
 * @author 基础服务模块
 */
@TableName("dynamic_dynamic_table_audit_log")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DynamicTableAuditLogDO extends TenantBaseDO {
    
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 关联的动态表ID
     */
    private Long dynamicTableId;
    
    /**
     * 操作类型（CREATE_TABLE/ADD_COLUMN/MODIFY_COLUMN/DROP_COLUMN/DROP_TABLE/ALTER_TABLE_ADD_COLUMN/ALTER_TABLE_DEPRECATE_COLUMN）
     */
    private String operationType;
    
    /**
     * 操作描述
     */
    private String operationDesc;
    
    /**
     * 变更前的配置（JSON格式）
     */
    private String beforeConfig;
    
    /**
     * 变更后的配置（JSON格式）
     */
    private String afterConfig;
    
    /**
     * 执行的SQL语句
     */
    private String executedSql;
    
    /**
     * 执行结果（SUCCESS/FAILED）
     */
    private String executeResult;
    
    /**
     * 错误信息
     */
    private String errorMessage;
    
    /**
     * 操作时间
     */
    private LocalDateTime operationTime;
    
    /**
     * 操作人ID
     */
    private Long operatorId;
    
    /**
     * 操作人名称
     */
    private String operatorName;
}
