package cn.iocoder.yudao.module.scene.platform.controller.admin.coordinate.vo;

import cn.iocoder.yudao.module.scene.platform.dal.dataobject.coordinate.CoordinateTransformProfileDO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - 坐标转换配置保存请求 VO")
@Data
public class CoordinateTransformProfileSaveReqVO {

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

    private String transformPipelineJson;

    private String remark;

    public CoordinateTransformProfileDO toDO() {
        CoordinateTransformProfileDO item = new CoordinateTransformProfileDO();
        item.setProfileCode(profileCode);
        item.setProfileName(profileName);
        item.setGeographicCrsCode(geographicCrsCode);
        item.setProjectedCrsCode(projectedCrsCode);
        item.setDatumCode(datumCode);
        item.setEllipsoidCode(ellipsoidCode);
        item.setPlanetShape(planetShape);
        item.setReferenceFrameType(referenceFrameType);
        item.setLocalFrameType(localFrameType);
        item.setEngineFrameType(engineFrameType);
        item.setAxisOrder(axisOrder);
        item.setHandedness(handedness);
        item.setLinearUnit(linearUnit);
        item.setAngularUnit(angularUnit);
        item.setTransformPipelineJson(transformPipelineJson);
        item.setRemark(remark);
        return item;
    }
}
