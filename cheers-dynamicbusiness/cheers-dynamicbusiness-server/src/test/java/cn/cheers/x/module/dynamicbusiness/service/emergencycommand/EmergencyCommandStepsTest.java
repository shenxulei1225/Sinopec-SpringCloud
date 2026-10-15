package cn.cheers.x.module.dynamicbusiness.service.emergencycommand;

import com.alibaba.fastjson2.JSONObject;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EmergencyCommandStepsTest {

    @Test
    void copyLevel_keepsOnlyTheJudgedLevel_andMarksStepsOpen() {
        JSONObject pack = JSONObject.parseObject("""
                {"version":1,"methods":[
                  {"methodKey":"I","stepTree":[{"nodeKey":"a","stepDescription":"先报告"}]},
                  {"methodKey":"II","stepTree":[{"nodeKey":"b","stepDescription":"再疏散"}]}
                ]}
                """);

        JSONObject copied = EmergencyCommandSteps.copyLevel(pack, "II");

        assertEquals("II", copied.getJSONArray("methods").getJSONObject(0).getString("methodKey"));
        assertEquals(1, copied.getJSONArray("methods").size());
        assertEquals("未完成", copied.getJSONArray("methods").getJSONObject(0)
                .getJSONArray("stepTree").getJSONObject(0).getString("runStatus"));
    }

    @Test
    void copyLevel_returnsNullWhenThatLevelHasNoSteps() {
        JSONObject pack = JSONObject.parseObject("""
                {"version":1,"methods":[{"methodKey":"I","stepTree":[]}]}
                """);

        assertNull(EmergencyCommandSteps.copyLevel(pack, "I"));
        assertNull(EmergencyCommandSteps.copyLevel(pack, "III"));
    }

    @Test
    void complete_marksTheCopiedStep_andLeavesOtherSteps() {
        JSONObject pack = JSONObject.parseObject("""
                {"version":1,"methods":[{"methodKey":"II","stepTree":[
                  {"nodeKey":"a","runStatus":"未完成"},
                  {"nodeKey":"b","runStatus":"未完成"}
                ]}]}
                """);

        JSONObject updated = EmergencyCommandSteps.complete(pack, "b");

        assertEquals("未完成", updated.getJSONArray("methods").getJSONObject(0)
                .getJSONArray("stepTree").getJSONObject(0).getString("runStatus"));
        assertEquals("已完成", updated.getJSONArray("methods").getJSONObject(0)
                .getJSONArray("stepTree").getJSONObject(1).getString("runStatus"));
        assertFalse(updated.getJSONArray("methods").getJSONObject(0)
                .getJSONArray("stepTree").getJSONObject(1).getString("completedAt").isBlank());
        assertNull(EmergencyCommandSteps.complete(pack, "missing"));
    }

    @Test
    void begin_marksTheStepRunning_andKeepsAFinishedStepFinished() {
        JSONObject pack = JSONObject.parseObject("""
                {"version":1,"methods":[{"methodKey":"II","stepTree":[
                  {"nodeKey":"a","runStatus":"未完成"},
                  {"nodeKey":"b","runStatus":"已完成","completedAt":"2026-10-15T06:49:01"}
                ]}]}
                """);

        JSONObject updated = EmergencyCommandSteps.begin(pack, "a");

        assertEquals("执行中", updated.getJSONArray("methods").getJSONObject(0)
                .getJSONArray("stepTree").getJSONObject(0).getString("runStatus"));
        assertFalse(updated.getJSONArray("methods").getJSONObject(0)
                .getJSONArray("stepTree").getJSONObject(0).getString("startedAt").isBlank());
        assertEquals("已完成", EmergencyCommandSteps.begin(updated, "b").getJSONArray("methods").getJSONObject(0)
                .getJSONArray("stepTree").getJSONObject(1).getString("runStatus"));
        assertNull(EmergencyCommandSteps.begin(pack, "missing"));
    }

    @Test
    void listLevels_keepsChildUnderParent_andOnlyMarksTheStartedLevel() {
        JSONObject plan = JSONObject.parseObject("""
                {"version":1,"methods":[
                  {"methodKey":"I","stepTree":[{"nodeKey":"a","stepDescription":"报告","children":[
                    {"nodeKey":"a1","stepDescription":"打电话"}
                  ]}]},
                  {"methodKey":"II","stepTree":[{"nodeKey":"b","stepDescription":"疏散"}]}
                ]}
                """);
        JSONObject response = JSONObject.parseObject("""
                {"version":1,"methods":[{"methodKey":"II","stepTree":[
                  {"nodeKey":"b","stepDescription":"疏散","runStatus":"已完成"}
                ]}]}
                """);

        List<EmergencyCommandSteps.LevelNodes> levels = EmergencyCommandSteps.listLevels(plan, response, "II");

        assertEquals(2, levels.size());
        EmergencyCommandSteps.StepNode child = levels.get(0).nodes().get(1);
        assertEquals("a1", child.nodeKey());
        assertEquals("a", child.parentNodeKey());
        assertTrue(child.runStatus() == null || child.runStatus().isEmpty());
        assertEquals("已完成", levels.get(1).nodes().get(0).runStatus());
    }

    @Test
    void listStages_keepsTheNamesTheUserWrote_andStepsKeepTheirStage() {
        JSONObject plan = JSONObject.parseObject("""
                {"version":1,"stages":[
                  {"stageKey":"report","name":"事件上报"},
                  {"stageKey":"response","name":"现场处置"}
                ],"methods":[{"methodKey":"II","stepTree":[
                  {"nodeKey":"b","stepDescription":"疏散","stageKey":"response"}
                ]}]}
                """);

        List<EmergencyCommandSteps.StageDef> stages = EmergencyCommandSteps.listStages(plan);

        assertEquals(2, stages.size());
        assertEquals("report", stages.get(0).stageKey());
        assertEquals("事件上报", stages.get(0).name());
        assertEquals("现场处置", stages.get(1).name());
        assertEquals("response", EmergencyCommandSteps.listLevels(plan, null, "II").get(0).nodes().get(0).stageKey());
    }
}
