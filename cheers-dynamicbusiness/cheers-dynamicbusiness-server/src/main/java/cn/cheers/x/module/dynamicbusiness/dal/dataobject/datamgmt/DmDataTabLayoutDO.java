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
 * 数据管理·数据 Tab 布局（分类 / 型号 / 实体 / 详情）。
 * 「模型管理」左侧分类栏见 dm_model_tab_category。
 */
@TableName(value = "dm_data_tab_layout", autoResultMap = true)
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DmDataTabLayoutDO extends TenantBaseDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String entityTypeCode;

    /** CATEGORY | MODEL | ENTITY | DETAIL */
    private String columnKind;

    private String perspectiveId;

    private Long propsId;

    private Boolean enabled;

    /** 「数据」Tab 分类列设置 */
    @TableField(typeHandler = JsonbMapTypeHandler.class)
    private Map<String, Object> categoryColumn;
}
