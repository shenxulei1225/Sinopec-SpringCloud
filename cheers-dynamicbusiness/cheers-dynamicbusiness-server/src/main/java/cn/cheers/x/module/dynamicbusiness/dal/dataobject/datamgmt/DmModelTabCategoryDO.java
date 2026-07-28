package cn.cheers.x.module.dynamicbusiness.dal.dataobject.datamgmt;

import cn.cheers.x.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * 「模型管理」Tab 左侧分类栏配置（按数据类型一行，与数据 Tab 布局分离）。
 */
@TableName("dm_model_tab_category")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DmModelTabCategoryDO extends TenantBaseDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String entityTypeCode;

    private Boolean enabled;

    private String label;

    private String categoryTypeCode;

    private Long propsId;
}
