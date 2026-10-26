package cn.cheers.x.inspection.task.service.execution.pagepush;

import cn.cheers.x.inspection.task.api.dto.ReplayPoseReqDTO;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class InspectionExecutionPagePublisherTest {

    @Test
    void trajectoryUsesWrittenLatitudeLongitudeAndHeight() {
        ReplayPoseReqDTO pose = new ReplayPoseReqDTO();
        pose.setX(120.1);
        pose.setY(30.2);
        pose.setZ(8.0);
        pose.setT(99L);

        Map<String, Object> body = InspectionExecutionPagePublisher.trajectoryBody(15L, pose);

        assertEquals(15L, body.get("executionRecordId"));
        assertEquals(30.2, body.get("lat"));
        assertEquals(120.1, body.get("lng"));
        assertEquals(8.0, body.get("height"));
        assertEquals(99L, body.get("time"));
    }

    @Test
    void stepKeepsAddressAndDropsPhotoBytes() {
        Map<String, Object> photo = new LinkedHashMap<>();
        photo.put("data", "AAAA");
        photo.put("url", "/files/step-1.jpg");
        Map<String, Object> update = new LinkedHashMap<>();
        update.put("stepCode", "seq-1");
        update.put("status", "completed");
        update.put("resultPayload", Map.of(
                "operatePoint", Map.of("lat", 30.2, "lng", 120.1),
                "result", Map.of("electricity", 80, "photos", List.of(photo))));

        Map<String, Object> body = InspectionExecutionPagePublisher.stepBody(15L, update);

        assertEquals("seq-1", body.get("stepCode"));
        assertEquals("completed", body.get("status"));
        assertEquals(30.2, body.get("lat"));
        assertEquals(120.1, body.get("lng"));
        assertEquals(80, body.get("electricity"));
        assertEquals(List.of("/files/step-1.jpg"), body.get("photoAddresses"));
    }

    @Test
    void topicFollowsTheExecutionRecord() {
        String topic = InspectionExecutionPagePublisher.topicOf(
                "inspection/execution/{执行记录编号}/trajectory", 15L);
        assertEquals("inspection/execution/15/trajectory", topic);
        assertNull(InspectionExecutionPagePublisher.topicOf(" ", 15L));
    }
}
