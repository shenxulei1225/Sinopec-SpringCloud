package cn.cheers.x.module.dynamicbusiness.service.entity;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PostgresEntityFieldQueryEngineTest {

    @Test
    void tokensFromDedicatedValue_readsJsonArray() {
        assertEquals(Set.of("ROBOT"), PostgresEntityFieldQueryEngine.tokensFromDedicatedValue("[\"ROBOT\"]"));
        assertEquals(Set.of("ROBOT", "UAV"), PostgresEntityFieldQueryEngine.tokensFromDedicatedValue(List.of("ROBOT", "UAV")));
    }

    @Test
    void tokensFromDedicatedValue_readsScalar() {
        assertEquals(Set.of("ROBOT"), PostgresEntityFieldQueryEngine.tokensFromDedicatedValue("ROBOT"));
        assertTrue(PostgresEntityFieldQueryEngine.tokensFromDedicatedValue(null).isEmpty());
    }

    @Test
    void storedCustomTextMatches_readsPlannedStartAndStatus() {
        assertTrue(PostgresEntityFieldQueryEngine.storedCustomTextMatches(
                "2026-10-02T01:00:00Z", "CONTAINS", "2026-10-02"));
        assertTrue(PostgresEntityFieldQueryEngine.storedCustomTextMatches(
                "pending", "IN", List.of("pending", "in_progress", "completed")));
        assertTrue(PostgresEntityFieldQueryEngine.storedCustomTextMatches(1, "EQ", 1));
        assertFalse(PostgresEntityFieldQueryEngine.storedCustomTextMatches(
                "2026-10-03T01:00:00Z", "CONTAINS", "2026-10-02"));
        assertFalse(PostgresEntityFieldQueryEngine.storedCustomTextMatches(
                null, "CONTAINS", "2026-10-02"));
    }
}
