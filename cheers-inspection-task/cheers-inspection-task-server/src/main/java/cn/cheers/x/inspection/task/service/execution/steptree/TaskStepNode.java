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
        Map<String, Object> params,
        String actionDomain,
        Long analysisCapabilityId,
        boolean required,
        /** 连续跑冒滴漏才有。TASK 表示从这次任务开始检测到结束。 */
        String analysisSpan
) {
    public static final String HANG_ACTION = "action";
    public static final String HANG_INSPECTION_ITEM = "inspection_item";
    /** 起点、终点，以及检查途经的点位。途经点下挂一次到达和该点上的检查项。 */
    public static final String HANG_POINT = "point";
    /** 没有点位时按设备成组：设备下挂检查项。 */
    public static final String HANG_EQUIPMENT = "equipment";

    public TaskStepNode {
        nodeKey = nodeKey == null ? "" : nodeKey.trim();
        hangTypeCode = hangTypeCode == null ? "" : hangTypeCode.trim();
        refCode = refCode == null || refCode.isBlank() ? null : refCode.trim();
        title = title == null ? "" : title.trim();
        parentNodeKey = parentNodeKey == null || parentNodeKey.isBlank() ? null : parentNodeKey.trim();
        actionDomain = trimToNull(actionDomain);
        analysisSpan = trimToNull(analysisSpan);
        params = params == null
                ? Map.of()
                : Collections.unmodifiableMap(new LinkedHashMap<>(params));
    }

    public TaskStepNode(
            String nodeKey,
            int order,
            String parentNodeKey,
            String hangTypeCode,
            String refCode,
            Long refId,
            String title,
            Map<String, Object> params
    ) {
        this(nodeKey, order, parentNodeKey, hangTypeCode, refCode, refId, title, params, null, null, true, null);
    }

    public TaskStepNode(
            String nodeKey,
            int order,
            String parentNodeKey,
            String hangTypeCode,
            String refCode,
            Long refId,
            String title,
            Map<String, Object> params,
            String actionDomain,
            Long analysisCapabilityId
    ) {
        this(nodeKey, order, parentNodeKey, hangTypeCode, refCode, refId, title, params,
                actionDomain, analysisCapabilityId, true, null);
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

    private static String trimToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
