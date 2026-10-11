package cn.cheers.x.device.protocolsimulator;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 模拟器自己记住的指令包和定时上报开关。
 * <p>负责：这次进程里收下的指令包、哪台设备开了定时状态。
 * <p>不负责：写入平台库。重启后这些记录清空。
 */
public class SimulatorState {

    private final Map<String, SavedPacket> packets = new ConcurrentHashMap<>();
    private final Map<String, ReportSwitch> reports = new ConcurrentHashMap<>();

    public void savePacket(String templateId, String lineId, String packagesJson) {
        packets.put(templateId, new SavedPacket(templateId, lineId, packagesJson));
    }

    public Optional<SavedPacket> packet(String templateId) {
        return Optional.ofNullable(packets.get(templateId));
    }

    public Collection<SavedPacket> packets() {
        return List.copyOf(packets.values());
    }

    public boolean deletePacket(String templateId) {
        return packets.remove(templateId) != null;
    }

    public void saveReportSwitch(String deviceId, boolean enable, Integer frequency, Integer total) {
        reports.put(deviceId, new ReportSwitch(deviceId, enable, frequency, total, 0));
    }

    public Map<String, ReportSwitch> enabledReports() {
        Map<String, ReportSwitch> enabled = new LinkedHashMap<>();
        reports.forEach((id, report) -> {
            if (report.enabled()) {
                enabled.put(id, report);
            }
        });
        return enabled;
    }

    public record SavedPacket(String templateId, String lineId, String packagesJson) {
    }

    public record ReportSwitch(String deviceId, boolean enabled, Integer frequency, Integer total, int sent) {

        public ReportSwitch nextSent() {
            return new ReportSwitch(deviceId, enabled, frequency, total, sent + 1);
        }
    }

    public void markReported(String deviceId) {
        ReportSwitch current = reports.get(deviceId);
        if (current == null) {
            return;
        }
        ReportSwitch next = current.nextSent();
        boolean stillOn = current.total() == null || current.total() < 0 || next.sent() < current.total();
        reports.put(deviceId, new ReportSwitch(
                current.deviceId(), stillOn && current.enabled(), current.frequency(), current.total(), next.sent()));
    }
}
