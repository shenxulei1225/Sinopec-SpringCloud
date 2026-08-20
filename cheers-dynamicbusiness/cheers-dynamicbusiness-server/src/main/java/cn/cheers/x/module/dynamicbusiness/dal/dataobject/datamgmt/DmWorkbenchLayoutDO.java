package cn.cheers.x.module.dynamicbusiness.dal.dataobject.datamgmt;

import cn.cheers.x.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * 工作台布局头：模版（isTemplate=true）或从模版生成的实例。
 */
@TableName("dm_workbench_layout")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DmWorkbenchLayoutDO extends TenantBaseDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    /** true=跨类型版式模版；false=页面/数据页签引用的实例 */
    private Boolean isTemplate;

    /** 实例来源模版 id；模版行为空 */
    private Long sourceTemplateId;
}
