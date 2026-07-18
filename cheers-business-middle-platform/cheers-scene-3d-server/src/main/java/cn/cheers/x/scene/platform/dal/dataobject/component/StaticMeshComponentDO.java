package cn.cheers.x.scene.platform.dal.dataobject.component;

import cn.cheers.x.framework.mybatis.core.dataobject.BaseDO;
import cn.cheers.x.scene.platform.dal.dataobject.JsonStringTypeHandler;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@TableName(value = "static_mesh_component", autoResultMap = true)
@Data
@EqualsAndHashCode(callSuper = true)
public class StaticMeshComponentDO extends BaseDO {

    @TableId
    private Long id;

    private String componentCode;

    private String displayName = "Static Mesh";

    private String meshCode;

    @TableField(typeHandler = JsonStringTypeHandler.class)
    private String materialsJson = "{}";

    private Boolean castShadow = Boolean.TRUE;

    private Boolean receiveShadow = Boolean.TRUE;

    private Boolean generateOverlapEvents = Boolean.FALSE;

    private String mobility = "Movable";

    @TableField(typeHandler = JsonStringTypeHandler.class)
    private String metadataJson = "{}";
}
