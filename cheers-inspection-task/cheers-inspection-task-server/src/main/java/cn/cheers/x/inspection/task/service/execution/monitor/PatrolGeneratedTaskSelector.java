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
}
