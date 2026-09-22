package cn.cheers.x.module.platform.scheduling.conflict;

import cn.cheers.x.module.platform.contract.dto.reservation.ResourceReservationDTO;
import cn.cheers.x.module.platform.contract.dto.schedule.ScheduleConflictDayDTO;
import cn.cheers.x.module.platform.contract.dto.schedule.ScheduleConflictDeviceDTO;
import cn.cheers.x.module.platform.contract.dto.schedule.ScheduleConflictOverlapDTO;
import cn.cheers.x.module.platform.contract.dto.schedule.ScheduleConflictReportDTO;
import cn.cheers.x.module.platform.contract.dto.schedule.ScheduleConflictWindowDTO;
import cn.cheers.x.module.platform.contract.dto.slot.AssignedResourceDTO;
import cn.cheers.x.module.platform.contract.dto.work.ResourceRequirementDTO;
import cn.cheers.x.module.platform.contract.dto.work.TimePreferencesDTO;
import cn.cheers.x.module.platform.contract.dto.work.TimeWindowDTO;
import cn.cheers.x.module.platform.contract.dto.work.WorkItemDTO;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 标准冲突检测：本批计划窗对已有占窗，按设备、按天出重叠报告。
 * <p>不管：挪已有、换设备、写试排快照。
 */
public final class ScheduleConflictReporter {

    private ScheduleConflictReporter() {
    }

    public static ScheduleConflictReportDTO report(
            List<WorkItemDTO> workItems,
            List<ResourceReservationDTO> occupied,
            int taskGapMinutes) {
        int gap = Math.max(0, taskGapMinutes);
        List<Window> planned = extractPlanned(workItems);
        List<Window> existing = extractOccupied(occupied);
        Map<String, DeviceBuckets> byDevice = new LinkedHashMap<>();
        int conflictCount = 0;
        for (Window plan : planned) {
            DeviceBuckets buckets = byDevice.computeIfAbsent(plan.resourceId, key -> new DeviceBuckets(plan.resourceType));
            buckets.planned.add(plan);
            for (Window other : existing) {
                if (!plan.resourceId.equals(other.resourceId)) {
                    continue;
                }
                if (!sameDay(plan.start, other.start) && !overlaps(plan.start, plan.end, other.start, other.end.plusMinutes(gap))) {
                    continue;
                }
                buckets.existing.add(other);
                OffsetDateTime overlapStart = plan.start.isAfter(other.start) ? plan.start : other.start;
                OffsetDateTime visualEnd = other.end.plusMinutes(gap);
                OffsetDateTime overlapEnd = plan.end.isBefore(visualEnd) ? plan.end : visualEnd;
                if (overlapStart.isBefore(overlapEnd)) {
                    buckets.overlaps.add(new Overlap(plan.resourceId, dateKey(plan.start), overlapStart, overlapEnd));
                    conflictCount++;
                }
            }
        }
        List<ScheduleConflictDeviceDTO> devices = new ArrayList<>();
        for (Map.Entry<String, DeviceBuckets> entry : byDevice.entrySet()) {
            List<ScheduleConflictDayDTO> days = toConflictDays(entry.getValue());
            if (days.isEmpty()) {
                continue;
            }
            devices.add(ScheduleConflictDeviceDTO.builder()
                    .resourceId(entry.getKey())
                    .resourceType(entry.getValue().resourceType)
                    .days(days)
                    .build());
        }
        return ScheduleConflictReportDTO.builder()
                .hasConflict(!devices.isEmpty())
                .plannedCount(planned.size())
                .conflictCount(conflictCount)
                .devices(devices)
                .build();
    }

    private static List<ScheduleConflictDayDTO> toConflictDays(DeviceBuckets buckets) {
        Map<String, List<Overlap>> overlapByDate = new LinkedHashMap<>();
        for (Overlap overlap : buckets.overlaps) {
            overlapByDate.computeIfAbsent(overlap.date, key -> new ArrayList<>()).add(overlap);
        }
        List<ScheduleConflictDayDTO> days = new ArrayList<>();
        for (Map.Entry<String, List<Overlap>> entry : overlapByDate.entrySet()) {
            String date = entry.getKey();
            days.add(ScheduleConflictDayDTO.builder()
                    .date(date)
                    .planned(toWindows(buckets.planned, date))
                    .existing(toWindows(new ArrayList<>(buckets.existing), date))
                    .overlaps(entry.getValue().stream()
                            .sorted(Comparator.comparing(item -> item.start))
                            .map(item -> ScheduleConflictOverlapDTO.builder()
                                    .start(fmt(item.start))
                                    .end(fmt(item.end))
                                    .build())
                            .toList())
                    .build());
        }
        days.sort(Comparator.comparing(ScheduleConflictDayDTO::getDate));
        return days;
    }

