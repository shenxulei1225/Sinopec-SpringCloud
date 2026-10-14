package cn.cheers.x.inspection.task.service.execution.analysis;

import cn.cheers.x.device.protocolgateway.api.dto.PatrolOpenRunSnapshotKeys;
import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.module.dynamicbusiness.api.entity.EntityRpcApi;
import cn.cheers.x.module.dynamicbusiness.api.entity.dto.EntityRespDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 连续视频检查：这次执行从进入「正在执行」到结束，按间隔从收流里取当前画面。
 * <p>只有整次任务上的连续视频检查才登记。点拍摄检查不抽帧。
 * 流名称来自这次执行已经写下的收流名称。任务结束就停，并结束这次连续检查。
 * <p>不负责解码视频，也不挑选算法。
 */
@Slf4j
@Component
public class PatrolLiveStreamWatch {

    static final String RECORD_TYPE = "task_excution_record";
    private static final Set<String> STOPPED = Set.of("completed", "fault", "failed", "cancelled");

    private final EntityRpcApi entityRpcApi;
    private final StreamFrameSource frames;
    private final PatrolLiveStreamAnalysis analysis;
    private final ConcurrentHashMap<Long, String> running = new ConcurrentHashMap<>();

    public PatrolLiveStreamWatch(
            EntityRpcApi entityRpcApi,
            StreamFrameSource frames,
            PatrolLiveStreamAnalysis analysis) {
        this.entityRpcApi = entityRpcApi;
        this.frames = frames;
        this.analysis = analysis;
    }

    /**
     * 设备已经报上这次执行的状态时调用。正在执行才开始抽帧。
     */
    public void onExecutionStatus(Long recordId, String executionStatus) {
        if (recordId == null || !StringUtils.hasText(executionStatus)) {
            return;
        }
        if (STOPPED.contains(executionStatus)) {
            if (running.remove(recordId) != null) {
                analysis.close(recordId);
            }
            return;
        }
        if (!"in_progress".equals(executionStatus) || running.containsKey(recordId)) {
            return;
        }
        if (!analysis.hasTaskSpan(recordId)) {
            return;
        }
        String streamName = streamName(recordId);
        if (streamName == null) {
            return;
        }
        running.put(recordId, streamName);
    }

    @Scheduled(fixedDelayString = "${patrol.media.frame-interval-ms:3000}")
    public void sampleRunningStreams() {
        for (Map.Entry<Long, String> entry : running.entrySet()) {
            String imageUrl = frames.currentImageUrl(entry.getValue());
            if (imageUrl == null) {
                continue;
            }
            try {
                analysis.submitFrame(entry.getKey(), imageUrl);
            } catch (RuntimeException ex) {
                log.info("[live-frame] 抽帧分析没有提交 recordId={} reason={}", entry.getKey(), ex.getMessage());
            }
        }
    }

    private String streamName(Long recordId) {
        CommonResult<EntityRespDTO> loaded = entityRpcApi.getEntity(recordId, RECORD_TYPE);
        if (loaded == null || !loaded.isSuccess() || loaded.getData() == null) {
            return null;
        }
        Map<String, Object> custom = loaded.getData().getCustomFields();
        if (custom == null) {
            return null;
        }
        String fromMedia = text(mapOf(custom.get("task_patrol_record_media")).get("streamName"));
        if (fromMedia != null) {
            return fromMedia;
        }
        return text(mapOf(custom.get("standard_snapshot")).get(PatrolOpenRunSnapshotKeys.TASK_RECORD_STREAM_NAME));
    }

    private static Map<String, Object> mapOf(Object raw) {
        if (!(raw instanceof Map<?, ?> map)) {
            return Map.of();
        }
        Map<String, Object> copy = new java.util.LinkedHashMap<>();
        map.forEach((key, value) -> {
            if (key != null) {
                copy.put(String.valueOf(key), value);
            }
        });
        return copy;
    }

    private static String text(Object raw) {
        if (raw == null) {
            return null;
        }
        String value = String.valueOf(raw).trim();
        return value.isEmpty() ? null : value;
    }
}
