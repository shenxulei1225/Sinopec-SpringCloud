package cn.cheers.x.device.protocolgateway.instructiondispatch;

import cn.cheers.x.device.protocolgateway.api.mission.DispatchAction;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * 把已经排好的步骤写成大疆航线文件。
 * <p>负责：到达点写成航点，点上的航线动作按顺序写进动作组，打成含 template.kml 与 waylines.wpml 的 kmz，并算出内容 MD5。
 * 动作参数用调用方已经按对照填好的指令字段，元素名与字段名一致。
 * 航点经纬度读飞向目标点上已选位置里的坐标。坐标在「FLD-LOC-014」这一格，子项是经度、纬度、高度。参数顶上直接写下的经度、纬度、高度同样认。没有这一格就不编一个数。
 * <p>不负责：上传文件、下发报文、东古和机器人的任务指令包、决定哪个动作参数填进哪个元素。
 * <p>禁止：把飞向目标点、一键起飞、直播这些单独报文写进航点动作组；代码里另写一套参数名。
 */
public final class WaylineKmzBuilder {

    /**
     * 只在航线文件里出现的标准动作编号，对应官网 actionActuatorFunc。
     * 点云和探照灯在动作库按用户能理解的操作拆步，WPML 仍使用同一个动作类型，
     * 具体操作值已经在冻结步骤上写好，生成器不再猜。
     */
    static final Map<String, String> FILE_ACTUATORS = Map.ofEntries(
            Map.entry("gimbalRotate", "gimbalRotate"),
            Map.entry("gimbalEvenlyRotate", "gimbalEvenlyRotate"),
            Map.entry("rotateYaw", "rotateYaw"),
            Map.entry("hover", "hover"),
            Map.entry("orientedShoot", "orientedShoot"),
            Map.entry("customDirName", "customDirName"),
            Map.entry("panoShot", "panoShot"),
            Map.entry("recordPointCloudStart", "recordPointCloud"),
            Map.entry("recordPointCloudPause", "recordPointCloud"),
            Map.entry("recordPointCloudResume", "recordPointCloud"),
            Map.entry("recordPointCloudStop", "recordPointCloud"),
            Map.entry("searchlightOn", "searchlight"),
            Map.entry("searchlightStrobe", "searchlight"),
            Map.entry("searchlightOff", "searchlight")
    );

    /** 飞向目标点只提供航点坐标，本身不是航点上的一个动作。 */
    static final String WAYPOINT_ANCHOR = "action-065340437bcb46788b7dc261fab81c63";

    /**
     * 准备并启动航线。三个高度速度和结束方式写在这一步上，不另发一条实时指令。
     * 回到机库是 goHome，在最后一个航点降落是 autoLand。
     */
    static final String MISSION_HEAD = "act-uav-wayline-prepare";

    private static final Set<String> LAT_KEYS = Set.of("latitude", "lat", "target_latitude");
    private static final Set<String> LNG_KEYS = Set.of("longitude", "lng", "target_longitude");
    private static final Set<String> HEIGHT_KEYS = Set.of("height", "executeHeight", "target_height");

    private WaylineKmzBuilder() {
    }

    static boolean isFileAction(String actionCode) {
        return actionCode != null && FILE_ACTUATORS.containsKey(actionCode);
    }

