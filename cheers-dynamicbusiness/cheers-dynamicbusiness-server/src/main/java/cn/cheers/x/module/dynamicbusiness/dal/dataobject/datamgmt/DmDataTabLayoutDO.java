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
 * 工作台布局栏行（分类 / 型号 / 实体 / 详情），归属 layoutId（模版或实例）。
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

    /** 所属工作台布局（模版或实例） */
    private Long layoutId;

    /** 实例可带目录编码；模版行可空 */
    private String entityTypeCode;

    /** CATEGORY | MODEL | ENTITY | DETAIL */
    private String columnKind;

    /** 列顶标签页（就是 Tab）的编号：分类/型号/实体列上每一个 Tab；DETAIL 为空 */
    private String tabId;

    private Long propsId;

    private Boolean enabled;

    /** 列扩展 jsonb：按 columnKind 为分类 / 型号 / 实体各自扩展 */
    @TableField(typeHandler = JsonbMapTypeHandler.class)
    private Map<String, Object> columnMeta;
}
