package cn.cheers.x.module.dynamicbusiness.service.execution;

import cn.cheers.x.framework.common.exception.ServiceException;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PatrolAnalysisAlarmRecorderTest {

    @Test
    void readsTheOnlyExecutionDevice() {
        Object binding = Map.of("ROBOT", Map.of("equipmentId", 900155));
        assertEquals(900155L, PatrolAnalysisAlarmRecorder.requireSingleExecutionEquipmentId(binding));
    }

    @Test
    void rejectsMissingOrMultipleExecutionDevices() {
        ServiceException missing = assertThrows(ServiceException.class,
                () -> PatrolAnalysisAlarmRecorder.requireSingleExecutionEquipmentId(Map.of()));
        assertTrue(missing.getMessage().contains("没有写下执行设备"));

        ServiceException multiple = assertThrows(ServiceException.class,
                () -> PatrolAnalysisAlarmRecorder.requireSingleExecutionEquipmentId(Map.of(
                        "ROBOT", Map.of("equipmentId", 1),
                        "UAV", Map.of("equipmentId", 2))));
        assertTrue(multiple.getMessage().contains("多台执行设备"));
    }

    @Test
    void onlyAlarmPayloadIsRecorded() {
        assertTrue(PatrolAnalysisAlarmRecorder.isAlarm(Map.of("analysisResult", "告警")));
        assertFalse(PatrolAnalysisAlarmRecorder.isAlarm(Map.of("analysisResult", "正常")));
    }
}
