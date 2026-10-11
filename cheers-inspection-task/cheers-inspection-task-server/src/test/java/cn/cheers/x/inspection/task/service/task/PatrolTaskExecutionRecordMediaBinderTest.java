package cn.cheers.x.inspection.task.service.task;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PatrolTaskExecutionRecordMediaBinderTest {

    @Test
    void mediaFromSnapshot_readsIngestAndStreamName() {
        Map<String, Object> media = PatrolTaskExecutionRecordMediaBinder.mediaFromSnapshot(Map.of(
                TaskPatrolRecordingSupport.SNAPSHOT_TASK_RECORD_INGEST_URL, "rtsp://m/ingest/patrol-1-s1",
                TaskPatrolRecordingSupport.SNAPSHOT_TASK_RECORD_STREAM_NAME, "patrol-1-s1"));
        assertEquals("rtsp://m/ingest/patrol-1-s1", media.get("ingestUrl"));
        assertEquals("patrol-1-s1", media.get("streamName"));
        assertEquals("task_patrol_full_record", media.get("scope"));
    }

    @Test
    void recordFileUrlTemplate_replacesStreamName() {
        PatrolTaskRecordIngestAllocator allocator = new PatrolTaskRecordIngestAllocator(
                "rtsp://127.0.0.1:8554/ingest",
                "http://127.0.0.1:1984/api/stream.mp4?src={streamName}");
        assertTrue(allocator.recordFileUrlForStream("patrol-2-x").isPresent());
        assertEquals(
                "http://127.0.0.1:1984/api/stream.mp4?src=patrol-2-x",
                allocator.recordFileUrlForStream("patrol-2-x").orElseThrow());
    }
}
