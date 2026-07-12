package cn.cheers.x.module.dynamicbusiness.dal.dataobject.datamgmt;

import cn.iocoder.yudao.framework.mybatis.core.type.JsonbMapTypeHandler;
import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.util.Map;

@TableName(value = "dm_entity_dimension", autoResultMap = true)
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DmEntityDimensionDO extends TenantBaseDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String entityTypeCode;

    private String dimensionKind;

    private String perspectiveId;

    private Long propsId;

    private Boolean enabled;

    @TableField(typeHandler = JsonbMapTypeHandler.class)
    private Map<String, Object> categoryDimensionMeta;

    /** 仅 MODEL 维：模型管理 Tab 分类栏配置 JSON */
    @TableField(typeHandler = JsonbMapTypeHandler.class)
    private Map<String, Object> modelAdminCategoryMeta;
}
