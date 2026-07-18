package cn.iocoder.yudao.module.scene.platform.dal.dataobject.component;

import cn.cheers.x.framework.mybatis.core.dataobject.BaseDO;
import cn.iocoder.yudao.module.scene.platform.dal.dataobject.JsonStringTypeHandler;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@TableName(value = "primitive_component", autoResultMap = true)
@Data
@EqualsAndHashCode(callSuper = true)
public class PrimitiveComponentDO extends BaseDO {

    @TableId
    private Long id;

    private String componentCode;

    private String displayName = "Primitive";

    private String collisionEnabled = "QueryAndPhysics";

    private String objectType = "WorldDynamic";

    private Boolean generateOverlapEvents = Boolean.TRUE;

    private Boolean simulationGeneratesHitEvents = Boolean.FALSE;

    private Boolean canCharacterStepUpOn = Boolean.FALSE;

    private Boolean useDefaultCollision = Boolean.TRUE;

    @TableField(typeHandler = JsonStringTypeHandler.class)
    private String physicsMaterialJson = "{}";

    @TableField(typeHandler = JsonStringTypeHandler.class)
    private String boundsJson = "{}";

    @TableField(typeHandler = JsonStringTypeHandler.class)
    private String collisionResponseJson = "{}";

    @TableField(typeHandler = JsonStringTypeHandler.class)
    private String metadataJson = "{}";
}
