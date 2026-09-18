package cn.cheers.x.device.protocolgateway.api.opcode;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InstructionExchangeCatalogTest {

    @Test
    void syncWait_commandAndStartup() {
        assertEquals(InstructionExchangeMode.SYNC_WAIT_RESULT,
                InstructionExchangeCatalog.modeOf(500104));
        assertEquals(InstructionExchangeMode.SYNC_WAIT_RESULT,
                InstructionExchangeCatalog.modeOf(500201));
        assertTrue(InstructionExchangeCatalog.isSyncWaitResult(500101));
    }

    @Test
    void asyncAndActive_doNotWait() {
        assertEquals(InstructionExchangeMode.ASYNC_RESULT,
                InstructionExchangeCatalog.modeOf(500202));
        assertEquals(InstructionExchangeMode.ACTIVE_UPLINK,
                InstructionExchangeCatalog.modeOf(500203));
        assertFalse(InstructionExchangeCatalog.isSyncWaitResult(500202));
    }

    @Test
    void heartbeatAndAck() {
        assertEquals(InstructionExchangeMode.HEARTBEAT,
                InstructionExchangeCatalog.modeOf(500105));
        assertEquals(InstructionExchangeMode.ACK,
                InstructionExchangeCatalog.modeOf(500106));
    }
}
