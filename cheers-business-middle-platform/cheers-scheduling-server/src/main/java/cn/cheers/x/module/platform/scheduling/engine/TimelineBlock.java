package cn.cheers.x.module.platform.scheduling.engine;

import cn.cheers.x.module.platform.contract.dto.reservation.ResourceReservationDTO;
import org.springframework.util.StringUtils;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 同一设备时间轴上的一段已占窗（含任务间隔）。
 * 挪动已有任务时改这里的起止，并回写来源占窗。
 */
final class TimelineBlock {

    private OffsetDateTime start;
    private OffsetDateTime end;
    private final int gapMinutes;
    private final ResourceReservationDTO source;

    TimelineBlock(OffsetDateTime start, OffsetDateTime end, int gapMinutes, ResourceReservationDTO source) {
        this.start = start;
        this.end = end;
        this.gapMinutes = Math.max(0, gapMinutes);
        this.source = source;
    }

    OffsetDateTime start() {
        return start;
    }

    OffsetDateTime end() {
        return end;
    }

    OffsetDateTime blockedEnd() {
        return end.plusMinutes(gapMinutes);
    }

    ResourceReservationDTO source() {
        return source;
    }

    void shiftEarlier(int minutes) {
        start = start.minusMinutes(minutes);
        end = end.minusMinutes(minutes);
        writeBack();
    }

    void shiftLater(int minutes) {
        start = start.plusMinutes(minutes);
        end = end.plusMinutes(minutes);
        writeBack();
    }

    private void writeBack() {
        if (source == null) {
            return;
        }
        String startText = DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(start);
        String endText = DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(end);
        source.setCandidateStart(startText);
        source.setCandidateEnd(endText);
        if (StringUtils.hasText(source.getPlannedStart())) {
            source.setPlannedStart(startText);
        }
        if (StringUtils.hasText(source.getPlannedEnd())) {
            source.setPlannedEnd(endText);
        }
    }
}
