package cn.cheers.x.scene.platform.dal.dataobject.component;

import cn.cheers.x.framework.tenant.core.db.TenantBaseDO;
import cn.cheers.x.scene.platform.dal.dataobject.JsonStringTypeHandler;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@TableName(value = "scene_component_instance", autoResultMap = true)
@Data
@EqualsAndHashCode(callSuper = true)
public class SceneComponentInstanceDO extends TenantBaseDO {

    @TableId
    private Long id;

    private Long sceneId;

    private String sceneCode;

    private String instanceCode;

    private String componentCode;

    private Boolean enabledFlag;

    @TableField(typeHandler = JsonStringTypeHandler.class)
    private String configJson;

    @TableField(typeHandler = JsonStringTypeHandler.class)
    private String metadataJson;

    private Integer sortNo;
}
