package cn.cheers.x.scene.platform.adapter.spi;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SceneTransform {

    private BigDecimal localX;
    private BigDecimal localY;
    private BigDecimal localZ;
    private BigDecimal rotationX;
    private BigDecimal rotationY;
    private BigDecimal rotationZ;
    private BigDecimal scaleX;
    private BigDecimal scaleY;
    private BigDecimal scaleZ;
}
