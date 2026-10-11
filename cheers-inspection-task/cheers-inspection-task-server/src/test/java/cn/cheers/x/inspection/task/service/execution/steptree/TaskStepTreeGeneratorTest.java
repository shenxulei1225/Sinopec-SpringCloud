package cn.cheers.x.inspection.task.service.execution.steptree;

import cn.cheers.x.framework.common.exception.ServiceException;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TaskStepTreeGeneratorTest {

    @Test
    void generate_robot_usesPathOrderAndHeadTail() {
        Map<String, Object> tree = TaskStepTreeGenerator.generate(
                TaskStepTreeGenerator.MEANS_ROBOT,
                List.of(
                        new TaskStepTreeGenerator.SelectedEquipment(10L, List.of(7L), "1#储罐"),
                        new TaskStepTreeGenerator.SelectedEquipment(20L, List.of(8L), "2#储罐")
                ),
                List.of(20L, 10L),
                Map.of(
                        7L, List.of(new TaskStepTreeGenerator.MethodAction(11L, "act-photo", "拍照")),
                        8L, List.of(new TaskStepTreeGenerator.MethodAction(12L, "act-move", "到达"))
                ),
                List.of(new TaskStepTreeGenerator.MethodAction(1L, "act-robot-head", "任务头")),
                List.of(new TaskStepTreeGenerator.MethodAction(2L, "act-robot-tail", "任务尾")),
                Map.of(7L, "外观", 8L, "液位")
        );

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> nodes = (List<Map<String, Object>>) tree.get("nodes");
        assertEquals("stop-start", nodes.get(0).get("nodeKey"));
        assertEquals("act-robot-head", nodes.get(1).get("refCode"));
        assertEquals("stop-20", nodes.get(2).get("nodeKey"));
        assertEquals(8L, nodes.get(3).get("refId"));
        assertEquals("item-8-20", nodes.get(4).get("parentNodeKey"));
        assertEquals("stop-10", nodes.get(5).get("nodeKey"));
        assertEquals(7L, nodes.get(6).get("refId"));
        assertEquals("act-robot-tail", nodes.get(nodes.size() - 1).get("refCode"));
    }

    @Test
    void generate_samePoint_keepsOneArrivalAndBothCheckItems() {
        String slot = "F-e49f76bdca3e4e1393e8e2f1cc0e7867";
        TaskStepTreeGenerator.MethodAction arrive = new TaskStepTreeGenerator.MethodAction(
                41L, "action-arrive", "到达指定位置", Map.of(), "n1", List.of(slot));
        TaskStepTreeGenerator.MethodAction photoA = new TaskStepTreeGenerator.MethodAction(
                22L, "act-shoot", "拍照", Map.of(), "n3", List.of("shot_count"));
        TaskStepTreeGenerator.MethodAction photoB = new TaskStepTreeGenerator.MethodAction(
                23L, "act-shoot-b", "拍照", Map.of(), "m3", List.of("shot_count"));
        Map<String, Object> tree = TaskStepTreeGenerator.generate(
                TaskStepTreeGenerator.MEANS_ROBOT,
                List.of(new TaskStepTreeGenerator.SelectedEquipment(10L, List.of(1L, 2L), "1#储罐")),
                List.of(10L),
                Map.of(
                        1L, List.of(arrive, photoA),
                        2L, List.of(arrive, photoB)),
                List.of(),
                List.of(),
                null,
                "起点",
                null,
                "终点",
                Map.of(1L, "外观", 2L, "液位"),
                Map.of(
                        TaskStepTreeGenerator.itemStopKey(10L, 1L), "tank-north",
                        TaskStepTreeGenerator.itemStopKey(10L, 2L), "tank-north"),
                Set.of(slot));

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> nodes = (List<Map<String, Object>>) tree.get("nodes");
        assertEquals(1, nodes.stream().filter(node -> "point".equals(node.get("hangTypeCode"))).count());
        assertEquals(1, nodes.stream().filter(node -> "action-arrive".equals(node.get("refCode"))).count());
        assertEquals(2, nodes.stream().filter(node -> "inspection_item".equals(node.get("hangTypeCode"))).count());
        assertEquals(2, nodes.stream().filter(node -> "拍照".equals(node.get("title"))).count());
        assertEquals("point-tank-north", nodes.stream()
                .filter(node -> "action-arrive".equals(node.get("refCode")))
                .findFirst().orElseThrow().get("parentNodeKey"));
    }

    @Test
    void generate_differentPoints_keepSeparateArrivals() {
        String slot = "F-e49f76bdca3e4e1393e8e2f1cc0e7867";
        TaskStepTreeGenerator.MethodAction arrive = new TaskStepTreeGenerator.MethodAction(
                41L, "action-arrive", "到达指定位置", Map.of(), "n1", List.of(slot));
        TaskStepTreeGenerator.MethodAction photo = new TaskStepTreeGenerator.MethodAction(
                22L, "act-shoot", "拍照", Map.of(), "n3", List.of("shot_count"));
        Map<String, Object> tree = TaskStepTreeGenerator.generate(
                TaskStepTreeGenerator.MEANS_ROBOT,
                List.of(new TaskStepTreeGenerator.SelectedEquipment(10L, List.of(1L, 2L), "1#储罐")),
                List.of(10L),
                Map.of(
                        1L, List.of(arrive, photo),
                        2L, List.of(arrive, photo)),
                List.of(),
                List.of(),
                null,
                "起点",
                null,
                "终点",
                Map.of(1L, "外观", 2L, "液位"),
                Map.of(
                        TaskStepTreeGenerator.itemStopKey(10L, 1L), "tank-north",
                        TaskStepTreeGenerator.itemStopKey(10L, 2L), "tank-south"),
                Set.of(slot));

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> nodes = (List<Map<String, Object>>) tree.get("nodes");
        assertEquals(2, nodes.stream().filter(node -> "point".equals(node.get("hangTypeCode"))).count());
        assertEquals(2, nodes.stream().filter(node -> "action-arrive".equals(node.get("refCode"))).count());
    }

    @Test
    void generate_writesActionOwnershipSnapshot() {
        TaskStepTreeGenerator.MethodAction analysis = new TaskStepTreeGenerator.MethodAction(
                22L, "act-analysis", "智能分析", Map.of(), "n1", List.of(),
                "INTELLIGENT_ANALYSIS", 41L);

        Map<String, Object> tree = TaskStepTreeGenerator.generate(
                TaskStepTreeGenerator.MEANS_CAMERA,
                List.of(new TaskStepTreeGenerator.SelectedEquipment(10L, List.of(1L), "1#储罐")),
                List.of(10L),
                Map.of(1L, List.of(analysis)),
                List.of(),
                List.of(),
                Map.of(1L, "跑冒滴漏检查"));

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> nodes = (List<Map<String, Object>>) tree.get("nodes");
        Map<String, Object> action = nodes.stream()
                .filter(node -> "act-analysis".equals(node.get("refCode")))
                .findFirst()
                .orElseThrow();
        assertEquals("INTELLIGENT_ANALYSIS", action.get("actionDomain"));
        assertEquals(41L, action.get("analysisCapabilityId"));
        assertFalse(action.containsKey("protocolDispatchRole"));
    }

    @Test
    void generate_manual_rejectsHeadTail() {
        ServiceException ex = assertThrows(ServiceException.class, () -> TaskStepTreeGenerator.generate(
                TaskStepTreeGenerator.MEANS_MANUAL,
                List.of(new TaskStepTreeGenerator.SelectedEquipment(10L, List.of(7L), "1#储罐")),
                null,
                Map.of(7L, List.of(new TaskStepTreeGenerator.MethodAction(11L, "act-walk", "走到点"))),
                List.of(new TaskStepTreeGenerator.MethodAction(1L, "act-robot-head", "任务头")),
                List.of(),
                Map.of(7L, "外观")
        ));
        assertTrue(ex.getMessage().contains("不挂"));
    }

    @Test
    void generate_camera_usesSelectionOrder() {
        Map<String, Object> tree = TaskStepTreeGenerator.generate(
                TaskStepTreeGenerator.MEANS_CAMERA,
                List.of(
                        new TaskStepTreeGenerator.SelectedEquipment(10L, List.of(7L), "1#储罐"),
                        new TaskStepTreeGenerator.SelectedEquipment(20L, List.of(8L), "2#储罐")
                ),
                null,
                Map.of(
                        7L, List.of(new TaskStepTreeGenerator.MethodAction(11L, "act-snap", "抓拍")),
                        8L, List.of(new TaskStepTreeGenerator.MethodAction(12L, "act-snap2", "抓拍"))
                ),
                List.of(),
                List.of(),
                Map.of(7L, "外观", 8L, "液位")
        );

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> nodes = (List<Map<String, Object>>) tree.get("nodes");
        assertEquals("stop-10", nodes.get(0).get("nodeKey"));
        assertEquals(7L, nodes.get(1).get("refId"));
        assertEquals("stop-20", nodes.get(3).get("nodeKey"));
        assertEquals(8L, nodes.get(4).get("refId"));
    }

    @Test
    void generate_sameItemOnTwoDevices_keepsDistinctStepCodes() {
        TaskStepTreeGenerator.MethodAction fourth = new TaskStepTreeGenerator.MethodAction(
                37L, "act-video", "仪表读数识别（视频智能分析）开启");
        List<TaskStepTreeGenerator.MethodAction> actions = List.of(
                new TaskStepTreeGenerator.MethodAction(11L, "act-hold", "停留观察"),
                new TaskStepTreeGenerator.MethodAction(12L, "act-shoot", "拍照"),
                new TaskStepTreeGenerator.MethodAction(13L, "act-hold-2", "停留观察"),
                fourth);
        Map<String, Object> tree = TaskStepTreeGenerator.generate(
                TaskStepTreeGenerator.MEANS_ROBOT,
                List.of(
                        new TaskStepTreeGenerator.SelectedEquipment(10L, List.of(3146L), "北罐组西罐1底"),
                        new TaskStepTreeGenerator.SelectedEquipment(20L, List.of(3146L), "北罐组西罐2底")
                ),
                List.of(10L, 20L),
                Map.of(3146L, actions),
                List.of(),
                List.of(),
                Map.of(3146L, "跑冒滴漏检查")
        );

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> nodes = (List<Map<String, Object>>) tree.get("nodes");
        List<String> keys = nodes.stream().map(node -> String.valueOf(node.get("nodeKey"))).toList();
        assertEquals(keys.size(), (int) keys.stream().distinct().count());
        assertTrue(keys.contains("act-3146-10-4"));
        assertTrue(keys.contains("act-3146-20-4"));
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
        assertEquals(3, nodes.size());
        assertEquals("stop-10", nodes.get(0).get("nodeKey"));
        assertEquals("equipment", nodes.get(0).get("hangTypeCode"));
        assertEquals("北罐区1#储罐", nodes.get(0).get("title"));
        assertEquals(7L, nodes.get(1).get("refId"));
        assertEquals("外观", nodes.get(1).get("title"));
        assertEquals("item-7-10", nodes.get(2).get("parentNodeKey"));
    }

    @Test
    void generate_uav_writesTaskStartEndOnHeadTail() {
        Map<String, Object> tree = TaskStepTreeGenerator.generate(
                TaskStepTreeGenerator.MEANS_UAV,
                List.of(new TaskStepTreeGenerator.SelectedEquipment(10L, List.of(7L), "1#储罐")),
                List.of(10L),
                Map.of(7L, List.of(new TaskStepTreeGenerator.MethodAction(11L, "act-photo", "拍照"))),
                List.of(new TaskStepTreeGenerator.MethodAction(
                        1L, "act-uav-head", "任务头", Map.of("location_ref", "pad-a"))),
                List.of(new TaskStepTreeGenerator.MethodAction(
                        2L, "act-uav-tail", "任务尾", Map.of("location_ref", "pad-b"))),
                "pad-a",
                "pad-b",
                Map.of(7L, "外观")
        );

        assertEquals("pad-a", tree.get("startStopId"));
        assertEquals("pad-b", tree.get("endStopId"));
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> nodes = (List<Map<String, Object>>) tree.get("nodes");
        assertEquals("pad-a", nodes.get(0).get("refCode"));
        assertEquals("act-uav-head", nodes.get(1).get("refCode"));
        assertEquals(Map.of("location_ref", "pad-a"), nodes.get(1).get("params"));
        assertEquals("act-uav-tail", nodes.get(nodes.size() - 1).get("refCode"));
        assertEquals(Map.of("location_ref", "pad-b"), nodes.get(nodes.size() - 1).get("params"));
    }
}
