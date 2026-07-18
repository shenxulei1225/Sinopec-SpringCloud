package cn.cheers.x.scene.platform.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "四元数旋转")
@Data
public class Quaternion {

    @Schema(description = "X")
    private Double x;

    @Schema(description = "Y")
    private Double y;

    @Schema(description = "Z")
    private Double z;

    @Schema(description = "W")
    private Double w;
}
