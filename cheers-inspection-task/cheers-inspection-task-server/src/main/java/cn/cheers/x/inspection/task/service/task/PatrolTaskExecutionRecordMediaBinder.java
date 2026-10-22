package cn.cheers.x.inspection.task.service.task;

import cn.cheers.x.framework.common.exception.util.ServiceExceptionUtil;
import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.module.dynamicbusiness.api.entity.EntityRpcApi;
import cn.cheers.x.module.dynamicbusiness.api.entity.dto.EntityRespDTO;
import cn.cheers.x.module.dynamicbusiness.api.entity.dto.EntityWriteReqDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.LinkedHashMap;
import java.util.Map;

import static cn.cheers.x.inspection.task.service.execution.impl.InspectionTaskStartExecutionServiceImpl.PATROL_EXEC_MODEL_CODE;
import static cn.cheers.x.inspection.task.service.execution.impl.InspectionTaskStartExecutionServiceImpl.PATROL_RECORD_TYPE;

/**
 * 任务级全程录像：把物化快照里的收流信息写入执行账，供执行记录页与回放读取。
 * <p>负责：从 standard_snapshot 同步 task_patrol_record_media；整次结束时补落盘回放地址（模板配置时）。
 * <p>不负责：向 go2rtc 注册流、转码；不修改步骤完成态。
 */
@Service
@RequiredArgsConstructor
public class PatrolTaskExecutionRecordMediaBinder {

    private final EntityRpcApi entityRpcApi;
    private final PatrolTaskRecordIngestAllocator patrolTaskRecordIngestAllocator;

    /** 开跑后：把快照里的收流地址与流名写到执行账 custom_fields。 */
    public void bindFromSnapshot(Long executionRecordId, Object standardSnapshot) {
        Map<String, Object> media = mediaFromSnapshot(standardSnapshot);
        if (media.isEmpty()) {
            return;
        }
        writeMedia(executionRecordId, media);
    }

    /** 整次结束：在已有绑定上补 recordFileUrl。地址用流媒体服务器主机拼出。 */
    public void refreshRecordFileUrl(Long executionRecordId) {
        EntityRespDTO record = requireRecord(executionRecordId);
        Map<String, Object> existing = readMediaMap(record);
        if (existing.isEmpty()) {
            return;
        }
        String streamName = text(existing.get("streamName"));
        if (!StringUtils.hasText(streamName)) {
            return;
        }
        patrolTaskRecordIngestAllocator.recordFileUrlForStream(streamName).ifPresent(url -> {
            Map<String, Object> merged = new LinkedHashMap<>(existing);
            merged.put("recordFileUrl", url);
            writeMedia(executionRecordId, merged);
        });
    }

    static Map<String, Object> mediaFromSnapshot(Object standardSnapshot) {
        if (!(standardSnapshot instanceof Map<?, ?> snapshot)) {
            return Map.of();
        }
        String ingestUrl = text(snapshot.get(TaskPatrolRecordingSupport.SNAPSHOT_TASK_RECORD_INGEST_URL));
        String streamName = text(snapshot.get(TaskPatrolRecordingSupport.SNAPSHOT_TASK_RECORD_STREAM_NAME));
        if (!StringUtils.hasText(ingestUrl) && !StringUtils.hasText(streamName)) {
            return Map.of();
        }
        Map<String, Object> media = new LinkedHashMap<>();
        if (StringUtils.hasText(ingestUrl)) {
            media.put("ingestUrl", ingestUrl.trim());
        }
        if (StringUtils.hasText(streamName)) {
            media.put("streamName", streamName.trim());
        }
        media.put("scope", "task_patrol_full_record");
        return Map.copyOf(media);
    }

    private void writeMedia(Long executionRecordId, Map<String, Object> media) {
        EntityWriteReqDTO write = new EntityWriteReqDTO();
        write.setId(executionRecordId);
        write.setEntityTypeCode(PATROL_RECORD_TYPE);
        write.setModelCode(PATROL_EXEC_MODEL_CODE);
        write.setFields(Map.of(TaskPatrolRecordingSupport.CUSTOM_FIELD_TASK_PATROL_RECORD_MEDIA, media));
        CommonResult<Boolean> result = entityRpcApi.updateEntityFields(write);
        if (result == null || !result.isSuccess()) {
            throw ServiceExceptionUtil.invalidParamException("任务级录像绑定没有写入执行账");
        }
    }

    private EntityRespDTO requireRecord(Long executionRecordId) {
        CommonResult<EntityRespDTO> result = entityRpcApi.getEntity(executionRecordId, PATROL_RECORD_TYPE);
        if (result == null || !result.isSuccess() || result.getData() == null) {
            throw ServiceExceptionUtil.invalidParamException("执行账不存在：" + executionRecordId);
        }
        return result.getData();
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> readMediaMap(EntityRespDTO record) {
        if (record.getCustomFields() == null) {
            return Map.of();
        }
        Object raw = record.getCustomFields().get(TaskPatrolRecordingSupport.CUSTOM_FIELD_TASK_PATROL_RECORD_MEDIA);
        if (raw instanceof Map<?, ?> map) {
            Map<String, Object> copy = new LinkedHashMap<>();
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                if (entry.getKey() != null) {
                    copy.put(String.valueOf(entry.getKey()), entry.getValue());
                }
            }
            return copy;
        }
        Object snapshot = record.getCustomFields().get("standard_snapshot");
        return mediaFromSnapshot(snapshot);
    }

    private static String text(Object raw) {
        return raw == null ? null : String.valueOf(raw).trim();
    }
}
