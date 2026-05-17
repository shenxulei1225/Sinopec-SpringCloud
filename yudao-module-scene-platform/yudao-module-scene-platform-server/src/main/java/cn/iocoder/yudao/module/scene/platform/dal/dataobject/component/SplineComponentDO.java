package cn.iocoder.yudao.module.scene.platform.dal.dataobject.component;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import cn.iocoder.yudao.module.scene.platform.dal.dataobject.JsonStringTypeHandler;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@TableName(value = "spline_component", autoResultMap = true)
@Data
@EqualsAndHashCode(callSuper = true)
public class SplineComponentDO extends BaseDO {

    @TableId
    private Long id;

    private String componentCode;

    private String displayName = "Spline";

    @TableField(typeHandler = JsonStringTypeHandler.class)
    private String splinePointsJson = "[]";

    private Boolean closedLoop = Boolean.FALSE;

    private String mobility = "Movable";

    @TableField(typeHandler = JsonStringTypeHandler.class)
    private String metadataJson = "{}";
}
