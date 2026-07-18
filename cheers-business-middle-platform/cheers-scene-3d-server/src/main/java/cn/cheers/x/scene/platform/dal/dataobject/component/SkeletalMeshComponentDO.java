package cn.cheers.x.scene.platform.dal.dataobject.component;

import cn.cheers.x.framework.mybatis.core.dataobject.BaseDO;
import cn.cheers.x.scene.platform.dal.dataobject.JsonStringTypeHandler;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@TableName(value = "skeletal_mesh_component", autoResultMap = true)
@Data
@EqualsAndHashCode(callSuper = true)
public class SkeletalMeshComponentDO extends BaseDO {

    @TableId
    private Long id;

    private String componentCode;

    private String displayName = "Skeletal Mesh";

    private String skeletonCode;

    private String animationBlueprintCode;

    @TableField(typeHandler = JsonStringTypeHandler.class)
    private String materialsJson = "{}";

    private Boolean castShadow = Boolean.TRUE;

    private Boolean receiveShadow = Boolean.TRUE;

    private Boolean useAnimationBlueprint = Boolean.TRUE;

    @TableField(typeHandler = JsonStringTypeHandler.class)
    private String metadataJson = "{}";
}
