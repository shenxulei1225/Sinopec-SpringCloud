package cn.cheers.x.module.dynamicbusiness.service.security;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * 车道上一次识别，对照名单和外来车，决定这一条过车怎么记。
 *
 * 管什么：比对结果，以及进场时间、出场时间该写到哪一条上。
 * 不管什么：抬杆、告警、照片。调用方拿到结果后再写。
 * 禁止：没有车牌时编一个号；禁止进入的车仍给出自动放行。
 */
public final class VehiclePassDecision {

    public static final String AUTO = "自动放行";
    /** 这辆车不在允许进入的清单里。没有名为「拒绝」的比对结果。 */
    public static final String NOT_ON_LIST = "不在允许进入的车辆清单中";
    public static final String WAIT = "待人工确认";
    public static final String MANUAL = "人工放行";
    public static final String IN = "进场";
    public static final String OUT = "出场";
    public static final String ALLOW = "允许进入";
    public static final String FORBID = "禁止进入";

    private VehiclePassDecision() {
    }

    /**
     * 一条名单。允许车道为空表示这条车道不限。
     */
    public record Grant(
            String plate,
            String status,
            LocalDateTime validFrom,
            LocalDateTime validTo,
            Set<Long> laneIds) {
    }

    /**
     * 已经写下的一条进出。外来车在被同意之前，进入时间是空的。
     */
    public record Visit(
            long id,
            String plate,
            LocalDateTime agreedAt,
            LocalDateTime rejectedAt,
            LocalDateTime validFrom,
            LocalDateTime validTo,
            LocalDateTime enteredAt,
            LocalDateTime leftAt) {
    }

    /**
     * @param updateVisitId 进场或出场时间要写到这一条上。空着表示写到本次新识别上。
     * @param writeEnter 抬杆被接受之后才写入进入时间。
     * @param writeLeave 抬杆被接受之后才写入离开时间。
     */
    public record Result(
            String matchResult,
            String message,
            Long updateVisitId,
            boolean writeEnter,
            boolean writeLeave) {
    }

    public static String plateOf(String raw) {
        if (raw == null) {
            return "";
        }
        return raw.trim().toUpperCase(Locale.ROOT);
    }

    /**
     * 一辆公司车和它当前还没结束的调度。
     * 在用且这次调度还在时段里，进场按自动放行。没有调度不自动放行。
     */
    public record CompanyUse(
            String plate,
            boolean inUse,
            boolean openDispatch,
            LocalDateTime dispatchFrom,
            LocalDateTime dispatchTo) {
    }

    /**
     * 按车道方向比对。
     * 识别号码待确认时，禁止进入仍然不放行。不在允许进入清单里的，比对结果就写这句话。
     * 没有公司车调度时，公司车清单按空名单处理。
     */
    public static Result recognize(
            String direction,
            long laneId,
            String plate,
            boolean uncertain,
            List<Grant> grants,
            List<Visit> visits,
            LocalDateTime now) {
        return recognize(direction, laneId, plate, uncertain, grants, visits, List.of(), now);
    }

    /**
     * 公司车单独传入。个人名单仍走 grants。
     */
    public static Result recognize(
            String direction,
            long laneId,
            String plate,
            boolean uncertain,
            List<Grant> grants,
            List<Visit> visits,
            List<CompanyUse> companyUses,
            LocalDateTime now) {
        String normalized = plateOf(plate);
        if (normalized.isEmpty()) {
            return new Result(WAIT, "没有车牌", null, false, false);
        }
        if (OUT.equals(direction)) {
            return exit(normalized, uncertain, visits);
        }
        return enter(laneId, normalized, uncertain, grants, visits, companyUses == null ? List.of() : companyUses, now);
    }

    private static Result exit(String plate, boolean uncertain, List<Visit> visits) {
        Visit open = openVisit(plate, visits);
        if (open == null) {
            return new Result("没有进入记录", "没有进入记录", null, false, false);
        }
        if (uncertain) {
            return new Result(WAIT, "识别号码待确认", open.id(), false, false);
        }
        return new Result(AUTO, "", open.id(), false, true);
    }

    private static Result enter(
            long laneId,
            String plate,
            boolean uncertain,
            List<Grant> grants,
            List<Visit> visits,
            List<CompanyUse> companyUses,
            LocalDateTime now) {
        if (openVisit(plate, visits) != null) {
            return new Result("这辆车还在场内", "这辆车还在场内", null, false, false);
        }
        if (forbidden(plate, grants)) {
            return new Result("这辆车禁止进入", "这辆车禁止进入", null, false, false);
        }
        if (retiredCompany(plate, companyUses)) {
            return new Result("这辆车已停用", "这辆车已停用", null, false, false);
        }
        if (uncertain) {
            return new Result(WAIT, "识别号码待确认", null, false, false);
        }
        Grant allow = matchingAllow(plate, laneId, grants, now);
        if (allow != null) {
            return new Result(AUTO, "", null, true, false);
        }
        String allowBlock = allowBlock(plate, laneId, grants, now);
        if (!allowBlock.isEmpty()) {
            return new Result(allowBlock, allowBlock, null, false, false);
        }
        if (matchingCompany(plate, companyUses, now)) {
            return new Result(AUTO, "", null, true, false);
        }
        if (companyPlate(plate, companyUses)) {
            return new Result("当前没有调度", "当前没有调度", null, false, false);
        }
        Visit guest = guestVisit(plate, visits, now);
        if (guest != null) {
            return new Result(AUTO, "", guest.id(), true, false);
        }
        String guestBlock = guestBlock(plate, visits, now);
        if (!guestBlock.isEmpty()) {
            return guestBlock.equals("被访人还没同意")
                    ? new Result(WAIT, guestBlock, null, false, false)
                    : new Result(guestBlock, guestBlock, null, false, false);
        }
        return new Result(NOT_ON_LIST, NOT_ON_LIST, null, false, false);
    }

