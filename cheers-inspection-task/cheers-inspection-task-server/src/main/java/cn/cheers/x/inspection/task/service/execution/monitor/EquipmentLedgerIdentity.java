package cn.cheers.x.inspection.task.service.execution.monitor;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * 把报文上的编号对到巡检设备的设备编号。
 * <p>执行记录上记的是设备编号。大疆主题上常常是网关序列号或飞机序列号。
 * <p>不负责：挑一条执行记录，也不编一个序列号。
 * <p>禁止：多台设备对上同一个序列号时挑一台。
 */
public final class EquipmentLedgerIdentity {

    private EquipmentLedgerIdentity() {
    }

    /**
     * 一台巡检设备上能用来认报文的三个编号。空着的不参与。
     */
    public record Device(String code, String serialNumber, String aircraftSn) {
    }

    /**
     * code 有值表示恰好对上一台设备。ambiguous 为真表示多台对上，这时 code 为空。
     */
    public record Match(String code, boolean ambiguous) {

        static Match none() {
            return new Match(null, false);
        }

        static Match one(String code) {
            return new Match(code, false);
        }

        static Match many() {
            return new Match(null, true);
        }
    }

    /**
     * 报文编号等于某台设备的设备编号时，就用这个编号。
     * 否则只在恰好一台设备的序列号或飞机序列号等于它时，换成那台的设备编号。
     */
    public static Match resolve(String wireId, List<Device> devices) {
        String wanted = wireId == null ? "" : wireId.trim();
        if (wanted.isEmpty() || devices == null || devices.isEmpty()) {
            return Match.none();
        }
        Set<String> codes = new LinkedHashSet<>();
        for (Device device : devices) {
            if (device == null || !same(wanted, device.code())) {
                continue;
            }
            codes.add(device.code().trim());
        }
        if (codes.isEmpty()) {
            for (Device device : devices) {
                if (device == null || !hasText(device.code())) {
                    continue;
                }
                if (same(wanted, device.serialNumber()) || same(wanted, device.aircraftSn())) {
                    codes.add(device.code().trim());
                }
            }
        }
        if (codes.size() == 1) {
            return Match.one(codes.iterator().next());
        }
        if (codes.size() > 1) {
            return Match.many();
        }
        return Match.none();
    }

    private static boolean same(String wanted, String value) {
        return hasText(value) && wanted.equals(value.trim());
    }

    private static boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}
