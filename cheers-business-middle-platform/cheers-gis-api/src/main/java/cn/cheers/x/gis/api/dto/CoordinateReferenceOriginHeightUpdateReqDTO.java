package cn.cheers.x.gis.api.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 坐标参考：仅更新原点高程（RPC / 写回确认）。
 */
@Data
public class CoordinateReferenceOriginHeightUpdateReqDTO {

    @NotNull(message = "原点高程不能为空")
    private BigDecimal originHeight;
    private BigDecimal originLng;
    private BigDecimal originLat;
    private String originHeightSource;
}
