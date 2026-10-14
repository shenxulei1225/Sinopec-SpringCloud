package cn.cheers.x.inspection.task.service.execution.analysis;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class StreamFrameAddressTest {

    @Test
    void fillsTheStreamNameIntoTheGatewayTemplate() {
        assertEquals(
                "http://127.0.0.1:1984/api/frame.jpeg?src=patrol-1-s1",
                StreamFrameAddress.jpegUrl(
                        "http://127.0.0.1:1984/api/frame.jpeg?src={streamName}",
                        "patrol-1-s1"));
    }

    @Test
    void missingStreamNameDoesNotInventAFrame() {
        assertNull(StreamFrameAddress.jpegUrl(
                "http://127.0.0.1:1984/api/frame.jpeg?src={streamName}",
                " "));
    }
}
