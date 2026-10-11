package cn.cheers.x.inspection.task.service.task;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
    void allocate_missingBaseUrl_fails() {
        PatrolTaskRecordIngestAllocator allocator = new PatrolTaskRecordIngestAllocator("", "");
        assertThrows(RuntimeException.class, () -> allocator.allocate(1L, "s1"));
    }
}
