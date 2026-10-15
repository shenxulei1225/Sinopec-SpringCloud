package cn.cheers.x.inspection.task.service.execution.monitor;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * 从这台设备的执行账里选出协议监控这一次要下发的那一条。
 * <p>正在执行只有一条时用它。否则用计划时刻已经到了、仍待执行的最晚一条。
 * <p>不负责：按设备编号新造一条执行账。
 * <p>禁止：多条正在执行时挑一条；还没到点的待执行拿来下发。
 */
public final class PatrolGeneratedTaskSelector {

    static final String IN_PROGRESS = "in_progress";
    static final String PENDING = "pending";

    private PatrolGeneratedTaskSelector() {
    }

    public record Candidate(
            long id,
            String name,
            String status,
            String deviceId,
            OffsetDateTime plannedStart
    ) {
    }

    /**
     * 这台设备上还可以拿来下发的已生成任务。
     * 正在执行排在前面，其余待执行按计划时刻从早到晚。已结束的不在这里。
     * 还没到点的待执行也列出，由人在监控页点选，不在这里替人挑一条。
     */
    public static List<Candidate> listed(List<Candidate> rows, String deviceId) {
        if (deviceId == null || deviceId.isBlank() || rows == null) {
            return List.of();
        }
        String wanted = deviceId.trim();
        List<Candidate> mine = new ArrayList<>();
        for (Candidate row : rows) {
            if (row == null || !wanted.equals(row.deviceId())) {
                continue;
            }
            if (IN_PROGRESS.equals(row.status()) || PENDING.equals(row.status())) {
                mine.add(row);
            }
        }
        mine.sort(Comparator
                .comparingInt((Candidate row) -> IN_PROGRESS.equals(row.status()) ? 0 : 1)
                .thenComparing(Candidate::plannedStart, Comparator.nullsLast(Comparator.naturalOrder()))
                .thenComparingLong(Candidate::id));
        return List.copyOf(mine);
    }

    /** 人点中的那一条必须是这台设备待执行或正在执行的任务。 */
    public static Candidate requireListed(List<Candidate> rows, String deviceId, long recordId) {
        for (Candidate row : listed(rows, deviceId)) {
            if (row.id() == recordId) {
                return row;
            }
        }
        throw new IllegalArgumentException("这条执行记录不是这台设备待执行或正在执行的任务");
    }

    public static Candidate choose(List<Candidate> rows, String deviceId, OffsetDateTime now) {
        if (deviceId == null || deviceId.isBlank()) {
            throw new IllegalArgumentException("要先选定一台设备");
        }
        if (now == null) {
            throw new IllegalArgumentException("缺少当前时刻");
        }
        String wanted = deviceId.trim();
        List<Candidate> mine = new ArrayList<>();
        if (rows != null) {
            for (Candidate row : rows) {
                if (row != null && wanted.equals(row.deviceId())) {
                    mine.add(row);
                }
            }
        }
        List<Candidate> running = new ArrayList<>();
        for (Candidate row : mine) {
            if (IN_PROGRESS.equals(row.status())) {
                running.add(row);
            }
        }
        if (running.size() > 1) {
            throw new IllegalArgumentException("这台设备有多条正在执行的任务，不能决定下发哪一条");
        }
        if (running.size() == 1) {
            return running.get(0);
        }
        List<Candidate> due = new ArrayList<>();
        for (Candidate row : mine) {
            if (!PENDING.equals(row.status()) || row.plannedStart() == null) {
                continue;
            }
            if (!row.plannedStart().isAfter(now)) {
                due.add(row);
            }
        }
        if (due.isEmpty()) {
            throw new IllegalArgumentException("这台设备没有已经到点的待执行任务");
        }
        due.sort(Comparator.comparing(Candidate::plannedStart).reversed().thenComparing(Candidate::id, Comparator.reverseOrder()));
        return due.get(0);
    }

    /**
     * 这台设备此刻正在执行的那一条。
     * 没有，或多于一条，只给出原因。待执行的记录不参与。
     */
    public static RunningMatch runningOnly(List<Candidate> rows, String deviceId) {
        if (deviceId == null || deviceId.isBlank()) {
            return RunningMatch.skipped("要先选定一台设备");
        }
        String wanted = deviceId.trim();
        List<Candidate> running = new ArrayList<>();
        if (rows != null) {
            for (Candidate row : rows) {
                if (row != null && wanted.equals(row.deviceId()) && IN_PROGRESS.equals(row.status())) {
                    running.add(row);
                }
            }
        }
        if (running.isEmpty()) {
            return RunningMatch.skipped("这台设备没有正在执行的任务，这次往来未记入任务");
        }
        if (running.size() > 1) {
            return RunningMatch.skipped("这台设备有多条正在执行的任务，这次往来未记入任务");
        }
        return RunningMatch.found(running.get(0).id());
    }

    /**
     * 东古地面站这次往来要记进的执行记录。
     * 报文里没有任务 id 时才用。一条正在执行就用它。否则只有一条已经到点的待执行才用它。
     * 还没到点的不参与。多条正在执行，或多条已经到点，不挑。
     */
    public static RunningMatch forExchange(List<Candidate> rows, String deviceId, OffsetDateTime now) {
        if (deviceId == null || deviceId.isBlank()) {
            return RunningMatch.skipped("要先选定一台设备");
        }
        if (now == null) {
            return RunningMatch.skipped("缺少当前时刻");
        }
        String wanted = deviceId.trim();
        List<Candidate> running = new ArrayList<>();
        List<Candidate> due = new ArrayList<>();
        if (rows != null) {
            for (Candidate row : rows) {
                if (row == null || !wanted.equals(row.deviceId())) {
                    continue;
                }
                if (IN_PROGRESS.equals(row.status())) {
                    running.add(row);
                } else if (PENDING.equals(row.status()) && row.plannedStart() != null && !row.plannedStart().isAfter(now)) {
                    due.add(row);
                }
            }
        }
        if (running.size() > 1) {
            return RunningMatch.skipped("这台设备有多条正在执行的任务，这次往来未记入任务");
        }
        if (running.size() == 1) {
            return RunningMatch.found(running.get(0).id());
        }
        if (due.isEmpty()) {
            return RunningMatch.skipped("这台设备没有正在执行或已经到点的任务，这次往来未记入任务");
        }
        if (due.size() > 1) {
            return RunningMatch.skipped("这台设备有多条已经到点的任务，还没确定执行哪一条，这次往来未记入任务");
        }
        return RunningMatch.found(due.get(0).id());
    }

    /**
     * 恰好一条进行中记录时带上它的编号。否则编号为空，原因说明为什么没记。
     */
    public record RunningMatch(Long recordId, String skipReason) {

        public boolean found() {
            return recordId != null;
        }

        static RunningMatch found(long recordId) {
            return new RunningMatch(recordId, null);
        }

        static RunningMatch skipped(String skipReason) {
            return new RunningMatch(null, skipReason);
        }
    }
}
