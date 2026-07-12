package cn.cheers.x.module.dynamicbusiness.dal.dataobject.business;

import cn.iocoder.yudao.framework.mybatis.core.type.JsonbMapTypeHandler;
import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Map;

@TableName(value = "dynamic_business_entry", autoResultMap = true)
@KeySequence("dynamic_business_entry_id_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BusinessEntryDO extends TenantBaseDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long businessId;

    private String code;

    private String name;

    /** ENTITY_ADMIN / SCOPED_LIST / DASHBOARD / EXTERNAL */
    private String entryType;

    /** 可选绑定的实体类型编码 entityTypeCode */
    private String entityTypeCode;

    @TableField(typeHandler = JsonbMapTypeHandler.class)
    private Map<String, Object> scopeConfig;

    private Long pageConfigId;

    private Integer sort;

    private String status;

    public static final String STATUS_ACTIVE = "active";
    public static final String STATUS_INACTIVE = "inactive";
}
