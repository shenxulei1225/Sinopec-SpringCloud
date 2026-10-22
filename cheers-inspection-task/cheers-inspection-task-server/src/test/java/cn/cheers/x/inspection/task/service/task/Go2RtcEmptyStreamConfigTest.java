package cn.cheers.x.inspection.task.service.task;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class Go2RtcEmptyStreamConfigTest {

    @Test
    void ensureEmptyStream_insertsNameUnderStreams() {
        String config = """
                streams:
                  robot-live:
                """;
        String updated = Go2RtcEmptyStreamConfig.ensureEmptyStream(config, "patrol-1-s1");
        assertTrue(updated.contains("streams:\n  patrol-1-s1:\n  robot-live:\n"));
    }

    @Test
    void ensureEmptyStream_leavesConfigWhenNameExists() {
        String config = "streams:\n  patrol-1-s1:\n  robot-live:\n";
        assertEquals(config, Go2RtcEmptyStreamConfig.ensureEmptyStream(config, "patrol-1-s1"));
    }

    @Test
    void ensureEmptyStream_rejectsNameThatWouldBreakConfig() {
        assertThrows(IllegalArgumentException.class,
                () -> Go2RtcEmptyStreamConfig.ensureEmptyStream("streams:\n", "patrol 1"));
    }

    @Test
    void removeStream_dropsNameAndItsAddress() {
        String config = """
                streams:
                  patrol-1-old:
                    - rtsp://old
                  robot-live:
                  patrol-1-s1:
                """;
        String updated = Go2RtcEmptyStreamConfig.removeStream(config, "patrol-1-old");
        assertTrue(updated.contains("streams:\n  robot-live:\n  patrol-1-s1:\n"));
        assertFalse(updated.contains("patrol-1-old"));
        assertFalse(updated.contains("rtsp://old"));
    }

    @Test
    void removeStream_leavesConfigWhenNameIsAbsent() {
        String config = "streams:\n  robot-live:\n";
        assertEquals(config, Go2RtcEmptyStreamConfig.removeStream(config, "patrol-1-old"));
    }

    @Test
    void streamNames_readsOnlyTheStreamList() {
        String config = """
                api:
                  listen: ":1984"
                streams:
                  robot-live:
                  patrol-1-s1:
                    - rtsp://live
                log:
                  level: info
                """;
        assertEquals(java.util.List.of("robot-live", "patrol-1-s1"), Go2RtcEmptyStreamConfig.streamNames(config));
    }

    @Test
    void ensureEmptyStream_rejectsConfigWithoutStreamList() {
        assertThrows(IllegalArgumentException.class,
                () -> Go2RtcEmptyStreamConfig.ensureEmptyStream("api:\n  listen: \":1984\"\n", "patrol-1-s1"));
    }
}
