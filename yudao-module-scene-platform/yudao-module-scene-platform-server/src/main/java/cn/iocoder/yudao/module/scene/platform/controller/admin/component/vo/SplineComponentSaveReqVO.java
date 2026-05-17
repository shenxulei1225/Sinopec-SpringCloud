package cn.iocoder.yudao.module.scene.platform.controller.admin.component.vo;

import cn.iocoder.yudao.module.scene.platform.dal.dataobject.component.SplineComponentDO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - Spline 组件保存请求 VO")
@Data
public class SplineComponentSaveReqVO {

    private String componentCode;

    private String displayName;

    private String splinePointsJson;

    private Boolean closedLoop;

    private String metadataJson;

    public SplineComponentDO toDO() {
        SplineComponentDO item = new SplineComponentDO();
        item.setComponentCode(componentCode);
        item.setDisplayName(displayName);
        item.setSplinePointsJson(splinePointsJson);
        item.setClosedLoop(closedLoop);
        item.setMetadataJson(metadataJson);
        return item;
    }
}
