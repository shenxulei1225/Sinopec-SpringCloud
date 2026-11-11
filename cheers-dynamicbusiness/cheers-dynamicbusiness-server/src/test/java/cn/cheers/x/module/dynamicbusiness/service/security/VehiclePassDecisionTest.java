package cn.cheers.x.module.dynamicbusiness.service.security;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class VehiclePassDecisionTest {

    private static final LocalDateTime NOW = LocalDateTime.of(2026, 11, 12, 8, 0);

    @Test
    void allowsListedPlateOnEntryLane() {
        VehiclePassDecision.Result result = VehiclePassDecision.recognize(
                VehiclePassDecision.IN, 9L, " 沪a12345 ", false,
                List.of(allow("沪A12345", NOW.minusDays(1), NOW.plusDays(1), Set.of())),
                List.of(), NOW);
        assertEquals(VehiclePassDecision.AUTO, result.matchResult());
        assertTrue(result.writeEnter());
    }

    @Test
    void deniesForbiddenPlate() {
        VehiclePassDecision.Result result = VehiclePassDecision.recognize(
                VehiclePassDecision.IN, 9L, "沪A12345", true,
                List.of(new VehiclePassDecision.Grant("沪A12345", VehiclePassDecision.FORBID, null, null, Set.of())),
                List.of(), NOW);
        assertEquals("这辆车禁止进入", result.matchResult());
        assertEquals("这辆车禁止进入", result.message());
        assertFalse(result.writeEnter());
    }

    @Test
    void holdsUnknownPlateForGuard() {
        VehiclePassDecision.Result result = VehiclePassDecision.recognize(
                VehiclePassDecision.IN, 9L, "沪B00001", false, List.of(), List.of(), NOW);
        assertEquals(VehiclePassDecision.NOT_ON_LIST, result.matchResult());
        assertEquals(VehiclePassDecision.NOT_ON_LIST, result.message());
    }

    @Test
    void deniesSecondEntryWhileStillInside() {
        VehiclePassDecision.Visit inside = new VehiclePassDecision.Visit(
                3L, "沪A12345", null, null, null, null, NOW.minusHours(1), null);
        VehiclePassDecision.Result result = VehiclePassDecision.recognize(
                VehiclePassDecision.IN, 9L, "沪A12345", false,
                List.of(allow("沪A12345", NOW.minusDays(1), NOW.plusDays(1), Set.of())),
                List.of(inside), NOW);
        assertEquals("这辆车还在场内", result.matchResult());
        assertEquals("这辆车还在场内", result.message());
    }

    @Test
    void exitWritesLeaveOnTheOpenVisit() {
        VehiclePassDecision.Visit inside = new VehiclePassDecision.Visit(
                3L, "沪A12345", null, null, null, null, NOW.minusHours(1), null);
        VehiclePassDecision.Result result = VehiclePassDecision.recognize(
                VehiclePassDecision.OUT, 8L, "沪A12345", false, List.of(), List.of(inside), NOW);
        assertEquals(VehiclePassDecision.AUTO, result.matchResult());
        assertEquals(3L, result.updateVisitId());
        assertTrue(result.writeLeave());
    }

    @Test
    void agreedGuestEntersOnThatVisit() {
        VehiclePassDecision.Visit guest = new VehiclePassDecision.Visit(
                6L, "沪C9", NOW.minusMinutes(10), null, NOW.minusHours(1), NOW.plusHours(2), null, null);
        VehiclePassDecision.Result result = VehiclePassDecision.recognize(
                VehiclePassDecision.IN, 9L, "沪C9", false, List.of(), List.of(guest), NOW);
        assertEquals(VehiclePassDecision.AUTO, result.matchResult());
        assertEquals(6L, result.updateVisitId());
        assertTrue(result.writeEnter());
    }

    @Test
    void allowsCompanyCarWhenDispatchIsOpen() {
        VehiclePassDecision.Result result = VehiclePassDecision.recognize(
                VehiclePassDecision.IN, 9L, "鄂B10001", false, List.of(), List.of(),
                List.of(new VehiclePassDecision.CompanyUse(
                        "鄂B10001", true, true, NOW.minusHours(1), NOW.plusHours(2))),
                NOW);
        assertEquals(VehiclePassDecision.AUTO, result.matchResult());
        assertTrue(result.writeEnter());
    }

    @Test
    void holdsCompanyCarWhenNoDispatch() {
        VehiclePassDecision.Result result = VehiclePassDecision.recognize(
                VehiclePassDecision.IN, 9L, "鄂B10001", false, List.of(), List.of(),
                List.of(new VehiclePassDecision.CompanyUse("鄂B10001", true, false, null, null)),
                NOW);
        assertEquals("当前没有调度", result.matchResult());
        assertFalse(result.writeEnter());
    }

    private static VehiclePassDecision.Grant allow(
            String plate, LocalDateTime from, LocalDateTime to, Set<Long> lanes) {
        return new VehiclePassDecision.Grant(plate, VehiclePassDecision.ALLOW, from, to, lanes);
    }
}
