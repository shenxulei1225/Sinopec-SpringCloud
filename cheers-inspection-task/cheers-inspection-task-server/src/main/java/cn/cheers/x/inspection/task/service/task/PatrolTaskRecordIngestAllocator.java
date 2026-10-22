package cn.cheers.x.inspection.task.service.task;

import cn.cheers.x.framework.common.exception.util.ServiceExceptionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * 为单次巡检执行分配媒体收流地址（东古/机器人 200201 的 request.streamUrl）。
 * <p>收流前缀来自 {@link PatrolMediaAddress}。
 * 计划点编号已经是 {@code patrol-task-任务号-序号} 时，流名就用这个编号。
 * 再加 {@code patrol-任务号-} 会把 patrol 和任务号写两遍。
 * 计划点是短编号时，前面才加 {@code patrol-任务号-}，避免两条任务撞名。
 * <p>不负责：向 go2rtc 注册流、落盘录像文件；媒体侧须接受该路径上的推流/拉流。
 */
@Component
public class PatrolTaskRecordIngestAllocator {

    private final String ingestBaseUrl;
    private final String recordFileUrlTemplate;

    @Autowired
    public PatrolTaskRecordIngestAllocator(PatrolMediaAddress media) {
        this(media.ingestBaseUrl(), media.recordFileUrlTemplate());
    }

    /** 测试直接给出已经拼好的前缀和回放模板。运行时走 {@link #PatrolTaskRecordIngestAllocator(PatrolMediaAddress)}。 */
    public PatrolTaskRecordIngestAllocator(String ingestBaseUrl, String recordFileUrlTemplate) {
        this.ingestBaseUrl = ingestBaseUrl == null ? "" : ingestBaseUrl.trim();
        this.recordFileUrlTemplate = recordFileUrlTemplate == null ? "" : recordFileUrlTemplate.trim();
    }

    /**
     * @return 完整收流 URL，写入 stream_url / 执行快照 taskRecordIngestUrl
     */
    public String allocate(Long taskDefinitionId, String scheduleSlotId) {
        if (taskDefinitionId == null || taskDefinitionId <= 0) {
            throw ServiceExceptionUtil.invalidParamException("分配录像收流地址缺少任务 id");
        }
        if (!StringUtils.hasText(scheduleSlotId)) {
            throw ServiceExceptionUtil.invalidParamException("分配录像收流地址缺少计划点 id");
        }
        if (!StringUtils.hasText(ingestBaseUrl)) {
            throw ServiceExceptionUtil.invalidParamException(
                    "未配置流媒体收流地址，不能为本次巡检分配录像收流地址");
        }
        return buildIngestUrl(taskDefinitionId, scheduleSlotId);
    }

    /**
     * 这个收流名字是不是这条任务登记的。
     * 计划点编号本身、前面加过 patrol-任务号- 的，以及任务号写了两遍的旧名字，都算这条任务。
     * 别的任务、首页上原来就有的画面不算。
     */
    public static boolean belongsToTask(String streamName, long taskId) {
        if (!StringUtils.hasText(streamName) || taskId <= 0) {
            return false;
        }
        String name = streamName.trim();
        String planPoint = "patrol-task-" + taskId;
        if (name.equals(planPoint) || name.startsWith(planPoint + "-")) {
            return true;
        }
        return name.startsWith("patrol-" + taskId + "-");
    }

    /** go2rtc / 媒体网关上的流名，与收流路径最后一段一致。 */
    public String streamName(Long taskDefinitionId, String scheduleSlotId) {
        if (taskDefinitionId == null || taskDefinitionId <= 0 || !StringUtils.hasText(scheduleSlotId)) {
            throw ServiceExceptionUtil.invalidParamException("分配录像流名缺少任务或计划点");
        }
        String slot = sanitizeSlotId(scheduleSlotId.trim());
        String planPoint = "patrol-task-" + taskDefinitionId + "-";
        if (slot.startsWith(planPoint) || slot.equals("patrol-task-" + taskDefinitionId)) {
            return slot;
        }
        String shortPrefix = "patrol-" + taskDefinitionId + "-";
        if (slot.startsWith(shortPrefix)) {
            return slot;
        }
        return shortPrefix + slot;
    }

    public String buildIngestUrl(Long taskDefinitionId, String scheduleSlotId) {
        String base = ingestBaseUrl.endsWith("/") ? ingestBaseUrl.substring(0, ingestBaseUrl.length() - 1) : ingestBaseUrl;
        return base + "/" + streamName(taskDefinitionId, scheduleSlotId);
    }

    /**
     * 停录后用于执行记录回放的 MP4/HTTP 地址。模板须含 {@code {streamName}}，未配置则返回空。
     */
    public java.util.Optional<String> recordFileUrlForStream(String streamName) {
        if (!StringUtils.hasText(recordFileUrlTemplate) || !StringUtils.hasText(streamName)) {
            return java.util.Optional.empty();
        }
        if (!recordFileUrlTemplate.contains("{streamName}")) {
            return java.util.Optional.empty();
        }
        return java.util.Optional.of(recordFileUrlTemplate.replace("{streamName}", streamName.trim()));
    }

    static String sanitizeSlotId(String slotId) {
        return slotId.replaceAll("[^a-zA-Z0-9._-]", "_");
    }
}
