package cn.cheers.x.inspection.task.service.execution.analysis;

import cn.cheers.x.module.dynamicbusiness.api.execution.dto.TaskExecutionStepSnapshotDTO;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 把一次拍照结果与同一检查项下的全部智能分析动作配对。
 *
 * <p>只认任务生成时冻结的 actionDomain 与 analysisCapabilityId，不读检查项或动作标题。</p>
 * <p>不负责选择算法、补能力编号或改变设备动作。每个未完成的智能分析动作各生成一项决定。</p>
 */
public final class PatrolPhotoAnalysisPlanner {

    public static final int PHOTO_OPCODE = 200301;

    private PatrolPhotoAnalysisPlanner() {
    }

    public sealed interface Decision permits Decision.Hold, Decision.Send {
        record Hold(long stepId, String stepCode, String reason) implements Decision {
        }

        record Send(long stepId, String stepCode, String imageUrl, Long analysisCapabilityId)
                implements Decision {
        }
    }

    public static List<Decision> decideAll(
            List<TaskExecutionStepSnapshotDTO> steps,
            String photoStepCode,
            Map<String, Object> photoResultPayload) {
        if (steps == null || !StringUtils.hasText(photoStepCode)) {
            return List.of();
        }
        TaskExecutionStepSnapshotDTO photo = findByCode(steps, photoStepCode);
        if (photo == null || photo.getParentId() == null) {
            return List.of();
        }
        List<TaskExecutionStepSnapshotDTO> analyses = findAnalyses(steps, photo.getParentId());
        if (analyses.isEmpty()) {
            return List.of();
        }
        String imageUrl = firstHttpUrl(photoResultPayload);
        List<Decision> decisions = new ArrayList<>();
        for (TaskExecutionStepSnapshotDTO analysis : analyses) {
            if (alreadySettled(analysis)) {
                continue;
            }
            if (!StringUtils.hasText(imageUrl)) {
                decisions.add(new Decision.Hold(
                        analysis.getId(), analysis.getStepCode(), "拍照结果里没有可分析的图片"));
            } else {
                decisions.add(new Decision.Send(
                        analysis.getId(),
                        analysis.getStepCode(),
                        imageUrl,
                        capabilityIdOf(analysis)));
            }
        }
        return decisions;
    }

    public static boolean isPhotoReport(Map<String, Object> stepUpdate) {
        if (stepUpdate == null || !"completed".equals(String.valueOf(stepUpdate.get("status")))) {
            return false;
        }
        Object payload = stepUpdate.get("resultPayload");
        if (!(payload instanceof Map<?, ?> map)) {
            return false;
        }
        Object opcode = map.get("opcode");
        if (opcode instanceof Number number) {
            return number.intValue() == PHOTO_OPCODE;
        }
        return String.valueOf(PHOTO_OPCODE).equals(String.valueOf(opcode));
    }

    private static boolean alreadySettled(TaskExecutionStepSnapshotDTO analysis) {
        String status = analysis.getStepStatus() == null ? "" : analysis.getStepStatus().trim();
        if ("in_progress".equals(status) || "completed".equals(status)) {
            return true;
        }
        Object verdict = analysis.getResultPayload() == null
                ? null
                : analysis.getResultPayload().get("analysisResult");
        return "正常".equals(verdict) || "告警".equals(verdict);
    }

    private static List<TaskExecutionStepSnapshotDTO> findAnalyses(
            List<TaskExecutionStepSnapshotDTO> steps, Long itemId) {
        List<TaskExecutionStepSnapshotDTO> matches = new ArrayList<>();
        for (TaskExecutionStepSnapshotDTO step : steps) {
            if (step == null || step.getSource() == null) {
                continue;
            }
            if (itemId.equals(step.getParentId())
                    && "INTELLIGENT_ANALYSIS".equals(textOf(step.getSource().get("actionDomain")))
                    && capabilityIdOf(step) != null
                    && step.getId() != null
                    && StringUtils.hasText(step.getStepCode())) {
                matches.add(step);
            }
        }
        return matches;
    }

    private static Long capabilityIdOf(TaskExecutionStepSnapshotDTO step) {
        Object raw = step.getSource().get("analysisCapabilityId");
        if (raw instanceof Number number) {
            return number.longValue();
        }
        try {
            String text = textOf(raw);
            return text.isBlank() ? null : Long.parseLong(text);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private static String textOf(Object value) {
        return value == null ? "" : String.valueOf(value).trim();
    }

    private static TaskExecutionStepSnapshotDTO findByCode(
            List<TaskExecutionStepSnapshotDTO> steps, String stepCode) {
        for (TaskExecutionStepSnapshotDTO step : steps) {
            if (step != null && stepCode.equals(step.getStepCode())) {
                return step;
            }
        }
        return null;
    }

    static String firstHttpUrl(Object value) {
        List<String> found = new ArrayList<>();
        collectUrls(value, found);
        return found.isEmpty() ? "" : found.get(0);
    }

    private static void collectUrls(Object value, List<String> found) {
        if (value == null || !found.isEmpty()) {
            return;
        }
        if (value instanceof String text) {
            String trimmed = text.trim();
            if (trimmed.startsWith("http://") || trimmed.startsWith("https://")) {
                found.add(trimmed);
            }
            return;
        }
        if (value instanceof Map<?, ?> map) {
            for (Object child : map.values()) {
                collectUrls(child, found);
                if (!found.isEmpty()) {
                    return;
                }
            }
            return;
        }
        if (value instanceof Iterable<?> iterable) {
            for (Object child : iterable) {
                collectUrls(child, found);
                if (!found.isEmpty()) {
                    return;
                }
            }
        }
    }

    public static Optional<String> verdictOf(Map<String, Object> payload) {
        if (payload == null) {
            return Optional.empty();
        }
        Object verdict = payload.get("analysisResult");
        if (verdict instanceof String text && StringUtils.hasText(text)) {
            return Optional.of(text.trim());
        }
        return Optional.empty();
    }
}
