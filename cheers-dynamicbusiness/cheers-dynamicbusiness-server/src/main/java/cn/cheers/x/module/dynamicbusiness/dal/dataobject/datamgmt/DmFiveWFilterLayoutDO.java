package cn.cheers.x.module.dynamicbusiness.dal.dataobject.datamgmt;

import cn.cheers.x.framework.mybatis.core.type.JsonbListStringTypeHandler;
import cn.cheers.x.framework.mybatis.core.type.JsonbMapTypeHandler;
import cn.cheers.x.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.util.List;
import java.util.Map;

@TableName(value = "dm_five_w_filter_layout", autoResultMap = true)
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DmFiveWFilterLayoutDO extends TenantBaseDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String entityTypeCode;

    /** 筛选槽种类：CATEGORY | MODEL | ENTITY */
    private String columnKind;

    private String slotRef;

    private String perspectiveId;

    private Long propsId;

    private Boolean enabled;

    @TableField(typeHandler = JsonbListStringTypeHandler.class)
    private List<String> contextOutputs;

    /** 分类即实体时为 categoryLinkedEntity，保证点分类只同步一条 Who 实体 */
    private String entityIdRule;

    @TableField(typeHandler = JsonbMapTypeHandler.class)
    private Map<String, Object> categoryColumn;
}
