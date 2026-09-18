package cn.cheers.x.module.platform.routing.planner;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DepotTourSupportTest {

    @Test
    void assemble_sameEnd_returnsHome() {
        List<String> tour = DepotTourSupport.assemble("home", List.of("a", "b"), null, true, null);
        assertEquals(List.of("home", "a", "b", "home"), tour);
    }

    @Test
    void assemble_distinctEnd_appendsLanding() {
        List<String> tour = DepotTourSupport.assemble("home", List.of("a", "b"), null, false, "pad");
        assertEquals(List.of("home", "a", "b", "pad"), tour);
    }
}
