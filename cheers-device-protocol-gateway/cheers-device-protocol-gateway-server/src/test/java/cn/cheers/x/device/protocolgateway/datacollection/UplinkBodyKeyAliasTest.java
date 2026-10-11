package cn.cheers.x.device.protocolgateway.datacollection;

import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class UplinkBodyKeyAliasTest {

    @Test
    void stepResult_resultOnly_isAlsoVisibleAsPackages() {
        Map<String, Object> step = Map.of("opcode", 200102, "sequence", 1);
        Map<String, Object> fields = new LinkedHashMap<>();
        fields.put("opcode", 500202);
        fields.put("result", step);

        Map<String, Object> accepted = UplinkBodyKeyAlias.acceptEitherCarrier(500202, fields);

        assertEquals(step, accepted.get("packages"));
        assertEquals(step, accepted.get("result"));
    }

    @Test
    void stepResult_packagesWins_whenBothPresent() {
        Map<String, Object> fromPackages = Map.of("opcode", 200301, "sequence", 2);
        Map<String, Object> fromResult = Map.of("opcode", 200102, "sequence", 1);
        Map<String, Object> fields = new LinkedHashMap<>();
        fields.put("packages", fromPackages);
        fields.put("result", fromResult);

        Map<String, Object> accepted = UplinkBodyKeyAlias.acceptEitherCarrier(500202, fields);

        assertSame(fields, accepted);
        assertEquals(fromPackages, accepted.get("packages"));
    }

    @Test
    void stepResult_innerOutcomeOnly_isNotCopied() {
        Map<String, Object> fields = new LinkedHashMap<>();
        fields.put("result", Map.of("resultCode", 200));

        Map<String, Object> accepted = UplinkBodyKeyAlias.acceptEitherCarrier(500202, fields);

        assertSame(fields, accepted);
        assertEquals(false, accepted.containsKey("packages"));
    }

    @Test
    void timedStatus_packagesObject_isAlsoVisibleAsResult() {
        Map<String, Object> status = Map.of("electricity", "80", "status", 400102);
        Map<String, Object> fields = new LinkedHashMap<>();
        fields.put("packages", status);

        Map<String, Object> accepted = UplinkBodyKeyAlias.acceptEitherCarrier(500103, fields);

        assertEquals(status, accepted.get("result"));
        assertEquals(status, accepted.get("packages"));
    }

    @Test
    void commandPackageArray_isLeftAlone() {
        Map<String, Object> fields = new LinkedHashMap<>();
        fields.put("packages", List.of(Map.of("opcode", 200102, "sequence", 0)));

        Map<String, Object> accepted = UplinkBodyKeyAlias.acceptEitherCarrier(500104, fields);

        assertSame(fields, accepted);
        assertEquals(false, accepted.containsKey("result"));
    }
}
