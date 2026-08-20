package cn.cheers.x.module.dynamicbusiness.dal.dataobject.datamgmt;

import cn.cheers.x.framework.mybatis.core.type.JsonbMapTypeHandler;
import cn.cheers.x.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.util.Map;

/**
 * 栏间关系声明：按工作台布局实例（layoutId）配置。
 */
@TableName(value = "dm_data_tab_column_relation", autoResultMap = true)
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DmDataTabColumnRelationDO extends TenantBaseDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long layoutId;

    /** 目录编码（便于排查；权威归属是 layoutId） */
    private String entityTypeCode;

    /** 本页内稳定 id */
    private String edgeId;

    private String fromColumnIdentity;

    private String toColumnIdentity;

    private String relationKind;

    private String fromTypeCode;

    private String toTypeCode;

    @TableField(typeHandler = JsonbMapTypeHandler.class)
    private Map<String, Object> relationMeta;
}
