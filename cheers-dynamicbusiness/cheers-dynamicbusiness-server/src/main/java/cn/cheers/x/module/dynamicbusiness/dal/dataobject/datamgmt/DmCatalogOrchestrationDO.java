package cn.cheers.x.module.dynamicbusiness.dal.dataobject.datamgmt;

import cn.cheers.x.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

@TableName(value = "dm_catalog_orchestration", autoResultMap = true)
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DmCatalogOrchestrationDO extends TenantBaseDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String entityTypeCode;

    private Boolean enabled;

    /** 当前记录来源：LIST_ROW=点列表这一行；CATEGORY_NODE=点树上这个节点 */
    private String selectionSource;
}
