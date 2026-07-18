package cn.cheers.x.module.dynamicbusiness.dal.dataobject.dynamictable;

import cn.cheers.x.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * 动态表字段配置 DO
 * 
 * <p>用于记录动态表中的字段配置信息，包括字段与数据库列的映射关系。</p>
 * 
 * @author 基础服务模块
 */
@TableName("dynamic_dynamic_table_column")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DynamicTableColumnDO extends TenantBaseDO {
    
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
     * 关联的字段ID
     */
    private Long fieldId;
    
    /**
     * 物理列名
     */
    private String columnName;
    
    /**
     * 数据库数据类型
     */
    private String dataType;
    
    /**
     * 是否可空
     */
    private Boolean nullable;
    
    /**
     * 默认值
     */
    private String defaultValue;
    
    /**
     * 列注释
     */
    private String columnComment;
    
    /**
     * 排序顺序
     */
    private Integer sortOrder;
    
    /**
     * 状态（1-正常，0-已删除）
     */
    private Integer status;
}
