package cn.cheers.x.scene.platform.controller.admin.coordinate.vo;

import cn.cheers.x.scene.platform.dal.dataobject.coordinate.CoordinateReferenceDO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Schema(description = "管理后台 - 坐标参考保存请求 VO")
@Data
public class CoordinateReferenceSaveReqVO {

    private String geographicCrsCode;
    private String projectedCrsCode;
    private String datumCode;
    private String ellipsoidCode;
    private BigDecimal originLng;
    private BigDecimal originLat;
    private BigDecimal originHeight;
    private BigDecimal originProjectedX;
    private BigDecimal originProjectedY;
    private BigDecimal originProjectedZ;
    private BigDecimal originEcefX;
    private BigDecimal originEcefY;
    private BigDecimal originEcefZ;
    private BigDecimal originHeading;
    private BigDecimal originPitch;
    private BigDecimal originRoll;
    private String localFrameType;
    private String engineFrameType;
    private String referenceFrameType;
    private String planetShape;
    private String axisOrder;
    private String handedness;
    private String linearUnit;
    private String angularUnit;
    private String transformProfileCode;
    private String transformConfigJson;

    public CoordinateReferenceDO toDO() {
        CoordinateReferenceDO item = new CoordinateReferenceDO();
        item.setGeographicCrsCode(geographicCrsCode);
        item.setProjectedCrsCode(projectedCrsCode);
        item.setDatumCode(datumCode);
        item.setEllipsoidCode(ellipsoidCode);
        item.setOriginLng(originLng);
        item.setOriginLat(originLat);
        item.setOriginHeight(originHeight);
        item.setOriginProjectedX(originProjectedX);
        item.setOriginProjectedY(originProjectedY);
        item.setOriginProjectedZ(originProjectedZ);
        item.setOriginEcefX(originEcefX);
        item.setOriginEcefY(originEcefY);
        item.setOriginEcefZ(originEcefZ);
        item.setOriginHeading(originHeading);
        item.setOriginPitch(originPitch);
        item.setOriginRoll(originRoll);
        item.setLocalFrameType(localFrameType);
        item.setEngineFrameType(engineFrameType);
        item.setReferenceFrameType(referenceFrameType);
        item.setPlanetShape(planetShape);
        item.setAxisOrder(axisOrder);
        item.setHandedness(handedness);
        item.setLinearUnit(linearUnit);
        item.setAngularUnit(angularUnit);
        item.setTransformProfileCode(transformProfileCode);
        item.setTransformConfigJson(transformConfigJson);
        return item;
    }
}
