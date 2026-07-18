package cn.iocoder.yudao.module.scene.platform.dal.dataobject.component;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import cn.iocoder.yudao.module.scene.platform.dal.dataobject.JsonStringTypeHandler;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@TableName(value = "camera_component", autoResultMap = true)
@Data
@EqualsAndHashCode(callSuper = true)
public class CameraComponentDO extends BaseDO {

    @TableId
    private Long id;

    private String componentCode;

    private String displayName = "Camera";

    private Float fieldOfView = 90.0F;

    private Float aspectRatio = 1.7777778F;

    private Float nearClipPlane = 10.0F;

    private Float farClipPlane = 10000.0F;

    private Boolean constrainAspectRatio = Boolean.FALSE;

    private Boolean autoActivate = Boolean.TRUE;

    @TableField(typeHandler = JsonStringTypeHandler.class)
    private String metadataJson = "{}";
}
