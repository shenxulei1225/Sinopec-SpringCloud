package cn.cheers.x.module.dynamicbusiness.service.sop;

import cn.cheers.x.module.dynamicbusiness.service.sop.dto.SopMergeResult;
import cn.cheers.x.module.dynamicbusiness.service.sop.dto.SopStepOverride;
import cn.cheers.x.module.dynamicbusiness.service.sop.dto.SopTemplateSnapshot;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
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
            SopTemplateSnapshot template = v.getObject("template", SopTemplateSnapshot.class);
            SopStepOverride stepOverride = v.getObject("stepOverride", SopStepOverride.class);
            @SuppressWarnings("unchecked")
            Map<String, Object> paramOverride = v.getObject("paramOverride", Map.class);

            SopMergeResult result = mergeService.merge(template, stepOverride, paramOverride);
            JSONObject expect = v.getJSONObject("expect");
            boolean expectOk = expect.getBooleanValue("ok");
            assertEquals(expectOk, result.isOk(), "vector " + id + " ok");

            if (expectOk) {
                assertNotNull(result.getEffective(), "vector " + id);
                if (expect.containsKey("stepsLength")) {
                    assertEquals(expect.getIntValue("stepsLength"),
                            result.getEffective().getSteps().size(),
                            "vector " + id + " stepsLength");
                }
                if (expect.containsKey("secondStepTemplateId")) {
                    assertEquals(expect.getString("secondStepTemplateId"),
                            result.getEffective().getSteps().get(1).getStepTemplateId(),
                            "vector " + id + " secondStep");
                }
                if (expect.containsKey("params")) {
                    JSONObject expectedParams = expect.getJSONObject("params");
                    for (String key : expectedParams.keySet()) {
                        Object actual = result.getEffective().getParams().get(key);
                        Object expected = expectedParams.get(key);
                        if (expected instanceof Number && actual instanceof Number) {
                            assertEquals(((Number) expected).doubleValue(),
                                    ((Number) actual).doubleValue(),
                                    0.0001,
                                    "vector " + id + " param " + key);
                        } else {
                            assertEquals(String.valueOf(expected), String.valueOf(actual),
                                    "vector " + id + " param " + key);
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
