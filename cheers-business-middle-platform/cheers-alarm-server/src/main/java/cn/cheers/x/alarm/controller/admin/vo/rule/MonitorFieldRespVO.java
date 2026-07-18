package cn.cheers.x.alarm.controller.admin.vo.rule;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 监测字段响应 VO
 * 
 * <p>用于告警规则配置时选择监测字段。</p>
 *
 * @author 告警管理模块
 */
@Schema(description = "管理后台 - 监测字段响应 VO")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MonitorFieldRespVO {

    @Schema(description = "字段编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "waterLevel")
    private String fieldCode;

    @Schema(description = "字段名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "水位")
    private String fieldName;

    @Schema(description = "单位符号", example = "cm")
    private String unit;

    @Schema(description = "单位名称", example = "厘米")
    private String unitName;

    @Schema(description = "数据类型", example = "double")
    private String dataType;

    @Schema(description = "字段分组", example = "环境监测")
    private String groupName;

}
