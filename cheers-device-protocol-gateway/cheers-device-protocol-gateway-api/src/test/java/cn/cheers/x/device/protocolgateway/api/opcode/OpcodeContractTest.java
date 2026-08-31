package cn.cheers.x.device.protocolgateway.api.opcode;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

/**
 * 锁定设计稿码表，防止机器人移动再次退回错误的 200101。
 */
class OpcodeContractTest {

    @Test
    void transportCommandSendIs500104() {
        assertEquals(500104, TransportOpcode.COMMAND_SEND.code());
        assertEquals(500201, TransportOpcode.STARTUP_EXECUTE.code());
        assertEquals(500205, TransportOpcode.TASK_STOP.code());
    }

    @Test
    void robotMoveIs200102Not200101() {
        assertEquals(200102, RobotMissionOpcode.MOVE.code());
        assertNotEquals(200101, RobotMissionOpcode.MOVE.code());
        assertEquals(200301, RobotMissionOpcode.PHOTO.code());
    }

    @Test
    void deviceTaskStatusCodesMatchLegacy() {
        assertEquals(300101, DeviceTaskStatusCode.START_SUCCESS);
        assertEquals(300105, DeviceTaskStatusCode.TASK_COMPLETED);
        assertEquals(300102, DeviceTaskStatusCode.START_FAILURE);
    }
}
