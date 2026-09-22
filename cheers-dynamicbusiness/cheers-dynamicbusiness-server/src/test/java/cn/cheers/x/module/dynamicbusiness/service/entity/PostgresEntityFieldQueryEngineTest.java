package cn.cheers.x.module.dynamicbusiness.service.entity;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
}
