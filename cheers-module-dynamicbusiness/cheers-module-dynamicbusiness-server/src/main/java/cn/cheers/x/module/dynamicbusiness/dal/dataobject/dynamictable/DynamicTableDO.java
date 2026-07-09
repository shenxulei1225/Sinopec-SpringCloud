package cn.cheers.x.module.dynamicbusiness.dal.dataobject.dynamictable;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import java.time.LocalDateTime;

/**
 * 动态表配置 DO
 * 
 * <p>用于记录 DEDICATED 存储类型的动态创建表信息。</p>
 * 
 * <p>业务规则：</p>
 * <ul>
 *   <li>BR-STG-030: 动态表必须包含基础字段</li>
 *   <li>BR-STG-031: 固定列字段添加后不能物理删除，只能标记为废弃</li>
 *   <li>BR-STG-032: 所有表结构变更必须记录审计日志</li>
 * </ul>
 * 
 * @author 基础服务模块
 */
@TableName("dynamic_dynamic_table")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DynamicTableDO extends TenantBaseDO {
    
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 关联的业务模型ID
     */
    private Long modelId;
    
    /**
     * 业务类型编码
     */
    private String entityTypeCode;
    
    /**
     * 物理表名
     */
    private String tableName;
    
    /**
     * 表描述
     */
    private String tableComment;
    
    /**
     * 字段配置（JSON格式，用于快速查询表结构）
     */
    private String columnConfig;
    
    /**
     * 表状态（1-正常，0-已删除，2-迁移中）
     */
    private Integer status;
    
    /**
     * 当前版本号（每次表结构变更时递增）
     */
    private Integer version;
    
    /**
     * 最后同步时间
     */
    private LocalDateTime lastSyncTime;
}
