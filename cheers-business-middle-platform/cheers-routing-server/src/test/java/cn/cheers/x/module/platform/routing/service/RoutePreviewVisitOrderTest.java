package cn.cheers.x.module.platform.routing.service;

import cn.cheers.x.module.platform.contract.dto.route.RoutePreviewSegmentDTO;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RoutePreviewVisitOrderTest {

    @Test
    void flattenVisitNodeIds_joinsSegmentsAndKeepsRevisits() {
        List<String> visits = RoutePlanServiceImpl.flattenVisitNodeIds(List.of(
                RoutePreviewSegmentDTO.builder().nodeIds(List.of("home", "via", "s1")).build(),
                RoutePreviewSegmentDTO.builder().nodeIds(List.of("s1", "s2")).build(),
                RoutePreviewSegmentDTO.builder().nodeIds(List.of("s2", "via", "home")).build()));
        assertEquals(List.of("home", "via", "s1", "s2", "via", "home"), visits);
    }
}
