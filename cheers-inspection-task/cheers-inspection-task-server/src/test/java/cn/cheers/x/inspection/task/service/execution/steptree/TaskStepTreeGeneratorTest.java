package cn.cheers.x.inspection.task.service.execution.steptree;

import cn.cheers.x.framework.common.exception.ServiceException;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TaskStepTreeGeneratorTest {

    @Test
    void generate_robot_usesPathOrderAndHeadTail() {
        Map<String, Object> tree = TaskStepTreeGenerator.generate(
                TaskStepTreeGenerator.MEANS_ROBOT,
                List.of(
                        new TaskStepTreeGenerator.SelectedEquipment(10L, List.of(7L)),
                        new TaskStepTreeGenerator.SelectedEquipment(20L, List.of(8L))
                ),
                List.of(20L, 10L),
                Map.of(
                        7L, List.of(new TaskStepTreeGenerator.MethodAction(11L, "act-photo", "拍照")),
                        8L, List.of(new TaskStepTreeGenerator.MethodAction(12L, "act-move", "到达"))
                ),
                List.of(new TaskStepTreeGenerator.MethodAction(1L, "act-robot-head", "任务头")),
                List.of(new TaskStepTreeGenerator.MethodAction(2L, "act-robot-tail", "任务尾"))
        );

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> nodes = (List<Map<String, Object>>) tree.get("nodes");
        assertEquals("act-robot-head", nodes.get(0).get("refCode"));
        assertEquals(8L, nodes.get(1).get("refId"));
        assertEquals("item-8-20", nodes.get(2).get("parentNodeKey"));
        assertEquals(7L, nodes.get(3).get("refId"));
        assertEquals("act-robot-tail", nodes.get(nodes.size() - 1).get("refCode"));
    }

    @Test
    void generate_manual_rejectsHeadTail() {
        ServiceException ex = assertThrows(ServiceException.class, () -> TaskStepTreeGenerator.generate(
                TaskStepTreeGenerator.MEANS_MANUAL,
                List.of(new TaskStepTreeGenerator.SelectedEquipment(10L, List.of(7L))),
                null,
                Map.of(7L, List.of(new TaskStepTreeGenerator.MethodAction(11L, "act-walk", "走到点"))),
                List.of(new TaskStepTreeGenerator.MethodAction(1L, "act-robot-head", "任务头")),
                List.of()
        ));
        assertTrue(ex.getMessage().contains("不挂"));
    }

    @Test
    void generate_camera_usesSelectionOrder() {
        Map<String, Object> tree = TaskStepTreeGenerator.generate(
                TaskStepTreeGenerator.MEANS_CAMERA,
                List.of(
                        new TaskStepTreeGenerator.SelectedEquipment(10L, List.of(7L)),
                        new TaskStepTreeGenerator.SelectedEquipment(20L, List.of(8L))
                ),
                null,
                Map.of(
                        7L, List.of(new TaskStepTreeGenerator.MethodAction(11L, "act-snap", "抓拍")),
                        8L, List.of(new TaskStepTreeGenerator.MethodAction(12L, "act-snap2", "抓拍"))
                ),
                List.of(),
                List.of()
        );

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> nodes = (List<Map<String, Object>>) tree.get("nodes");
        assertEquals(7L, nodes.get(0).get("refId"));
        assertEquals(8L, nodes.get(2).get("refId"));
    }

    @Test
    void generate_missingMethod_throwsNamedGap() {
        ServiceException ex = assertThrows(ServiceException.class, () -> TaskStepTreeGenerator.generate(
                TaskStepTreeGenerator.MEANS_UAV,
                List.of(new TaskStepTreeGenerator.SelectedEquipment(10L, List.of(7L), "北罐区1#储罐")),
                List.of(10L),
                Map.of(),
                List.of(),
                List.of(),
                Map.of(7L, "液位")
        ));
        assertTrue(ex.getMessage().contains("北罐区1#储罐"));
        assertTrue(ex.getMessage().contains("液位"));
        assertTrue(ex.getMessage().contains("无人机"));
    }

    @Test
    void generate_skipsItemsWithoutThisMeansAndWritesReadyOnes() {
        Map<String, Object> tree = TaskStepTreeGenerator.generate(
                TaskStepTreeGenerator.MEANS_UAV,
                List.of(
                        new TaskStepTreeGenerator.SelectedEquipment(10L, List.of(7L), "北罐区1#储罐"),
                        new TaskStepTreeGenerator.SelectedEquipment(20L, List.of(8L), "北罐区2#储罐")
                ),
                List.of(10L, 20L),
                Map.of(7L, List.of(new TaskStepTreeGenerator.MethodAction(11L, "act-photo", "拍照"))),
                List.of(),
                List.of(),
                Map.of(7L, "外观", 8L, "液位")
        );

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> nodes = (List<Map<String, Object>>) tree.get("nodes");
        assertEquals(2, nodes.size());
        assertEquals(7L, nodes.get(0).get("refId"));
        assertEquals("item-7-10", nodes.get(1).get("parentNodeKey"));
    }

    @Test
    void generate_uav_writesTaskStartEndOnHeadTail() {
        Map<String, Object> tree = TaskStepTreeGenerator.generate(
                TaskStepTreeGenerator.MEANS_UAV,
                List.of(new TaskStepTreeGenerator.SelectedEquipment(10L, List.of(7L))),
                List.of(10L),
                Map.of(7L, List.of(new TaskStepTreeGenerator.MethodAction(11L, "act-photo", "拍照"))),
                List.of(new TaskStepTreeGenerator.MethodAction(
                        1L, "act-uav-head", "任务头", Map.of("location_ref", "pad-a"))),
                List.of(new TaskStepTreeGenerator.MethodAction(
                        2L, "act-uav-tail", "任务尾", Map.of("location_ref", "pad-b"))),
                "pad-a",
                "pad-b"
        );

        assertEquals("pad-a", tree.get("startStopId"));
        assertEquals("pad-b", tree.get("endStopId"));
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> nodes = (List<Map<String, Object>>) tree.get("nodes");
        assertEquals("act-uav-head", nodes.get(0).get("refCode"));
        assertEquals(Map.of("location_ref", "pad-a"), nodes.get(0).get("params"));
        assertEquals("act-uav-tail", nodes.get(nodes.size() - 1).get("refCode"));
        assertEquals(Map.of("location_ref", "pad-b"), nodes.get(nodes.size() - 1).get("params"));
    }
}
