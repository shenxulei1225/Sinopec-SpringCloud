package cn.cheers.x.module.dynamicbusiness.service.action;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ActionDeletedChildQueryFilterTest {

    @Test
    void dropsDeletedActionGroupMember() {
        Map<String, Object> tree = Map.of(
                "version", 1,
                "members", List.of(
                        Map.of("memberKey", "m1", "actionCode", "act-deleted"),
                        Map.of("memberKey", "m2", "actionCode", "act-live")));

        @SuppressWarnings("unchecked")
        Map<String, Object> next = (Map<String, Object>) ActionDeletedChildQueryFilter.omitValue(
                tree, Set.of("act-deleted"));
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> members = (List<Map<String, Object>>) next.get("members");

        assertEquals(List.of("act-live"), members.stream().map(row -> row.get("actionCode")).toList());
    }

    @Test
    void dropsDeletedChildFromMethodTreeAndKeepsLiveSibling() {
        Map<String, Object> tree = Map.of(
                "version", 2,
                "methods", List.of(Map.of(
                        "executionMeans", "UAV",
                        "actionTree", List.of(
                                Map.of("actionCode", "act-uav-battery-check", "children", List.of()),
                                Map.of("actionCode", "act-uav-takeoff", "children", List.of())))));
        Object next = ActionDeletedChildQueryFilter.omitValue(tree, Set.of("act-uav-battery-check"));
        @SuppressWarnings("unchecked")
        Map<String, Object> methodsHolder = (Map<String, Object>) next;
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> methods = (List<Map<String, Object>>) methodsHolder.get("methods");
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> actionTree = (List<Map<String, Object>>) methods.get(0).get("actionTree");
        assertEquals(List.of("act-uav-takeoff"), actionTree.stream().map(node -> node.get("actionCode")).toList());
    }

    @Test
    void dropsDeletedCheckItemStepByActionId() {
        Map<String, Object> tree = Map.of(
                "methods", List.of(Map.of(
                        "methodKey", "ROBOT",
                        "stepTree", List.of(
                                Map.of("actionId", "act-robot-arrive", "order", 1),
                                Map.of("actionId", "act-robot-shoot", "order", 2)))));
        Object next = ActionDeletedChildQueryFilter.omitValue(tree, Set.of("act-robot-shoot"));
        @SuppressWarnings("unchecked")
        Map<String, Object> holder = (Map<String, Object>) next;
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> methods = (List<Map<String, Object>>) holder.get("methods");
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> steps = (List<Map<String, Object>>) methods.get(0).get("stepTree");
        assertEquals(List.of("act-robot-arrive"), steps.stream().map(node -> node.get("actionId")).toList());
    }

    @Test
    void dropsDeletedNodeWithoutPromotingItsChildren() {
        Map<String, Object> node = Map.of(
                "actionCode", "act-deleted",
                "children", List.of(Map.of("actionCode", "act-live", "children", List.of())));
        Object next = ActionDeletedChildQueryFilter.omitValue(List.of(node), Set.of("act-deleted"));
        assertEquals(List.of(), next);
    }

    @Test
    void dropsDeletedCodeFromChildIdList() {
        Object next = ActionDeletedChildQueryFilter.omitValue(
                List.of("act-uav-battery-check", "act-uav-takeoff"),
                Set.of("act-uav-battery-check"));
        assertEquals(List.of("act-uav-takeoff"), next);
    }
}
