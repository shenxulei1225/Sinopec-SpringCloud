package cn.iocoder.yudao.module.scene.platform.dal.dataobject.coordinate;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import cn.iocoder.yudao.module.scene.platform.dal.dataobject.JsonStringTypeHandler;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@TableName(value = "coordinate_reference", autoResultMap = true)
@Data
@EqualsAndHashCode(callSuper = true)
public class CoordinateReferenceDO extends TenantBaseDO {

    @TableId
    private Long id;

    private Long sceneId;

    private BigDecimal originLng;

    private BigDecimal originLat;

    private BigDecimal originHeight;

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

    private BigDecimal originProjectedX;

    private BigDecimal originProjectedY;

    private BigDecimal originProjectedZ;

    private BigDecimal originEcefX;

    private BigDecimal originEcefY;

    private BigDecimal originEcefZ;

    private BigDecimal originHeading;

    private BigDecimal originPitch;

    private BigDecimal originRoll;

    private String transformProfileCode;

    @TableField(typeHandler = JsonStringTypeHandler.class)
    private String transformConfigJson;
}