    private static List<ScheduleConflictWindowDTO> toWindows(List<Window> source, String date) {
        return source.stream()
                .filter(item -> date.equals(dateKey(item.start)))
                .sorted(Comparator.comparing(item -> item.start))
                .map(item -> ScheduleConflictWindowDTO.builder()
                        .start(fmt(item.start))
                        .end(fmt(item.end))
                        .label(item.label)
                        .build())
                .toList();
    }

    private static List<Window> extractPlanned(List<WorkItemDTO> workItems) {
        List<Window> result = new ArrayList<>();
        if (workItems == null) {
            return result;
        }
        for (WorkItemDTO item : workItems) {
            OffsetDateTime start = preferredStart(item);
            if (start == null || item.getEstimatedDuration() == null || item.getEstimatedDuration() <= 0) {
                continue;
            }
            OffsetDateTime end = start.plusMinutes(item.getEstimatedDuration());
            for (String resourceId : resourceIds(item.getResourceRequirements())) {
                result.add(new Window(resourceId, firstResourceType(item), start, end,
                        hasText(item.getWorkId()) ? item.getWorkId() : "本任务计划"));
            }
        }
        return result;
    }

    private static List<Window> extractOccupied(List<ResourceReservationDTO> occupied) {
        List<Window> result = new ArrayList<>();
        if (occupied == null) {
            return result;
        }
        for (ResourceReservationDTO item : occupied) {
            OffsetDateTime start = parseTime(firstText(item.getCandidateStart(), item.getPlannedStart()));
            OffsetDateTime end = parseTime(firstText(item.getCandidateEnd(), item.getPlannedEnd()));
            if (start == null || end == null) {
                continue;
            }
            List<AssignedResourceDTO> assigned = item.getAssignedResources();
            if (assigned == null || assigned.isEmpty()) {
                continue;
            }
            for (AssignedResourceDTO resource : assigned) {
                if (resource == null || !hasText(resource.getResourceId())) {
                    continue;
                }
                String label = hasText(item.getWorkId()) ? item.getWorkId() : "已有任务";
                result.add(new Window(resource.getResourceId().trim(), resource.getResourceType(), start, end, label));
            }
        }
        return result;
    }

    private static List<String> resourceIds(List<ResourceRequirementDTO> requirements) {
        List<String> ids = new ArrayList<>();
        if (requirements == null) {
            return ids;
        }
        for (ResourceRequirementDTO req : requirements) {
            if (req == null || !hasText(req.getFixedResourceId())) {
                continue;
            }
            ids.add(req.getFixedResourceId().trim());
        }
        return ids;
    }

    private static String firstResourceType(WorkItemDTO item) {
        if (item.getResourceRequirements() == null) {
            return null;
        }
        for (ResourceRequirementDTO req : item.getResourceRequirements()) {
            if (req != null && hasText(req.getResourceType())) {
                return req.getResourceType();
            }
        }
        return null;
    }

    private static OffsetDateTime preferredStart(WorkItemDTO item) {
        TimePreferencesDTO prefs = item.getTimePreferences();
        if (prefs == null || prefs.getAllowedWindows() == null || prefs.getAllowedWindows().isEmpty()) {
            return null;
        }
        TimeWindowDTO window = prefs.getAllowedWindows().get(0);
        return window == null ? null : parseTime(window.getStart());
    }

    private static OffsetDateTime parseTime(String text) {
        if (!hasText(text)) {
            return null;
        }
        return OffsetDateTime.parse(text, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }

    private static String firstText(String preferred, String fallback) {
        return hasText(preferred) ? preferred : fallback;
    }

    private static boolean sameDay(OffsetDateTime left, OffsetDateTime right) {
        return dateKey(left).equals(dateKey(right));
    }

    private static boolean overlaps(OffsetDateTime aStart, OffsetDateTime aEnd,
                                    OffsetDateTime bStart, OffsetDateTime bEnd) {
        return aStart.isBefore(bEnd) && bStart.isBefore(aEnd);
    }

    private static String dateKey(OffsetDateTime time) {
        return time.toLocalDate().toString();
    }

    private static String fmt(OffsetDateTime time) {
        return DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(time);
    }

    private static boolean hasText(String text) {
        return text != null && !text.isBlank();
    }

    private record Window(String resourceId, String resourceType,
                          OffsetDateTime start, OffsetDateTime end, String label) {
    }

    private record Overlap(String resourceId, String date, OffsetDateTime start, OffsetDateTime end) {
    }

    private static final class DeviceBuckets {
        private final String resourceType;
        private final List<Window> planned = new ArrayList<>();
        private final java.util.LinkedHashSet<Window> existing = new java.util.LinkedHashSet<>();
        private final List<Overlap> overlaps = new ArrayList<>();

        private DeviceBuckets(String resourceType) {
            this.resourceType = resourceType;
        }
    }
}
