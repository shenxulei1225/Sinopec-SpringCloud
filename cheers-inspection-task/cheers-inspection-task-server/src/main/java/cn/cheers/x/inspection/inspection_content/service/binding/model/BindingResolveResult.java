package cn.cheers.x.inspection.inspection_content.service.binding.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 对象停靠点绑定解析结果。
 */
@Data
public class BindingResolveResult {

    private Map<Long, List<ObjectStationBindingView>> bindingsByObjectId = new HashMap<>();

    private List<Long> missingObjectIds = new ArrayList<>();
}
