package cn.cheers.x.scene.platform.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "三维向量")
@Data
public class Vector3 {

    @Schema(description = "X")
    private Double x;

    @Schema(description = "Y")
    private Double y;

    @Schema(description = "Z")
    private Double z;

    public Vector3() {}

    public Vector3(Double x, Double y, Double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
}
