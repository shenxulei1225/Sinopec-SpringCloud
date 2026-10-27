package cn.cheers.x.inspection.task.service.execution.steptree;

import cn.cheers.x.device.protocolgateway.api.protocol.ProtocolDispatchProfile;
import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.module.dynamicbusiness.api.execution.dto.TaskExecutionStartReqDTO;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TaskStepTreeAssemblerTest {

    @Test
    void assemble_dispatchesEachActionWithItsOwnParams() {
        List<TaskStepNode> nodes = List.of(
                action("n-move", 1, null, 11L, "act-arrive", "到达指定位置",
                        Map.of("location_ref", Map.of("FLD-PNT-002", "node-1"))),
                item("n-item", 2, null, 7L, "检查阀"),
                action("n-aim", 3, "n-item", 21L, "act-aim", "调整拍摄角度",
                        Map.of("yaw", 30, "pitch", -10)),
                action("n-photo", 4, "n-item", 22L, "act-photo", "拍照", Map.of("shot_count", 2)),
                action("n-human", 5, "n-item", 33L, "act-human", "人工确认", Map.of(),
                        "MANUAL", null),
                action("n-ai", 6, "n-item", 44L, "act-ai", "智能分析", Map.of(),
                        "INTELLIGENT_ANALYSIS", 41L),
                action("n-boot", 7, null, 51L, "act-robot-self-check", "开机自检", Map.of(),
                        "PLATFORM", null),
                action("n-video", 8, "n-item", 37L, "act-video", "仪表读数识别（视频智能分析）开启",
                        Map.of(), "INTELLIGENT_ANALYSIS", 41L)
        );

        TaskStepTreeAssembler.AssembledOpenRun result = TaskStepTreeAssembler.assemble(
                nodes, code -> Optional.empty());

        assertEquals(3, result.dispatchActions().size());
        assertEquals(11L, result.dispatchActions().get(0).actionId());
        assertEquals("node-1",
                ((Map<?, ?>) result.dispatchActions().get(0).params().get("location_ref")).get("FLD-PNT-002"));
        assertEquals(21L, result.dispatchActions().get(1).actionId());
        assertEquals(30, result.dispatchActions().get(1).params().get("yaw"));
        assertEquals(-10, result.dispatchActions().get(1).params().get("pitch"));
        assertEquals(22L, result.dispatchActions().get(2).actionId());
        assertEquals(2, result.dispatchActions().get(2).params().get("shot_count"));
        assertFalse(result.dispatchActions().get(2).params().containsKey("yaw"));
        assertEquals("seq-0", result.bindings().get(0).stepCode());
        assertEquals("seq-1", result.bindings().get(1).stepCode());
        assertEquals("seq-2", result.bindings().get(2).stepCode());
        assertEquals(7L, result.bindings().get(1).inspectionItemId());
        assertEquals(7L, result.bindings().get(2).inspectionItemId());
        assertEquals("任务准备", result.sessionSteps().get(0).getName());
        assertEquals(0, result.sessionSteps().get(0).getStepOrder());
        assertEquals("到达指定位置", result.sessionSteps().get(1).getName());
        assertEquals(1, result.sessionSteps().get(1).getStepOrder());
        assertEquals("检查阀", result.sessionSteps().get(2).getName());
        assertEquals(2, result.sessionSteps().get(2).getStepOrder());
        assertTrue((Boolean) result.sessionSteps().stream()
                .filter(step -> "调整拍摄角度".equals(step.getName()))
                .findFirst().orElseThrow().getSource().get("dispatched"));
        assertTrue((Boolean) result.sessionSteps().stream()
                .filter(step -> "拍照".equals(step.getName()))
                .findFirst().orElseThrow().getSource().get("dispatched"));
        Map<String, Object> recognitionSource = result.sessionSteps().stream()
                .filter(step -> "智能分析".equals(step.getName()))
                .findFirst().orElseThrow().getSource();
        assertEquals("INTELLIGENT_ANALYSIS", recognitionSource.get("actionDomain"));
        assertEquals(41L, recognitionSource.get("analysisCapabilityId"));
    }

    @Test
    void assemble_doesNotSpecialCaseDjiBatteryByProtocol() {
        List<TaskStepNode> nodes = List.of(
                action("n-battery", 1, null, 64L, ProtocolDispatchProfile.ACTION_UAV_BATTERY_CHECK, "电量检查", Map.of()),
                action("n-photo", 2, null, 84L, "orientedShoot", "定点拍照", Map.of())
        );

        TaskStepTreeAssembler.AssembledOpenRun result = TaskStepTreeAssembler.assemble(
                nodes, code -> Optional.empty(), "dji-pilot-1.16.1");
        assertEquals(2, result.dispatchActions().size());
        assertEquals(64L, result.dispatchActions().get(0).actionId());
    }

    @Test
    void assemble_aimAlone_dispatchesItsOwnStep() {
        List<TaskStepNode> nodes = List.of(
                action("n-aim", 1, null, 21L, "act-aim", "调整拍摄角度", Map.of("yaw", 1))
        );

        TaskStepTreeAssembler.AssembledOpenRun result = TaskStepTreeAssembler.assemble(
                nodes, code -> Optional.empty());

        assertEquals(1, result.dispatchActions().size());
        assertEquals(21L, result.dispatchActions().get(0).actionId());
        assertEquals(1, result.dispatchActions().get(0).params().get("yaw"));
        assertEquals("seq-0", result.bindings().get(0).stepCode());
    }

    @Test
    void assemble_dispatchesEveryDeviceActionByDomain() {
        List<TaskStepNode> nodes = List.of(
                action("n-command", 1, null, 21L, "act-command", "设备命令", Map.of(),
                        "DEVICE", null),
                action("n-wayline", 2, null, 22L, "act-wayline", "航线动作", Map.of(),
                        "DEVICE", null),
                action("n-check", 3, null, 23L, "act-check", "平台检查", Map.of(),
                        "DEVICE", null),
                action("n-report", 4, null, 24L, "act-report", "设备上报", Map.of(),
                        "DEVICE", null),
                action("n-platform", 5, null, 25L, "act-platform", "平台动作", Map.of(),
                        "PLATFORM", null)
        );

        TaskStepTreeAssembler.AssembledOpenRun result = TaskStepTreeAssembler.assemble(
                nodes, code -> Optional.empty());

        assertEquals(List.of(21L, 22L, 23L, 24L), result.dispatchActions().stream()
                .map(action -> action.actionId())
                .toList());
    }

    @Test
    void assemble_resolvesActionCode_whenRefIdMissing() {
        List<TaskStepNode> nodes = List.of(
                action("n-move", 1, null, null, "act-arrive", "到达指定位置", Map.of())
        );

        TaskStepTreeAssembler.AssembledOpenRun result = TaskStepTreeAssembler.assemble(
                nodes, code -> Optional.of(99L));

        assertEquals(99L, result.dispatchActions().get(0).actionId());
    }

    @Test
    void assemble_visitPointFlyAction_stepOrderBeforeInspectionItems() {
        List<TaskStepNode> nodes = List.of(
                point("p-tank", 1, null, "北罐组内罐1号"),
                action("n-fly", 2, "p-tank", 11L, "act-fly", "飞向目标点", Map.of()),
                item("n-item", 3, "p-tank", 7L, "罐体无渗漏变形"),
                action("n-photo", 4, "n-item", 22L, "act-photo", "定点拍照", Map.of())
        );

        TaskStepTreeAssembler.AssembledOpenRun result = TaskStepTreeAssembler.assemble(
                nodes, code -> Optional.empty());

        TaskExecutionStartReqDTO.StepDraft fly = result.sessionSteps().stream()
                .filter(step -> "飞向目标点".equals(step.getName()))
                .findFirst()
                .orElseThrow();
        TaskExecutionStartReqDTO.StepDraft item = result.sessionSteps().stream()
                .filter(step -> "罐体无渗漏变形".equals(step.getName()))
                .findFirst()
                .orElseThrow();
        assertEquals("p-tank", fly.getParentStepCode());
        assertTrue(fly.getStepOrder() < item.getStepOrder());
        assertEquals("p-tank", item.getParentStepCode());
    }

    @Test
    void assemble_hangsInspectionItemUnderVisitPoint() {
        List<TaskStepNode> nodes = List.of(
                point("p-valve", 1, null, "阀区"),
                equipment("e-tank", 2, "p-valve", "储罐"),
                item("n-item", 3, "e-tank", 7L, "检查阀"),
                action("n-photo", 4, "n-item", 22L, "act-photo", "拍照", Map.of("shot_count", 1))
        );

        TaskStepTreeAssembler.AssembledOpenRun result = TaskStepTreeAssembler.assemble(
                nodes, code -> Optional.empty());

        TaskExecutionStartReqDTO.StepDraft itemStep = result.sessionSteps().stream()
                .filter(step -> "检查阀".equals(step.getName()))
                .findFirst()
                .orElseThrow();
        TaskExecutionStartReqDTO.StepDraft photo = result.sessionSteps().stream()
                .filter(step -> "拍照".equals(step.getName()))
                .findFirst()
                .orElseThrow();
        assertEquals("p-valve", itemStep.getParentStepCode());
        assertEquals("n-item", photo.getParentStepCode());
    }

    @Test
    void assemble_stopRecord_hangsUnderFinishNotPrepare() {
        List<TaskStepNode> nodes = List.of(
                point("stop-start", 1, null, "起点"),
                action("head-1", 2, "stop-start", 74L, "deviceStatus", "获取设备实时状态", Map.of()),
                action("head-2", 3, "stop-start", 75L, "startRecord", "开始录像", Map.of()),
                point("point-1", 4, null, "北罐组西罐1底"),
                action("arrive", 5, "point-1", 11L, "act-arrive", "到达作业位置", Map.of()),
                point("stop-end", 154, null, "终点"),
                action("tail-1", 155, "stop-end", 76L, "stopRecord", "停止录像", Map.of())
        );

        TaskStepTreeAssembler.AssembledOpenRun result = TaskStepTreeAssembler.assemble(
                nodes, code -> Optional.empty());

        TaskExecutionStartReqDTO.StepDraft start = stepNamed(result, "开始录像");
        TaskExecutionStartReqDTO.StepDraft stop = stepNamed(result, "停止录像");
        TaskExecutionStartReqDTO.StepDraft finish = stepNamed(result, "结束工作");
        TaskExecutionStartReqDTO.StepDraft point = stepNamed(result, "北罐组西罐1底");
        assertEquals(TaskStepTreeAssembler.PREPARE_STEP_CODE, start.getParentStepCode());
        assertEquals(TaskStepTreeAssembler.FINISH_STEP_CODE, stop.getParentStepCode());
        assertEquals(154, finish.getStepOrder());
        assertTrue(finish.getStepOrder() > point.getStepOrder());
    }

    private static TaskExecutionStartReqDTO.StepDraft stepNamed(
            TaskStepTreeAssembler.AssembledOpenRun result, String name
    ) {
        return result.sessionSteps().stream()
                .filter(step -> name.equals(step.getName()))
                .findFirst()
                .orElseThrow();
    }

    @Test
    void assemble_emptyTree_throws() {
        ServiceException ex = assertThrows(ServiceException.class,
                () -> TaskStepTreeAssembler.assemble(List.of(), code -> Optional.empty()));
        assertTrue(ex.getMessage().contains("没有执行步骤图"));
    }

    @Test
    void assemble_onlyHuman_throws() {
        List<TaskStepNode> nodes = List.of(
                action("n-human", 1, null, 33L, "act-human", "人工确认", Map.of(),
                        "MANUAL", null)
        );
        ServiceException ex = assertThrows(ServiceException.class,
                () -> TaskStepTreeAssembler.assemble(nodes, code -> Optional.empty()));
        assertTrue(ex.getMessage().contains("没有归属为设备下发或航线编排的动作"));
    }

    @Test
    void assemble_unresolvedAction_throws() {
        List<TaskStepNode> nodes = List.of(
                action("n-move", 1, null, null, "act-missing", "到达指定位置", Map.of())
        );
        ServiceException ex = assertThrows(ServiceException.class,
                () -> TaskStepTreeAssembler.assemble(nodes, code -> Optional.empty()));
        assertTrue(ex.getMessage().contains("对不上动作库"));
    }

    private static TaskStepNode action(
            String key, int order, String parent, Long refId, String refCode, String title, Map<String, Object> params
    ) {
        return action(key, order, parent, refId, refCode, title, params,
                "DEVICE", null);
    }

    private static TaskStepNode action(
            String key, int order, String parent, Long refId, String refCode, String title,
            Map<String, Object> params, String domain, Long capabilityId
    ) {
        return new TaskStepNode(
                key, order, parent, TaskStepNode.HANG_ACTION, refCode, refId, title, params,
                domain, capabilityId);
    }

    private static TaskStepNode item(String key, int order, String parent, Long refId, String title) {
        return new TaskStepNode(key, order, parent, TaskStepNode.HANG_INSPECTION_ITEM, null, refId, title, Map.of());
    }

    private static TaskStepNode point(String key, int order, String parent, String title) {
        return new TaskStepNode(key, order, parent, TaskStepNode.HANG_POINT, "pt", null, title, Map.of());
    }

    private static TaskStepNode equipment(String key, int order, String parent, String title) {
        return new TaskStepNode(key, order, parent, TaskStepNode.HANG_EQUIPMENT, "eq", null, title, Map.of());
    }
}
