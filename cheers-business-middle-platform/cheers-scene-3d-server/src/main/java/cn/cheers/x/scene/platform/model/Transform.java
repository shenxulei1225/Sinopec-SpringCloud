package cn.cheers.x.scene.platform.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "变换")
@Data
public class Transform {

    @Schema(description = "位置")
    private Vector3 location;

    @Schema(description = "旋转")
    private Rotator rotation;

    @Schema(description = "缩放")
    private Vector3 scale;
}
