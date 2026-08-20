package cn.cheers.x.module.dynamicbusiness.dal.dataobject.datamgmt;

import cn.cheers.x.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * 页面 → 布局：layoutId=整页布局；dataLayoutId=页内嵌入数据工作台实例。
 */
@TableName("dm_page_layout_ref")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DmPageLayoutRefDO extends TenantBaseDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 页面身份键，如 view:{viewConfigId} */
    private String pageKey;

    private Long layoutId;

    /** 嵌入数据工作台所用布局实例；非嵌入可空 */
    private Long dataLayoutId;
}
