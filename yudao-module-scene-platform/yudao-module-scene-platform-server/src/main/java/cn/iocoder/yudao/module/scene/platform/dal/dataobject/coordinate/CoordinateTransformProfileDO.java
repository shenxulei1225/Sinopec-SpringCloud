package cn.iocoder.yudao.module.scene.platform.dal.dataobject.coordinate;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import cn.iocoder.yudao.module.scene.platform.dal.dataobject.JsonStringTypeHandler;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@TableName(value = "coordinate_transform_profile", autoResultMap = true)
@Data
@EqualsAndHashCode(callSuper = true)
public class CoordinateTransformProfileDO extends BaseDO {

    @TableId
    private Long id;

    private String profileCode;

    private String profileName;

    private String geographicCrsCode;

    private String projectedCrsCode;

    private String datumCode;

    private String ellipsoidCode;

    private String planetShape;

    private String referenceFrameType;

    private String localFrameType;

    private String engineFrameType;

    private String axisOrder;

    private String handedness;

    private String linearUnit;

    private String angularUnit;

    @TableField(typeHandler = JsonStringTypeHandler.class)
    private String transformPipelineJson;

    private String remark;

    private Integer status;
}
