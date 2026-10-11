package cn.cheers.x.inspection.task.service.task;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TaskPatrolRecordingSupportTest {

    @Test
    void taskLevelRecording_robotWsEnabled() {
        assertTrue(TaskPatrolRecordingSupport.taskLevelRecordingEnabled("ROBOT", "robot-ws"));
        assertTrue(TaskPatrolRecordingSupport.usesServerIngest("robot-ws"));
    }

    @Test
    void dropFromInspectionMethod_whenTaskLevelEnabled() {
        assertTrue(TaskPatrolRecordingSupport.dropFromInspectionMethod(
                "startRecord", "UAV", "uav-ws"));
        assertFalse(TaskPatrolRecordingSupport.dropFromInspectionMethod(
                "takePhoto", "UAV", "uav-ws"));
    }
}
