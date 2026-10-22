package cn.cheers.x.inspection.task.service.task;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PatrolTaskRecordIngestAllocatorTest {

    @Test
    void allocate_buildsPerSlotUrl() {
        PatrolTaskRecordIngestAllocator allocator =
                new PatrolTaskRecordIngestAllocator("rtsp://127.0.0.1:8554/ingest/", "");
        assertEquals(
                "rtsp://127.0.0.1:8554/ingest/patrol-9-slot-a",
                allocator.allocate(9L, "slot-a"));
    }

    @Test
    void streamName_planPointAlreadyContainsTask_doesNotRepeat() {
        PatrolTaskRecordIngestAllocator allocator =
                new PatrolTaskRecordIngestAllocator("rtmp://47.117.153.179:1935", "");
        assertEquals("patrol-task-910104-1", allocator.streamName(910104L, "patrol-task-910104-1"));
        assertEquals(
                "rtmp://47.117.153.179:1935/patrol-task-910104-1",
                allocator.allocate(910104L, "patrol-task-910104-1"));
    }

    @Test
    void belongsToTask_matchesCurrentAndDoubledNames() {
        assertTrue(PatrolTaskRecordIngestAllocator.belongsToTask("patrol-task-910104-42", 910104L));
        assertTrue(PatrolTaskRecordIngestAllocator.belongsToTask(
                "patrol-910104-patrol-task-910104-42", 910104L));
        assertFalse(PatrolTaskRecordIngestAllocator.belongsToTask("patrol-task-910101-0", 910104L));
        assertFalse(PatrolTaskRecordIngestAllocator.belongsToTask("robot-live", 910104L));
    }

    @Test
    void allocate_missingBaseUrl_fails() {
        PatrolTaskRecordIngestAllocator allocator = new PatrolTaskRecordIngestAllocator("", "");
        assertThrows(RuntimeException.class, () -> allocator.allocate(1L, "s1"));
    }
}
