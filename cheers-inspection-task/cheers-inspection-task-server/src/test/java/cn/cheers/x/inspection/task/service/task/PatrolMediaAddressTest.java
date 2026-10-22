package cn.cheers.x.inspection.task.service.task;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PatrolMediaAddressTest {

    @Test
    void rtsp_usesPort8554() {
        PatrolMediaAddress media = new PatrolMediaAddress("47.117.153.179", "rtsp");
        assertEquals("rtsp://47.117.153.179:8554", media.ingestBaseUrl());
        assertEquals("http://47.117.153.179:1984", media.gatewayApiBaseUrl());
    }

    @Test
    void rtmp_usesPort1935() {
        PatrolMediaAddress media = new PatrolMediaAddress("47.117.153.179", "RTMP");
        assertEquals("rtmp://47.117.153.179:1935", media.ingestBaseUrl());
    }

    @Test
    void unknownProtocol_fails() {
        assertThrows(IllegalStateException.class, () -> new PatrolMediaAddress("47.117.153.179", "hls"));
    }

    @Test
    void hostWithScheme_fails() {
        assertThrows(IllegalStateException.class,
                () -> new PatrolMediaAddress("rtsp://47.117.153.179:8554", "rtsp"));
    }
}