    private static Visit openVisit(String plate, List<Visit> visits) {
        Visit found = null;
        for (Visit visit : visits) {
            if (!plate.equals(plateOf(visit.plate()))) {
                continue;
            }
            if (visit.enteredAt() != null && visit.leftAt() == null && (found == null || visit.id() > found.id())) {
                found = visit;
            }
        }
        return found;
    }

    /** 公司车已停用时，进场不自动放行。不确定号码也停住。 */
    private static boolean retiredCompany(String plate, List<CompanyUse> companyUses) {
        boolean saw = false;
        boolean inUse = false;
        for (CompanyUse car : companyUses) {
            if (!plate.equals(plateOf(car.plate()))) {
                continue;
            }
            saw = true;
            if (car.inUse()) {
                inUse = true;
            }
        }
        return saw && !inUse;
    }

    private static boolean companyPlate(String plate, List<CompanyUse> companyUses) {
        for (CompanyUse car : companyUses) {
            if (plate.equals(plateOf(car.plate()))) {
                return true;
            }
        }
        return false;
    }

    /** 在用，并且有一条还没结束、此刻落在时段里的调度。 */
    private static boolean matchingCompany(String plate, List<CompanyUse> companyUses, LocalDateTime now) {
        for (CompanyUse car : companyUses) {
            if (!plate.equals(plateOf(car.plate())) || !car.inUse() || !car.openDispatch()) {
                continue;
            }
            if (within(car.dispatchFrom(), car.dispatchTo(), now)) {
                return true;
            }
        }
        return false;
    }

    private static boolean forbidden(String plate, List<Grant> grants) {
        for (Grant grant : grants) {
            if (plate.equals(plateOf(grant.plate())) && FORBID.equals(grant.status())) {
                return true;
            }
        }
        return false;
    }

    private static Grant matchingAllow(String plate, long laneId, List<Grant> grants, LocalDateTime now) {
        for (Grant grant : grants) {
            if (!plate.equals(plateOf(grant.plate())) || !ALLOW.equals(grant.status())) {
                continue;
            }
            if (!within(grant.validFrom(), grant.validTo(), now)) {
                continue;
            }
            if (grant.laneIds() != null && !grant.laneIds().isEmpty() && !grant.laneIds().contains(laneId)) {
                continue;
            }
            return grant;
        }
        return null;
    }

    private static String allowBlock(String plate, long laneId, List<Grant> grants, LocalDateTime now) {
        boolean saw = false;
        boolean expired = false;
        boolean notYet = false;
        boolean wrongLane = false;
        for (Grant grant : grants) {
            if (!plate.equals(plateOf(grant.plate())) || !ALLOW.equals(grant.status())) {
                continue;
            }
            saw = true;
            if (grant.validFrom() != null && now.isBefore(grant.validFrom())) {
                notYet = true;
            } else if (grant.validTo() != null && now.isAfter(grant.validTo())) {
                expired = true;
            } else if (grant.laneIds() != null && !grant.laneIds().isEmpty() && !grant.laneIds().contains(laneId)) {
                wrongLane = true;
            }
        }
        if (!saw) {
            return "";
        }
        if (wrongLane) {
            return "这辆车不能走这条车道";
        }
        if (notYet) {
            return "这辆车的允许还没到有效期";
        }
        if (expired) {
            return "这辆车的允许已经到期";
        }
        return "";
    }

    private static Visit guestVisit(String plate, List<Visit> visits, LocalDateTime now) {
        Visit found = null;
        for (Visit visit : visits) {
            if (!plate.equals(plateOf(visit.plate())) || visit.enteredAt() != null) {
                continue;
            }
            if (visit.agreedAt() == null || visit.rejectedAt() != null) {
                continue;
            }
            if (!within(visit.validFrom(), visit.validTo(), now)) {
                continue;
            }
            if (found == null || visit.id() > found.id()) {
                found = visit;
            }
        }
        return found;
    }

    private static String guestBlock(String plate, List<Visit> visits, LocalDateTime now) {
        boolean saw = false;
        boolean rejected = false;
        boolean waiting = false;
        boolean expired = false;
        for (Visit visit : visits) {
            if (!plate.equals(plateOf(visit.plate())) || visit.enteredAt() != null) {
                continue;
            }
            if (visit.agreedAt() == null && visit.rejectedAt() == null) {
                saw = true;
                waiting = true;
                continue;
            }
            if (visit.rejectedAt() != null) {
                saw = true;
                rejected = true;
                continue;
            }
            if (visit.agreedAt() != null && !within(visit.validFrom(), visit.validTo(), now)) {
                saw = true;
                expired = true;
            }
        }
        if (!saw) {
            return "";
        }
        if (rejected) {
            return "被访人已拒绝";
        }
        if (expired) {
            return "这辆车的允许已经到期";
        }
        if (waiting) {
            return "被访人还没同意";
        }
        return "";
    }

    private static boolean within(LocalDateTime from, LocalDateTime to, LocalDateTime now) {
        if (from != null && now.isBefore(from)) {
            return false;
        }
        return to == null || !now.isAfter(to);
    }
}
