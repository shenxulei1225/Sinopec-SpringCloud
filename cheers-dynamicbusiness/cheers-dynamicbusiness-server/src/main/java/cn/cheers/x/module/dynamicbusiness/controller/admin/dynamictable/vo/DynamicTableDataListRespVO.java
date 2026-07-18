package cn.cheers.x.module.dynamicbusiness.controller.admin.dynamictable.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 动态表数据列表响应 VO
 */
@Schema(description = "管理后台 - 动态表数据列表响应")
@Data
public class DynamicTableDataListRespVO {

    @Schema(description = "数据列表")
    private List<Map<String, Object>> list;

    @Schema(description = "总数")
    private Long total;
}
