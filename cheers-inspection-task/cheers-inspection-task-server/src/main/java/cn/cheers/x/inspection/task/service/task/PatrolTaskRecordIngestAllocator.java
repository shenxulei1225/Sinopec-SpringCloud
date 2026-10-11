package cn.cheers.x.inspection.task.service.task;

import cn.cheers.x.framework.common.exception.util.ServiceExceptionUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * 为单次巡检执行分配媒体收流地址（东古/机器人 200201 的 rstp）。
 * <p>基址来自配置；流名含任务 id 与计划点 id，避免多任务共用一个 ingest 分不清归属。
 * <p>不负责：向 go2rtc 注册流、落盘录像文件；媒体侧须接受该路径上的推流/拉流。
 */
@Component
public class PatrolTaskRecordIngestAllocator {

    private final String ingestBaseUrl;
    private final String recordFileUrlTemplate;

    public PatrolTaskRecordIngestAllocator(
            @Value("${patrol.media.record-ingest-base-url:}") String ingestBaseUrl,
            @Value("${patrol.media.record-file-url-template:}") String recordFileUrlTemplate) {
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
                    "未配置 patrol.media.record-ingest-base-url，不能为本次巡检分配录像收流地址");
        }
        return buildIngestUrl(taskDefinitionId, scheduleSlotId);
    }

    /** go2rtc / 媒体网关上的流名，与收流路径最后一段一致。 */
    public String streamName(Long taskDefinitionId, String scheduleSlotId) {
        if (taskDefinitionId == null || taskDefinitionId <= 0 || !StringUtils.hasText(scheduleSlotId)) {
            throw ServiceExceptionUtil.invalidParamException("分配录像流名缺少任务或计划点");
        }
        return "patrol-" + taskDefinitionId + "-" + sanitizeSlotId(scheduleSlotId.trim());
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
