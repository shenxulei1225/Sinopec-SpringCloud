package cn.cheers.x.inspection.task.service.execution.steptree;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 任务执行步骤图上的一步：挂哪一类、挂哪一条。
 * <p>权威形态与详情步骤树插件一致（{@code version=1, nodes[]}）。
 * <p>不负责：生成步骤图、发明头尾动作。
 */
public record TaskStepNode(
        String nodeKey,
        int order,
        String parentNodeKey,
        String hangTypeCode,
        String refCode,
        Long refId,
        String title,
        Map<String, Object> params
) {
    public static final String HANG_ACTION = "action";
    public static final String HANG_INSPECTION_ITEM = "inspection_item";
    /** 起点 / 终点路网点位 */
    public static final String HANG_POINT = "point";
    /** 巡检对象成组：对象下挂检查项 */
    public static final String HANG_EQUIPMENT = "equipment";

    public TaskStepNode {
        nodeKey = nodeKey == null ? "" : nodeKey.trim();
        hangTypeCode = hangTypeCode == null ? "" : hangTypeCode.trim();
        refCode = refCode == null || refCode.isBlank() ? null : refCode.trim();
        title = title == null ? "" : title.trim();
        parentNodeKey = parentNodeKey == null || parentNodeKey.isBlank() ? null : parentNodeKey.trim();
        params = params == null
                ? Map.of()
                : Collections.unmodifiableMap(new LinkedHashMap<>(params));
    }

    public boolean isAction() {
        return HANG_ACTION.equals(hangTypeCode);
    }

    public boolean isInspectionItem() {
        return HANG_INSPECTION_ITEM.equals(hangTypeCode);
    }

    public boolean isStopGroup() {
        return HANG_POINT.equals(hangTypeCode) || HANG_EQUIPMENT.equals(hangTypeCode);
    }
}
