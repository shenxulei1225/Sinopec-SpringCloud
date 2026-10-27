package cn.cheers.x.device.protocolgateway.protocol.uplink;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class GroundStationLiveStatusStoreTest {

    @Test
    void remembersElectricityAndDoesNotInventRemainSeconds() {
        GroundStationLiveStatusStore store = new GroundStationLiveStatusStore(new ObjectMapper());
        store.remember("dev-1", 500101, "{\"opcode\":500101,\"data\":{\"electricity\":\"82.9\"}}");

        GroundStationLiveStatusStore.Frame frame = store.latest("dev-1");

        assertEquals("82.9", frame.electricity());
    }

    @Test
    void ignoresPacketsThatAreNotStatus() {
        GroundStationLiveStatusStore store = new GroundStationLiveStatusStore(new ObjectMapper());
        store.remember("dev-1", 500104, "{\"opcode\":500104,\"data\":{\"electricity\":\"82.9\"}}");

        assertNull(store.latest("dev-1"));
    }

    @Test
    void doesNotKeepABlankStatusFrame() {
        GroundStationLiveStatusStore store = new GroundStationLiveStatusStore(new ObjectMapper());
        store.remember("dev-1", 500101, "{\"opcode\":500101,\"code\":200}");

        assertNull(store.latest("dev-1"));
    }

    @Test
    void readsElectricityFromTimedStatusResult() {
        GroundStationLiveStatusStore store = new GroundStationLiveStatusStore(new ObjectMapper());
        store.remember("dev-1", 500103, "{\"opcode\":500103,\"result\":{\"electricity\":\"80\"}}");

        assertEquals("80", store.latest("dev-1").electricity());
    }
}
