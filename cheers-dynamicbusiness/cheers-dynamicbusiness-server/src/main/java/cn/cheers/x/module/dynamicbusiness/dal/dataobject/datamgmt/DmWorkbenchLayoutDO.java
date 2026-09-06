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
 * 工作台布局头：持有模版/实例身份与整段工作台设置。
 * settingsJson 是区段显隐等布局头设置的权威；栏行服务不得反向推导或补写。
 */
@TableName(value = "dm_workbench_layout", autoResultMap = true)
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

    /** 布局头设置 jsonb：sections（区域清单，含名字与摆法）与 sectionHidden（按区域编号隐藏） */
    @TableField(typeHandler = JsonbMapTypeHandler.class)
    private Map<String, Object> settingsJson;
}
