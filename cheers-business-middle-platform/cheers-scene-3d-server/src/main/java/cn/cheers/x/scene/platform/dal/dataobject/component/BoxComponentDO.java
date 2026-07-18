package cn.cheers.x.scene.platform.dal.dataobject.component;

import cn.cheers.x.framework.mybatis.core.dataobject.BaseDO;
import cn.cheers.x.scene.platform.dal.dataobject.JsonStringTypeHandler;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@TableName(value = "box_component", autoResultMap = true)
@Data
@EqualsAndHashCode(callSuper = true)
public class BoxComponentDO extends BaseDO {

    @TableId
    private Long id;

    private String componentCode;

    private String displayName = "Box";

    private Float boxExtentX = 50.0F;

    private Float boxExtentY = 50.0F;

    private Float boxExtentZ = 50.0F;

    private String collisionEnabled = "QueryAndPhysics";

    private Boolean generateOverlapEvents = Boolean.TRUE;

    @TableField(typeHandler = JsonStringTypeHandler.class)
    private String metadataJson = "{}";
}
