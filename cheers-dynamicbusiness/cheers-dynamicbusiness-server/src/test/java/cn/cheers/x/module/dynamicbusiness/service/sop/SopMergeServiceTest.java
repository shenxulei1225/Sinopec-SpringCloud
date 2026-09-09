package cn.cheers.x.module.dynamicbusiness.service.sop;

import cn.cheers.x.module.dynamicbusiness.service.sop.dto.SopMergeResult;
import cn.cheers.x.module.dynamicbusiness.service.sop.dto.SopStandardSnapshot;
import cn.cheers.x.module.dynamicbusiness.service.sop.dto.SopTreeOverride;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.TypeReference;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 与 docs/superpowers/fixtures/sop-merge-vectors.json（classpath 副本）对齐。
 */
class SopMergeServiceTest {

    private SopMergeService mergeService;
    private JSONArray vectors;

    @BeforeEach
    void setUp() throws Exception {
        mergeService = new SopMergeServiceImpl();
        try (InputStream in = getClass().getClassLoader()
                .getResourceAsStream("fixtures/sop-merge-vectors.json")) {
            assertNotNull(in, "classpath fixtures/sop-merge-vectors.json missing");
            String json = new String(in.readAllBytes(), StandardCharsets.UTF_8);
            JSONObject root = JSON.parseObject(json);
            vectors = root.getJSONArray("vectors");
            assertNotNull(vectors);
            assertFalse(vectors.isEmpty());
        }
    }

    @Test
    void allFixtureVectorsMatchExpected() {
        for (int i = 0; i < vectors.size(); i++) {
            JSONObject v = vectors.getJSONObject(i);
            String id = v.getString("id");
            SopStandardSnapshot standard = v.getObject("template", SopStandardSnapshot.class);
            SopTreeOverride treeOverride = v.getObject("treeOverride", SopTreeOverride.class);
            Map<String, Map<String, Object>> paramOverride = null;
            if (v.get("paramOverride") != null && !(v.get("paramOverride") instanceof String)) {
                paramOverride = v.getObject("paramOverride",
                        new TypeReference<Map<String, Map<String, Object>>>() {
                        });
            }

            SopMergeResult result = mergeService.merge(standard, treeOverride, paramOverride);
            JSONObject expect = v.getJSONObject("expect");
            boolean expectOk = expect.getBooleanValue("ok");
            assertEquals(expectOk, result.isOk(), "vector " + id + " ok");

            if (expectOk) {
                assertNotNull(result.getEffective(), "vector " + id);
                if (expect.containsKey("nodesLength")) {
                    assertEquals(expect.getIntValue("nodesLength"),
                            result.getEffective().getNodes().size(),
                            "vector " + id + " nodesLength");
                }
                if (expect.containsKey("secondActionId")) {
                    assertEquals(expect.getString("secondActionId"),
                            result.getEffective().getNodes().get(1).getActionId(),
                            "vector " + id + " secondAction");
                }
                if (expect.containsKey("paramsByNode")) {
                    JSONObject expectedByNode = expect.getJSONObject("paramsByNode");
                    for (String nodeKey : expectedByNode.keySet()) {
                        JSONObject expectedParams = expectedByNode.getJSONObject(nodeKey);
                        Map<String, Object> actualParams =
                                result.getEffective().getParamsByNode().get(nodeKey);
                        assertNotNull(actualParams, "vector " + id + " node " + nodeKey);
                        for (String key : expectedParams.keySet()) {
                            Object actual = actualParams.get(key);
                            Object expected = expectedParams.get(key);
                            if (expected instanceof Number && actual instanceof Number) {
                                assertEquals(((Number) expected).doubleValue(),
                                        ((Number) actual).doubleValue(),
                                        0.0001,
                                        "vector " + id + " " + nodeKey + "." + key);
                            } else {
                                assertEquals(String.valueOf(expected), String.valueOf(actual),
                                        "vector " + id + " " + nodeKey + "." + key);
                            }
                        }
                    }
                }
            } else {
                List<String> gapCodes = result.getGapCodes();
                assertNotNull(gapCodes);
                JSONArray expectedGaps = expect.getJSONArray("gapCodes");
                for (int g = 0; g < expectedGaps.size(); g++) {
                    assertTrue(gapCodes.contains(expectedGaps.getString(g)),
                            "vector " + id + " missing gap " + expectedGaps.getString(g));
                }
            }
        }
    }
}
