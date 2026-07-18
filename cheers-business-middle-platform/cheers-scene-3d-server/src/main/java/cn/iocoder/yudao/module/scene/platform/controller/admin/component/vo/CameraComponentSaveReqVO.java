package cn.iocoder.yudao.module.scene.platform.controller.admin.component.vo;

import cn.iocoder.yudao.module.scene.platform.dal.dataobject.component.CameraComponentDO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - Camera 组件保存请求 VO")
@Data
public class CameraComponentSaveReqVO {

    private String componentCode;

    private String displayName;

    private Float fieldOfView;

    private Float aspectRatio;

    private Float nearClipPlane;

    private Float farClipPlane;

    private Boolean constrainAspectRatio;

    private Boolean autoActivate;

    private String metadataJson;

    public CameraComponentDO toDO() {
        CameraComponentDO item = new CameraComponentDO();
        item.setComponentCode(componentCode);
        item.setDisplayName(displayName);
        item.setFieldOfView(fieldOfView);
        item.setAspectRatio(aspectRatio);
        item.setNearClipPlane(nearClipPlane);
        item.setFarClipPlane(farClipPlane);
        item.setConstrainAspectRatio(constrainAspectRatio);
        item.setAutoActivate(autoActivate);
        item.setMetadataJson(metadataJson);
        return item;
    }
}
