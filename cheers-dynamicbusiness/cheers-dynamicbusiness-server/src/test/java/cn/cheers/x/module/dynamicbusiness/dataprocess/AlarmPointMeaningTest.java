package cn.cheers.x.module.dynamicbusiness.dataprocess;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AlarmPointMeaningTest {

    @Test
    void runningPhrasesDropTheDevicePrefix() {
        Map<String, String> phrases = AlarmPointMeaning.phrases(
                "为 1 时设备正在运行。为 0 时没有运行。这是状态，不是开关命令。");
        assertEquals("正在运行", phrases.get("1"));
        assertEquals("没有运行", phrases.get("0"));
    }

    @Test
    void remoteKeepsTheControlClause() {
        Map<String, String> phrases = AlarmPointMeaning.phrases(
                "处于远程时为 1，平台可以控制。为 0 时不在远程，平台不能控制。这是状态，不是开关命令。");
        assertEquals("处于远程，平台可以控制", phrases.get("1"));
        assertEquals("不在远程，平台不能控制", phrases.get("0"));
    }

    @Test
    void breakerDropsThisCircuitPrefix() {
        Map<String, String> phrases = AlarmPointMeaning.phrases("这一路分闸时为 1，未分闸时为 0。");
        assertEquals("分闸", phrases.get("1"));
        assertEquals("未分闸", phrases.get("0"));
    }
}
