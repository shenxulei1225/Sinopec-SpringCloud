package cn.iocoder.yudao.module.scene.platform.dal.dataobject.component;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import cn.iocoder.yudao.module.scene.platform.dal.dataobject.JsonStringTypeHandler;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@TableName(value = "mesh_component", autoResultMap = true)
@Data
@EqualsAndHashCode(callSuper = true)
public class MeshComponentDO extends BaseDO {

    @TableId
    private Long id;

    private String componentCode;

    private String displayName = "Mesh";

    private String meshDescription;

    @TableField(typeHandler = JsonStringTypeHandler.class)
    private String materialsJson = "{}";

    private Boolean castShadow = Boolean.TRUE;

    private Boolean receiveShadow = Boolean.TRUE;

    private String mobility = "Movable";

    @TableField(typeHandler = JsonStringTypeHandler.class)
    private String metadataJson = "{}";
}
