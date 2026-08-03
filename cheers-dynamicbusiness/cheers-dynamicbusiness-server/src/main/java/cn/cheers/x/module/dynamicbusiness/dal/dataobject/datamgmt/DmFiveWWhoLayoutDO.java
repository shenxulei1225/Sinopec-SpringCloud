package cn.cheers.x.module.dynamicbusiness.dal.dataobject.datamgmt;

import cn.cheers.x.framework.mybatis.core.type.JsonbMapTypeHandler;
import cn.cheers.x.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.*;

import java.util.List;
import java.util.Map;

@TableName(value = "dm_five_w_who_layout", autoResultMap = true)
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DmFiveWWhoLayoutDO extends TenantBaseDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String entityTypeCode;

    /** CATEGORY | MODEL | ENTITY */
    private String columnKind;

    private String slotRef;

    private String perspectiveId;

    private Long propsId;

    private Boolean enabled;

    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> contextOutputs;

    private String entityIdRule;

    @TableField(typeHandler = JsonbMapTypeHandler.class)
    private Map<String, Object> categoryColumn;
}
