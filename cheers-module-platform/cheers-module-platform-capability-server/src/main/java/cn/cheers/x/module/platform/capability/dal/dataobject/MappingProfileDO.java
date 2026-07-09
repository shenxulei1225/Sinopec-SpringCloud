package cn.cheers.x.module.platform.capability.dal.dataobject;

import cn.cheers.x.module.platform.capability.framework.mybatis.JsonbStringTypeHandler;
import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@TableName(value = "platform_mapping_profile", autoResultMap = true)
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MappingProfileDO extends TenantBaseDO {

    @TableId
    private String id;

    private String entityTypeCode;

    private String sourceModelCode;

    private String displayName;

    @TableField(typeHandler = JsonbStringTypeHandler.class)
    private String fieldMappings;

    private Integer defaultDurationMinutes;

    private Integer defaultPriority;

    private String status;
}
