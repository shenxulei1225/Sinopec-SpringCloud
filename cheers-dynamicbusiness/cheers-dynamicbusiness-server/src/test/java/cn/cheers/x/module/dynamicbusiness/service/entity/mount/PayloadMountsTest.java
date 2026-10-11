package cn.cheers.x.module.dynamicbusiness.service.entity.mount;

import cn.cheers.x.framework.common.exception.ServiceException;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PayloadMountsTest {

    @Test
    void parseNormalizesPointAndSkipsEmptyDevice() {
        List<PayloadMounts.Row> rows = PayloadMounts.parse(List.of(
                Map.of("mountPoint", "07", "equipmentId", 12),
                Map.of("mountPoint", "1")
        ));
        assertEquals(2, rows.size());
        assertEquals("7", rows.get(0).mountPoint());
        assertEquals(12L, rows.get(0).equipmentId());
        assertEquals(null, rows.get(1).equipmentId());
    }

    @Test
    void blankNewRowIsKeptOutOfTheSavedRows() {
        List<PayloadMounts.Row> rows = PayloadMounts.parse(List.of(
                Map.of("mountPoint", "0", "equipmentId", 12),
                Map.of("mountPoint", "", "equipmentId", "")
        ));
        assertEquals(1, rows.size());
        assertEquals("0", rows.get(0).mountPoint());
    }

    @Test
    void duplicatePointRejected() {
        ServiceException ex = assertThrows(ServiceException.class, () -> PayloadMounts.parse(List.of(
                Map.of("mountPoint", "0", "equipmentId", 1),
                Map.of("mountPoint", "0", "equipmentId", 2)
        )));
        assertEquals("同一台设备上挂载点不能重复", ex.getMessage());
    }

    @Test
    void planMovesNewDeviceAndClearsRemovedChild() {
        List<PayloadMounts.Row> previous = List.of(new PayloadMounts.Row("0", 5L));
        List<PayloadMounts.Row> next = List.of(new PayloadMounts.Row("1", 8L));
        Map<Long, Long> parents = new HashMap<>();
        parents.put(5L, 100L);
        parents.put(8L, null);
        List<PayloadMounts.Move> moves = PayloadMounts.plan(100L, previous, next, parents);
        assertEquals(List.of(
                new PayloadMounts.Move(5L, null),
                new PayloadMounts.Move(8L, 100L)
        ), moves);
    }

    @Test
    void planLeavesDeviceWhoseParentIsSomeoneElse() {
        List<PayloadMounts.Row> previous = List.of(new PayloadMounts.Row("0", 5L));
        List<PayloadMounts.Move> moves = PayloadMounts.plan(100L, previous, List.of(), Map.of(5L, 9L));
        assertEquals(List.of(), moves);
    }
}
