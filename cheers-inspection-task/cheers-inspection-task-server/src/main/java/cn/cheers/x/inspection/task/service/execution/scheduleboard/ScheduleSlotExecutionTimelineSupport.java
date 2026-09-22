package cn.cheers.x.inspection.task.service.execution.scheduleboard;

import cn.cheers.x.module.platform.runtime.api.dto.ProcessTimelineActionRespDTO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 从 L4 计划点过程时间线解析执行进度与步骤态（读侧权威）。
 * <p>写侧通过 {@code slot.execution_start / slot.step_update / slot.execution_complete} 追加事件；
 * 展示层只消费时间线，禁止再按 plannedEnd 或 slot 状态猜步骤完成情况。
 */
public final class ScheduleSlotExecutionTimelineSupport {

    public static final String ACTION_EXECUTION_START = "slot.execution_start";
    public static final String ACTION_STEP_UPDATE = "slot.step_update";
    public static final String ACTION_EXECUTION_COMPLETE = "slot.execution_complete";

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private ScheduleSlotExecutionTimelineSupport() {
    }

    public record ExecutionSnapshot(
            int progressPercent,
            Map<String, String> stepStatusByCode,
            boolean hasStepFailure
    ) {
        public static ExecutionSnapshot empty() {
            return new ExecutionSnapshot(0, Map.of(), false);
        }
    }

    public static ExecutionSnapshot resolve(List<ProcessTimelineActionRespDTO> timeline) {
        if (CollectionUtils.isEmpty(timeline)) {
            return ExecutionSnapshot.empty();
        }
        List<ProcessTimelineActionRespDTO> ordered = timeline.stream()
                .sorted(Comparator.comparing(ProcessTimelineActionRespDTO::getOccurredAt,
                        Comparator.nullsLast(Comparator.naturalOrder()))
                        .thenComparing(ProcessTimelineActionRespDTO::getId,
                                Comparator.nullsLast(Comparator.naturalOrder())))
                .toList();

        Map<String, String> stepStatusByCode = new LinkedHashMap<>();
        int progressPercent = 0;
        boolean hasFailure = false;

        for (ProcessTimelineActionRespDTO row : ordered) {
            if (row == null || !StringUtils.hasText(row.getActionCode())) {
                continue;
            }
            Map<String, Object> payload = parsePayload(row.getPayloadJson());
            switch (row.getActionCode().trim()) {
                case ACTION_STEP_UPDATE -> {
                    String stepCode = text(payload.get("stepCode"));
                    String status = mapStepStatus(text(payload.get("status")));
                    if (StringUtils.hasText(stepCode) && StringUtils.hasText(status)) {
                        stepStatusByCode.put(stepCode.trim(), status);
                        if ("FAILED".equals(status)) {
                            hasFailure = true;
                        }
                    }
                    Integer progress = intOrNull(payload.get("progressPercent"));
                    if (progress != null) {
                        progressPercent = Math.max(0, Math.min(100, progress));
                    }
                }
                case ACTION_EXECUTION_COMPLETE -> {
                    Integer progress = intOrNull(payload.get("progressPercent"));
                    if (progress != null) {
                        progressPercent = Math.max(0, Math.min(100, progress));
                    }
                    if (Boolean.TRUE.equals(payload.get("hasStepFailure"))) {
                        hasFailure = true;
                    }
                }
                default -> {
                }
            }
        }
        return new ExecutionSnapshot(progressPercent, Map.copyOf(stepStatusByCode), hasFailure);
    }

    public static String resolveNodeStatus(
            Map<String, Object> nodeMap,
            Map<String, String> stepStatusByCode) {
        if (nodeMap == null || stepStatusByCode.isEmpty()) {
            return null;
        }
        String stepCode = firstText(nodeMap.get("stepCode"), nodeMap.get("nodeKey"));
        if (!StringUtils.hasText(stepCode)) {
            return null;
        }
        return stepStatusByCode.get(stepCode.trim());
    }

    private static String mapStepStatus(String raw) {
        if (!StringUtils.hasText(raw)) {
            return null;
        }
        return switch (raw.trim().toLowerCase()) {
            case "completed", "done" -> "DONE";
            case "failed", "fault" -> "FAILED";
            case "in_progress", "executing" -> "EXECUTING";
            case "skipped" -> "SKIPPED";
            default -> "PENDING";
        };
    }

    private static Map<String, Object> parsePayload(String payloadJson) {
        if (!StringUtils.hasText(payloadJson)) {
            return Map.of();
        }
        try {
            return MAPPER.readValue(payloadJson.trim(), new TypeReference<>() {
            });
        } catch (Exception ignored) {
            return Map.of();
        }
    }

    private static String text(Object raw) {
        return raw == null ? null : String.valueOf(raw).trim();
    }

    private static String firstText(Object... candidates) {
        for (Object candidate : candidates) {
            String text = text(candidate);
            if (StringUtils.hasText(text)) {
                return text;
            }
        }
        return null;
    }

    private static Integer intOrNull(Object raw) {
        if (raw == null) {
            return null;
        }
        if (raw instanceof Number number) {
            return number.intValue();
        }
        try {
            return Integer.parseInt(String.valueOf(raw).trim());
        } catch (NumberFormatException ignored) {
            return null;
        }
    }
}