    static boolean containsFileAction(List<DispatchAction> actions) {
        if (actions == null) {
            return false;
        }
        for (DispatchAction action : actions) {
            if (isFileAction(action.actionCode())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 按步骤顺序收航点。飞向目标点打开一个航点；紧随的航线动作挂在这个点上。
     */
    static Built build(List<DispatchAction> actions) {
        List<Waypoint> waypoints = new ArrayList<>();
        Waypoint current = null;
        for (DispatchAction action : actions) {
            if (WAYPOINT_ANCHOR.equals(action.actionCode())) {
                current = new Waypoint(
                        number(action.params(), LNG_KEYS, "经度"),
                        number(action.params(), LAT_KEYS, "纬度"),
                        number(action.params(), HEIGHT_KEYS, "高度"),
                        new ArrayList<>());
                waypoints.add(current);
                continue;
            }
            if (!isFileAction(action.actionCode())) {
                continue;
            }
            if (current == null) {
                throw new IllegalArgumentException("航线动作前面没有到达点，写不出航点");
            }
            current.actions().add(new FileAction(FILE_ACTUATORS.get(action.actionCode()), fileParams(action)));
        }
        if (waypoints.isEmpty()) {
            throw new IllegalArgumentException("航线文件没有航点");
        }
        double speed = number(firstParamsHaving(actions, "max_speed"), Set.of("max_speed"), "飞行速度");
        double securityHeight = number(
                firstParamsHaving(actions, "security_takeoff_height"),
                Set.of("security_takeoff_height"),
                "起飞安全高度");
        double rthAltitude = number(
                firstParamsHaving(actions, "rth_altitude"),
                Set.of("rth_altitude"),
                "返航高度");
        String finish = finishAction(actions);
        String template = document(waypoints, speed, securityHeight, finish, true);
        String waylines = document(waypoints, speed, securityHeight, finish, false);
        byte[] kmz = zip(template, waylines);
        return new Built(kmz, md5(kmz), rthAltitude);
    }

    private static Map<String, Object> firstParamsHaving(List<DispatchAction> actions, String key) {
        for (DispatchAction action : actions) {
            if (findNumber(action.params(), Set.of(key)) != null) {
                return action.params();
            }
        }
        return Map.of();
    }

    /** 指令空包里已经填上的字段原样写成元素。空着的不写。不在这里改名。 */
    private static Map<String, String> fileParams(DispatchAction action) {
        Map<String, String> params = new LinkedHashMap<>();
        if (action.params() == null) {
            return params;
        }
        for (Map.Entry<String, Object> entry : action.params().entrySet()) {
            if (entry.getValue() == null || String.valueOf(entry.getValue()).isBlank()) {
                continue;
            }
            params.put(entry.getKey(), String.valueOf(entry.getValue()));
        }
        return params;
    }

    /**
     * 这次任务选的结束方式。回到机库是 goHome，在最后一个航点降落是 autoLand。没选就停住。
     */
    private static String finishAction(List<DispatchAction> actions) {
        for (DispatchAction action : actions) {
            if (action.params() == null) {
                continue;
            }
            Object raw = action.params().get("finish_action");
            if (raw == null || String.valueOf(raw).isBlank()) {
                continue;
            }
            String text = String.valueOf(raw).trim();
            if ("goHome".equals(text) || "autoLand".equals(text)) {
                return text;
            }
            throw new IllegalArgumentException("航线结束无法识别");
        }
        throw new IllegalArgumentException("航线结束还没选");
    }

    private static String document(
            List<Waypoint> waypoints, double speed, double securityHeight, String finish, boolean template
    ) {
        StringBuilder xml = new StringBuilder();
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        xml.append("<kml xmlns=\"http://www.opengis.net/kml/2.2\" xmlns:wpml=\"http://www.dji.com/wpmz/1.0.2\">\n");
        xml.append("<Document>\n");
        xml.append("<wpml:missionConfig>\n");
        xml.append("<wpml:flyToWaylineMode>safely</wpml:flyToWaylineMode>\n");
        xml.append("<wpml:finishAction>").append(finish).append("</wpml:finishAction>\n");
        xml.append("<wpml:exitOnRCLost>executeLostAction</wpml:exitOnRCLost>\n");
        xml.append("<wpml:executeRCLostAction>goBack</wpml:executeRCLostAction>\n");
        xml.append("<wpml:takeOffSecurityHeight>").append(securityHeight).append("</wpml:takeOffSecurityHeight>\n");
        xml.append("<wpml:globalTransitionalSpeed>").append(speed).append("</wpml:globalTransitionalSpeed>\n");
        xml.append("</wpml:missionConfig>\n");
        xml.append("<Folder>\n");
        xml.append("<wpml:templateId>0</wpml:templateId>\n");
        if (!template) {
            xml.append("<wpml:waylineId>0</wpml:waylineId>\n");
        }
        xml.append("<wpml:autoFlightSpeed>").append(speed).append("</wpml:autoFlightSpeed>\n");
        int groupId = 0;
        for (int i = 0; i < waypoints.size(); i++) {
            Waypoint point = waypoints.get(i);
            xml.append("<Placemark>\n");
            xml.append("<Point><coordinates>")
                    .append(point.longitude()).append(",").append(point.latitude())
                    .append("</coordinates></Point>\n");
            xml.append("<wpml:index>").append(i).append("</wpml:index>\n");
            if (template) {
                xml.append("<wpml:height>").append(point.height()).append("</wpml:height>\n");
                xml.append("<wpml:ellipsoidHeight>").append(point.height()).append("</wpml:ellipsoidHeight>\n");
            } else {
                xml.append("<wpml:executeHeight>").append(point.height()).append("</wpml:executeHeight>\n");
            }
            List<FileAction> atPoint = new ArrayList<>();
            List<FileAction> alongSegment = new ArrayList<>();
            for (FileAction fileAction : point.actions()) {
                if ("gimbalEvenlyRotate".equals(fileAction.actuator())) {
                    alongSegment.add(fileAction);
                } else {
                    atPoint.add(fileAction);
                }
            }
            if (!atPoint.isEmpty()) {
                groupId = writeGroup(xml, groupId, i, i, "reachPoint", atPoint);
            }
            if (!alongSegment.isEmpty()) {
                if (i + 1 >= waypoints.size()) {
                    throw new IllegalArgumentException("航段间均匀转动云台俯仰角需要下一个航点");
                }
                groupId = writeGroup(xml, groupId, i, i + 1, "betweenAdjacentPoints", alongSegment);
            }
            xml.append("</Placemark>\n");
        }
        xml.append("</Folder>\n</Document>\n</kml>\n");
        return xml.toString();
    }

    /**
     * 到达航点的动作用 reachPoint，只在该点生效。
     * 航段间均匀转动云台俯仰角必须用 betweenAdjacentPoints，从当前点生效到下一航点。
     */
    private static int writeGroup(
            StringBuilder xml,
            int groupId,
            int startIndex,
            int endIndex,
            String trigger,
            List<FileAction> actions
    ) {
        xml.append("<wpml:actionGroup>\n");
        xml.append("<wpml:actionGroupId>").append(groupId).append("</wpml:actionGroupId>\n");
        xml.append("<wpml:actionGroupStartIndex>").append(startIndex).append("</wpml:actionGroupStartIndex>\n");
        xml.append("<wpml:actionGroupEndIndex>").append(endIndex).append("</wpml:actionGroupEndIndex>\n");
        xml.append("<wpml:actionGroupMode>sequence</wpml:actionGroupMode>\n");
        xml.append("<wpml:actionTrigger><wpml:actionTriggerType>")
                .append(trigger)
                .append("</wpml:actionTriggerType></wpml:actionTrigger>\n");
        for (int actionIndex = 0; actionIndex < actions.size(); actionIndex++) {
            FileAction fileAction = actions.get(actionIndex);
            xml.append("<wpml:action>\n");
            xml.append("<wpml:actionId>").append(actionIndex).append("</wpml:actionId>\n");
            xml.append("<wpml:actionActuatorFunc>")
                    .append(escape(fileAction.actuator()))
                    .append("</wpml:actionActuatorFunc>\n");
            xml.append("<wpml:actionActuatorFuncParam>\n");
            for (Map.Entry<String, String> entry : fileAction.params().entrySet()) {
                xml.append("<wpml:").append(escape(entry.getKey())).append(">")
                        .append(escape(entry.getValue()))
                        .append("</wpml:").append(escape(entry.getKey())).append(">\n");
            }
            xml.append("</wpml:actionActuatorFuncParam>\n");
            xml.append("</wpml:action>\n");
        }
        xml.append("</wpml:actionGroup>\n");
        return groupId + 1;
    }

    private static byte[] zip(String template, String waylines) {
        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            try (ZipOutputStream zip = new ZipOutputStream(buffer, StandardCharsets.UTF_8)) {
                put(zip, "wpmz/template.kml", template);
                put(zip, "wpmz/waylines.wpml", waylines);
            }
            return buffer.toByteArray();
        } catch (Exception ex) {
            throw new IllegalStateException("航线文件没有打成压缩包", ex);
        }
    }

    private static void put(ZipOutputStream zip, String name, String body) throws Exception {
        zip.putNextEntry(new ZipEntry(name));
        zip.write(body.getBytes(StandardCharsets.UTF_8));
        zip.closeEntry();
    }

    static String md5(byte[] body) {
        try {
            byte[] digest = MessageDigest.getInstance("MD5").digest(body);
            return HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("算不出航线文件签名", ex);
        }
    }

    private static double number(Map<String, Object> params, Set<String> keys, String label) {
        Double found = findNumber(params, keys);
        if (found == null) {
            throw new IllegalArgumentException("航线文件缺少" + label);
        }
        return found;
    }

    private static Double findNumber(Map<String, Object> params, Set<String> keys) {
        if (params == null) {
            return null;
        }
        Double direct = readKeys(params, keys);
        if (direct != null) {
            return direct;
        }
        return readCoordinate(params, keys, 0);
    }

    /**
     * 已选位置里的坐标。点位同步写在 FLD-LOC-014，子项为 longitude、latitude、height。
     * 只读这一格。旁边没有坐标时返回空，不另编经度。
     */
    private static Double readCoordinate(Object node, Set<String> keys, int depth) {
        if (!(node instanceof Map<?, ?> map) || depth > 4) {
            return null;
        }
        Object coordinate = map.get("FLD-LOC-014");
        if (coordinate instanceof Map<?, ?> fields) {
            Double found = readKeys(fields, keys);
            if (found != null) {
                return found;
            }
        }
        for (Object value : map.values()) {
            if (value instanceof Map<?, ?>) {
                Double found = readCoordinate(value, keys, depth + 1);
                if (found != null) {
                    return found;
                }
            }
        }
        return null;
    }

    private static Double readKeys(Map<?, ?> map, Set<String> keys) {
        for (String key : keys) {
            Double parsed = asDouble(map.get(key));
            if (parsed != null) {
                return parsed;
            }
        }
        return null;
    }

    private static Double asDouble(Object value) {
        if (value instanceof Number number) {
            return number.doubleValue();
        }
        if (value instanceof String text && !text.isBlank()) {
            try {
                return Double.parseDouble(text.trim());
            } catch (NumberFormatException ex) {
                return null;
            }
        }
        return null;
    }

    private static String escape(String text) {
        return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    record Waypoint(double longitude, double latitude, double height, List<FileAction> actions) {
    }

    record FileAction(String actuator, Map<String, String> params) {
    }

    record Built(byte[] kmz, String fingerprint, double returnHomeAltitude) {
    }
}
