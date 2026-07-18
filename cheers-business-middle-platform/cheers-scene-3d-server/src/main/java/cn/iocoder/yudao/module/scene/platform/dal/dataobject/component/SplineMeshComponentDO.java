package cn.iocoder.yudao.module.scene.platform.dal.dataobject.component;

import cn.cheers.x.framework.mybatis.core.dataobject.BaseDO;
import cn.iocoder.yudao.module.scene.platform.dal.dataobject.JsonStringTypeHandler;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@TableName(value = "spline_mesh_component", autoResultMap = true)
@Data
@EqualsAndHashCode(callSuper = true)
public class SplineMeshComponentDO extends BaseDO {

    @TableId
    private Long id;

    private String componentCode;

    private String displayName = "Spline Mesh";

    private String sourceSplineCode;

    private String meshCode;

    private Boolean castShadow = Boolean.TRUE;

    private Boolean receiveShadow = Boolean.TRUE;

    @TableField(typeHandler = JsonStringTypeHandler.class)
    private String metadataJson = "{}";
}
