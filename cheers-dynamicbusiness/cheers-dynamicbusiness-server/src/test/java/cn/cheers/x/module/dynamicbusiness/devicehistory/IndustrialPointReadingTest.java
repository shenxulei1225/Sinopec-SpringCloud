package cn.cheers.x.module.dynamicbusiness.devicehistory;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class IndustrialPointReadingTest {

    @Test
    void readsRunningFromTheFieldDescription() {
        IndustrialPointReading.Line line = IndustrialPointReading.read(
                "为 1 时设备正在运行。为 0 时没有运行。这是状态，不是开关命令。", 1);

        assertEquals("设备正在运行", line.text());
        assertNull(line.allowsControl());
    }

    @Test
    void remoteOneAllowsControlWhenTheDescriptionSaysSo() {
        IndustrialPointReading.Line line = IndustrialPointReading.read(
                "处于远程时为 1，平台可以控制。为 0 时不在远程，平台不能控制。这是状态，不是开关命令。", 1);

        assertTrue(line.allowsControl());
    }

    @Test
    void manholeRemoteZeroAllowsControl() {
        IndustrialPointReading.Line line = IndustrialPointReading.read(
                "处于远程时为 0，平台可以控制。为 1 时不在远程，平台不能控制。这是状态，不是开关命令。", 0);

        assertEquals("平台可以控制", line.text());
        assertTrue(line.allowsControl());
    }

    @Test
    void doesNotGuessWhenTheValueIsNotZeroOrOne() {
        assertNull(IndustrialPointReading.read("为 1 时设备正在运行。为 0 时没有运行。", 2));
    }

    @Test
    void localDoesNotAllowControl() {
        IndustrialPointReading.Line line = IndustrialPointReading.read(
                "处于远程时为 1，平台可以控制。为 0 时不在远程，平台不能控制。", 0);

        assertFalse(line.allowsControl());
    }
}
