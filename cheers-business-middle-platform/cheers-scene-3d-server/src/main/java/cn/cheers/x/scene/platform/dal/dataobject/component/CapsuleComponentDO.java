package cn.cheers.x.scene.platform.dal.dataobject.component;

import cn.cheers.x.framework.mybatis.core.dataobject.BaseDO;
import cn.cheers.x.scene.platform.dal.dataobject.JsonStringTypeHandler;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@TableName(value = "capsule_component", autoResultMap = true)
@Data
@EqualsAndHashCode(callSuper = true)
public class CapsuleComponentDO extends BaseDO {

    @TableId
    private Long id;

    private String componentCode;

    private String displayName = "Capsule";

    private Float capsuleRadius = 42.0F;

    private Float capsuleHalfHeight = 96.0F;

    private String collisionEnabled = "QueryAndPhysics";

    private Boolean generateOverlapEvents = Boolean.TRUE;

    @TableField(typeHandler = JsonStringTypeHandler.class)
    private String metadataJson = "{}";
}
