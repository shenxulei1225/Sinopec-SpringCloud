package cn.cheers.x.inspection.inspection_content.controller.admin.vo.binding;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@Schema(description = "批量对象↔停靠点绑定解析响应")
public class ObjectStationBindingResolveRespVO {

    @Schema(description = "按对象 ID 分组的绑定列表")
    private Map<Long, List<ObjectStationBindingRespVO>> bindingsByObjectId = new HashMap<>();

    @Schema(description = "无绑定记录的对象 ID 列表")
    private List<Long> missingObjectIds = new ArrayList<>();
}
