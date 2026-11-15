package cn.cheers.x.module.dynamicbusiness.service.security;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.framework.tenant.core.context.TenantContextHolder;
import cn.cheers.x.device.protocolgateway.api.vehicle.VehicleGateActions;
import cn.cheers.x.device.protocolgateway.api.vehicle.VehicleGateCommandApi;
import cn.cheers.x.device.protocolgateway.api.vehicle.VehicleGateCommandReq;
import cn.cheers.x.device.protocolgateway.api.vehicle.VehicleGateCommandResp;
import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.module.dynamicbusiness.api.security.dto.VehicleRestrictionDTO;
import cn.cheers.x.module.dynamicbusiness.api.security.dto.VehicleRestrictionPushResp;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityCreateReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityUpdateReqVO;
import cn.cheers.x.module.dynamicbusiness.dataprocess.AlarmRecordWriter;
import cn.cheers.x.module.dynamicbusiness.dataprocess.AlarmTypeCatalog;
import cn.cheers.x.module.dynamicbusiness.framework.facility.FacilityOwningFieldCodes;
import cn.cheers.x.module.dynamicbusiness.service.entity.EntityService;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 把车道上的一次识别写成车辆进出。
 *
 * 管什么：比对名单和外来车，写下比对结果。允许放行时记下抬杆结果。摄像机这条识别里，禁止进入写入告警账。
 * 网关的识别到车只写进出账，不写门禁告警。允许放行时由网关按对照发抬杆，厂商接受后才写时间。
 * 已经进出按厂商时间写入，不发抬杆。下发限制读车辆登记和还没离开的外来车，按对照发给道闸。
 * 不管什么：车辆在场内的位置。名单和车道的日常维护由车辆管理页写入。厂商主机不写在这里。
 * 禁止：摄像机不在时造一条过车；没有抬杆指令时写成已经抬杆；把禁止进入写成车辆定位告警；把限制读数写成一条进出。
 */
@Service
public class VehicleRecognitionService {

    public static final String PASS = "vehicle_pass";
    public static final String LIST = "vehicle_list";
    public static final String LANE = "vehicle_lane";
    public static final String DISPATCH = "vehicle_dispatch";
    private static final String EQUIPMENT = "equipment";
    private static final ZoneId CLOCK = ZoneId.of("Asia/Shanghai");
    private static final DateTimeFormatter STAMP = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final Set<String> LOCKED = Set.of(
            "这辆车禁止进入",
            "这辆车还在场内",
            "这辆车的允许已经到期",
            "这辆车的允许还没到有效期",
            "这辆车不能走这条车道",
            "被访人已拒绝",
            "没有进入记录",
            "这辆车已停用");

    public record SeenPass(
            long id,
            boolean lift,
            String deviceCode,
            String plate,
            long laneId,
            String direction,
            boolean enter) {
    }

    private final JdbcTemplate jdbcTemplate;
    private final EntityService entityService;
    private final AlarmRecordWriter alarmRecordWriter;
    private final VehicleGateCommandApi vehicleGateCommandApi;

    public VehicleRecognitionService(JdbcTemplate jdbcTemplate, EntityService entityService,
                                     AlarmRecordWriter alarmRecordWriter,
                                     VehicleGateCommandApi vehicleGateCommandApi) {
        this.jdbcTemplate = jdbcTemplate;
        this.entityService = entityService;
        this.alarmRecordWriter = alarmRecordWriter;
        this.vehicleGateCommandApi = vehicleGateCommandApi;
    }

    public record Outcome(long id, String matchResult, String barrierResult, String message) {
    }

    /**
     * 摄像机交出车牌和照片。车道上的摄像机不在时不写过车。
     */
    public Outcome recognize(long facilityId, long laneId, String plate, String plateColor,
                             String scenePhoto, String platePhoto, boolean uncertain) {
        Lane lane = requireLane(facilityId, laneId);
        Camera camera = requireCamera(lane.cameraId());
        LocalDateTime now = LocalDateTime.now(CLOCK);
        VehiclePassDecision.Result decision = VehiclePassDecision.recognize(
                lane.direction(), lane.id(), plate, uncertain, grants(facilityId), visits(facilityId),
                companyUses(facilityId), now);
        long id = apply(facilityId, null, lane, VehiclePassDecision.plateOf(plate), plateColor, scenePhoto, platePhoto,
                decision, "");
        noteAlarm(id, decision, camera, VehiclePassDecision.plateOf(plate));
        if (!(decision.writeEnter() || decision.writeLeave())) {
            return outcome(id, decision, lane);
        }
        String deviceCode = equipmentCode(lane.barrierId());
        if (deviceCode.isEmpty()) {
            return outcome(id, decision, lane);
        }
        VehicleGateCommandResp sent = sendGate(deviceCode, VehicleGateActions.LIFT, liftParams(
                VehiclePassDecision.plateOf(plate), lane.id(), lane.direction()));
        finishLift(id, sent.sent(), sent.message(), decision.writeEnter());
        if (sent.sent()) {
            return new Outcome(id, decision.matchResult(), "已抬杆", decision.message());
        }
        return new Outcome(id, decision.matchResult(), "抬杆失败", sent.message());
    }

    /**
     * 协议网关收来「识别到车」。按这条车道比对后写入进出账。
     * 允许放行且道闸有设备编号时，返回要抬杆。时间等抬杆被接受后再写。
     */
    public SeenPass acceptSeen(long laneId, String plate, String plateColor,
                               String scenePhoto, String platePhoto, boolean uncertain) {
        Lane lane = requireLaneById(laneId);
        requireCamera(lane.cameraId());
        LocalDateTime now = LocalDateTime.now(CLOCK);
        VehiclePassDecision.Result decision = VehiclePassDecision.recognize(
                lane.direction(), lane.id(), plate, uncertain, grants(lane.facilityId()), visits(lane.facilityId()),
                companyUses(lane.facilityId()), now);
        long id = apply(lane.facilityId(), null, lane, VehiclePassDecision.plateOf(plate), plateColor, scenePhoto,
                platePhoto, decision, "");
        boolean lift = decision.writeEnter() || decision.writeLeave();
        String deviceCode = lift ? equipmentCode(lane.barrierId()) : "";
        if (deviceCode.isEmpty()) {
            lift = false;
        }
        return new SeenPass(id, lift, deviceCode, VehiclePassDecision.plateOf(plate), lane.id(), lane.direction(),
                decision.writeEnter());
    }

    /**
     * 抬杆已经被厂商接受时，写下进入或离开时间。没接受时只更新失败原因，不写时间。
     */
    public void finishLift(long passId, boolean sent, String message, boolean enter) {
        PassRow row = requirePass(passId);
        if (!sent) {
            String text = message == null || message.isBlank() ? "这台道闸还没有抬杆指令" : message.trim();
            merge(PASS, passId, null, Map.of("抬杆结果", "抬杆失败", "放行原因", text));
            return;
        }
        LocalDateTime now = LocalDateTime.now(CLOCK);
        if (enter) {
            stamp(passId, "进入时间", now);
        } else {
            stamp(passId, "离开时间", now);
            VehiclePassDecision.Visit open = openVisit(row.plate(), visits(row.facilityId()));
            if (open != null && open.id() != passId) {
                stamp(open.id(), "离开时间", now);
            }
        }
        merge(PASS, passId, null, Map.of("抬杆结果", "已抬杆", "放行原因", "自动放行"));
    }

    /**
     * 协议网关收来「已经进出」。时间用厂商记录里的时间。不发抬杆，不写门禁告警。
     * 方向和这条车道对不上、没有车牌、还在场内又来一条进入时，不写。
     */
    public long acceptRecorded(long laneId, String plate, String direction, LocalDateTime when,
                               String scenePhoto, String platePhoto) {
        Lane lane = requireLaneById(laneId);
        if (!lane.direction().equals(direction)) {
            throw new ServiceException(400, "这条车道的方向对不上");
        }
        String normalized = VehiclePassDecision.plateOf(plate);
        if (normalized.isEmpty()) {
            throw new ServiceException(400, "没有车牌");
        }
        if (when == null) {
            throw new ServiceException(400, "没有进出时间");
        }
        Map<String, Object> values = new LinkedHashMap<>();
        values.put("方向", lane.direction());
        values.put("车道", lane.id());
        values.put("道口", lane.name());
        values.put("全景照片", scenePhoto);
        values.put("车牌照片", platePhoto);
        VehiclePassDecision.Visit open = openVisit(normalized, visits(lane.facilityId()));
        if (VehiclePassDecision.OUT.equals(direction)) {
            long id = save(lane.facilityId(), null, normalized, values);
            stamp(id, "离开时间", when);
            if (open != null) {
                stamp(open.id(), "离开时间", when);
            }
            return id;
        }
        if (open != null) {
            throw new ServiceException(400, "这辆车还在场内");
        }
        long id = save(lane.facilityId(), null, normalized, values);
        stamp(id, "进入时间", when);
        return id;
    }

    /**
     * 下发限制要读的车辆登记。只读已经写下的允许、禁止、有效期、车道，以及公司车的在用或停用。
     * 不写进出，也不表示已经发给厂商。
     */
    public List<VehicleRestrictionDTO> restrictions(long facilityId) {
        List<VehicleRestrictionDTO> rows = new ArrayList<>();
        for (VehiclePassDecision.Grant grant : grants(facilityId)) {
            rows.add(new VehicleRestrictionDTO(
                    grant.plate(),
                    "个人车辆",
                    grant.status(),
                    format(grant.validFrom()),
                    format(grant.validTo()),
                    grant.laneIds() == null ? List.of() : List.copyOf(grant.laneIds())));
        }
        for (VehiclePassDecision.CompanyUse car : companyUses(facilityId)) {
            rows.add(new VehicleRestrictionDTO(
                    car.plate(),
                    "公司车辆",
                    car.inUse() ? "在用" : "停用",
                    format(car.dispatchFrom()),
                    format(car.dispatchTo()),
                    List.of()));
        }
        rows.addAll(guestRestrictions(facilityId));
        return rows;
    }

    /**
     * 把这座站要下发的限制，按每条车道的道闸设备编号发给厂商。
     * 没有道闸、没有对照、地址不是完整 http 地址时，不写成已经下发。
     */
    public VehicleRestrictionPushResp pushRestrictions(long facilityId) {
        List<String> devices = barrierCodes(facilityId);
        if (devices.isEmpty()) {
            throw new ServiceException(400, "这座站的车道还没有道闸");
        }
        List<VehicleRestrictionDTO> rows = restrictions(facilityId);
        if (rows.isEmpty()) {
            throw new ServiceException(400, "这座站还没有要下发的车辆");
        }
        int sent = 0;
        List<String> messages = new ArrayList<>();
        for (String deviceCode : devices) {
            for (VehicleRestrictionDTO row : rows) {
                Map<String, Object> params = new LinkedHashMap<>();
                params.put("车牌", row.plate());
                params.put("种类", row.kind());
                params.put("进出", row.entry());
                params.put("有效期起", row.validFrom());
                params.put("有效期止", row.validTo());
                params.put("允许车道", row.laneIds());
                VehicleGateCommandResp result = sendGate(deviceCode, VehicleGateActions.PUSH, params);
                if (result.sent()) {
                    sent++;
                } else {
                    messages.add(row.plate() + " " + result.message());
                }
            }
        }
        return new VehicleRestrictionPushResp(sent, messages);
    }

    /**
     * 门卫确认这一条。禁止进入、还在场内、已经到期、没有进入记录，都不能改成放行。
     */
    public Outcome confirm(long passId, String reason) {
        String written = reason == null ? "" : reason.trim();
        if (written.isEmpty()) {
            throw new ServiceException(400, "请填写放行原因");
        }
        PassRow row = requirePass(passId);
        if (LOCKED.contains(row.message())) {
            throw new ServiceException(400, row.message());
        }
        if (row.entered() && VehiclePassDecision.IN.equals(row.direction())) {
            throw new ServiceException(400, "这一条已经放行");
        }
        if (row.left() && VehiclePassDecision.OUT.equals(row.direction())) {
            throw new ServiceException(400, "这一条已经放行");
        }
        LocalDateTime now = LocalDateTime.now(CLOCK);
        String barrier = barrierFailure(row.barrierId());
        if (VehiclePassDecision.OUT.equals(row.direction())) {
            VehiclePassDecision.Visit open = openVisit(row.plate(), visits(row.facilityId()));
            if (open == null) {
                throw new ServiceException(400, "没有进入记录");
            }
            stamp(open.id(), "离开时间", now);
            stamp(passId, "离开时间", now);
        } else {
            if (openVisit(row.plate(), visits(row.facilityId())) != null) {
                throw new ServiceException(400, "这辆车还在场内");
            }
            stamp(passId, "进入时间", now);
        }
        Map<String, Object> patch = new LinkedHashMap<>();
        patch.put("比对结果", VehiclePassDecision.MANUAL);
        patch.put("放行原因", written);
        patch.put("抬杆结果", "抬杆失败");
        patch.put("告警说明", barrier);
        merge(PASS, passId, null, patch);
        String deviceCode = equipmentCode(row.barrierId());
        if (deviceCode.isEmpty()) {
            return new Outcome(passId, VehiclePassDecision.MANUAL, "抬杆失败", barrier);
        }
        VehicleGateCommandResp sent = sendGate(deviceCode, VehicleGateActions.LIFT, liftParams(
                row.plate(), row.laneId(), row.direction()));
        if (sent.sent()) {
            merge(PASS, passId, null, Map.of("抬杆结果", "已抬杆"));
            return new Outcome(passId, VehiclePassDecision.MANUAL, "已抬杆", written);
        }
        merge(PASS, passId, null, Map.of("告警说明", sent.message()));
        return new Outcome(passId, VehiclePassDecision.MANUAL, "抬杆失败", sent.message());
    }

    /**
     * 改正这一条的车牌后再比对。已经写下进入或离开时间的不再改。
     */
    public Outcome correctPlate(long passId, String plate) {
        String normalized = VehiclePassDecision.plateOf(plate);
        if (normalized.isEmpty()) {
            throw new ServiceException(400, "请填写车牌");
        }
        PassRow row = requirePass(passId);
        if (row.entered() || row.left()) {
            throw new ServiceException(400, "这一条已经放行");
        }
        Lane lane = requireLane(row.facilityId(), row.laneId());
        LocalDateTime now = LocalDateTime.now(CLOCK);
        VehiclePassDecision.Result decision = VehiclePassDecision.recognize(
                lane.direction(), lane.id(), normalized, false, grants(row.facilityId()), visits(row.facilityId()),
                companyUses(row.facilityId()), now);
        apply(row.facilityId(), passId, lane, normalized, row.plateColor(), row.scenePhoto(), row.platePhoto(),
                decision, "");
        Camera camera = cameraOrMissing(lane.cameraId());
        noteAlarm(passId, decision, camera, normalized);
        return outcome(passId, decision, lane);
    }

    /**
     * 没有车牌。不填一个假号码。原因固定写成无牌，并按车道方向写下时间。
     */
    public Outcome unplated(long facilityId, long laneId) {
        Lane lane = requireLane(facilityId, laneId);
        requireCamera(lane.cameraId());
        LocalDateTime now = LocalDateTime.now(CLOCK);
        if (VehiclePassDecision.OUT.equals(lane.direction())) {
            VehiclePassDecision.Visit open = openVisit("", visits(facilityId));
            if (open == null) {
                throw new ServiceException(400, "没有进入记录");
            }
            stamp(open.id(), "离开时间", now);
        }
        VehiclePassDecision.Result decision = new VehiclePassDecision.Result(
                VehiclePassDecision.MANUAL, "无牌", null, false, false);
        long id = apply(facilityId, null, lane, "", "", "", "", decision, "无牌");
        if (VehiclePassDecision.IN.equals(lane.direction())) {
            stamp(id, "进入时间", now);
        } else {
            stamp(id, "离开时间", now);
        }
        return new Outcome(id, VehiclePassDecision.MANUAL, "抬杆失败", barrierFailure(lane.barrierId()));
    }

    private long apply(long facilityId, Long eventId, Lane lane, String plate, String plateColor,
                       String scenePhoto, String platePhoto, VehiclePassDecision.Result decision,
                       String reason) {
        String barrier = decision.writeEnter() || decision.writeLeave()
                ? barrierFailure(lane.barrierId()) : "";
        Map<String, Object> values = new LinkedHashMap<>();
        values.put("车牌颜色", plateColor);
        values.put("方向", lane.direction());
        values.put("车道", lane.id());
        values.put("道口", lane.name());
        values.put("比对结果", decision.matchResult());
        values.put("全景照片", scenePhoto);
        values.put("车牌照片", platePhoto);
        values.put("抬杆结果", barrier.isEmpty() ? "" : "抬杆失败");
        if (!reason.isEmpty()) {
            values.put("放行原因", reason);
        } else if (!decision.message().isEmpty()) {
            values.put("放行原因", decision.message());
        } else if (!barrier.isEmpty()) {
            values.put("放行原因", barrier);
        }
        // 道闸指令还没有登记。自动放行只记下抬杆失败，不写进入或离开时间。
        if (eventId != null) {
            return merge(PASS, eventId, plate, values);
        }
        if (decision.writeEnter() && decision.updateVisitId() != null) {
            return merge(PASS, decision.updateVisitId(), plate, values);
        }
        return save(facilityId, null, plate, values);
    }

    private Outcome outcome(long id, VehiclePassDecision.Result decision, Lane lane) {
        boolean lift = decision.writeEnter() || decision.writeLeave();
        String failure = lift ? barrierFailure(lane.barrierId()) : "";
        String message = decision.message();
        if (message.isEmpty() && !failure.isEmpty()) {
            message = failure;
        }
        return new Outcome(id, decision.matchResult(), lift ? "抬杆失败" : "", message);
    }

    private void noteAlarm(long passId, VehiclePassDecision.Result decision, Camera camera, String plate) {
        if (!"这辆车禁止进入".equals(decision.message())) {
            return;
        }
        if (camera == null || camera.id() <= 0 || camera.zoneId() == null || camera.zoneId() <= 0) {
            merge(PASS, passId, null, Map.of("告警说明", "没有位置，告警没有写入"));
            return;
        }
        try {
            alarmRecordWriter.open(
                    TenantContextHolder.getRequiredTenantId(),
                    null,
                    "alarm-security-access",
                    "vehicle-deny-" + passId,
                    "禁止进入 " + plate,
                    AlarmTypeCatalog.LEVEL_ABNORMAL,
                    plate + " 禁止进入",
                    camera.id(),
                    camera.zoneId(),
                    plate,
                    "",
                    "",
                    null);
        } catch (RuntimeException ex) {
            String message = ex.getMessage() == null ? "告警没有写入" : ex.getMessage();
            merge(PASS, passId, null, Map.of("告警说明", message));
        }
    }

    private long save(long facilityId, Long id, String plate, Map<String, Object> namedValues) {
        if (id == null) {
            Book book = book(PASS);
            EntityCreateReqVO request = new EntityCreateReqVO();
            request.setRegistryCode(PASS);
            Map<String, Object> base = new LinkedHashMap<>();
            base.put("entityTypeCode", PASS);
            base.put("modelId", book.modelId());
            base.put("name", plate);
            base.put("status", 1);
            base.put(FacilityOwningFieldCodes.FIELD_CODE, FacilityOwningFieldCodes.toApiRef(facilityId));
            putNamed(book, base, namedValues);
            request.setBaseFields(base);
            request.setCustomFields(Map.of());
            return entityService.create(request);
        }
        return merge(PASS, id, plate, namedValues);
    }

    private long merge(String catalog, long id, String plate, Map<String, Object> namedValues) {
        Book book = book(catalog);
        EntityRespVO current = entityService.get(id, catalog);
        if (current == null || current.getBaseFields() == null) {
            throw new ServiceException(404, "没有这一条过车");
        }
        Map<String, Object> base = new LinkedHashMap<>(current.getBaseFields());
        base.put("entityTypeCode", catalog);
        if (plate != null) {
            base.put("name", plate);
        }
        putNamed(book, base, namedValues);
        EntityUpdateReqVO request = new EntityUpdateReqVO();
        request.setId(id);
        request.setRegistryCode(catalog);
        request.setBaseFields(base);
        request.setCustomFields(current.getCustomFields() == null ? Map.of() : current.getCustomFields());
        entityService.update(request);
        return id;
    }

    private void stamp(long id, String fieldName, LocalDateTime time) {
        merge(PASS, id, null, Map.of(fieldName, STAMP.format(time)));
    }

    private void putNamed(Book book, Map<String, Object> base, Map<String, Object> namedValues) {
        for (Map.Entry<String, Object> entry : namedValues.entrySet()) {
            if (entry.getValue() == null || "".equals(entry.getValue())) {
                continue;
            }
            String code = book.code(entry.getKey());
            Object value = entry.getValue();
            if ("车道".equals(entry.getKey()) && value instanceof Long laneId) {
                Map<String, Object> ref = new LinkedHashMap<>();
                ref.put("entityTypeCode", LANE);
                ref.put("id", laneId);
                value = ref;
            }
            base.put(code, value);
        }
    }

    private Lane requireLane(long facilityId, long laneId) {
        Book book = book(LANE);
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                "SELECT id, name, " + book.column("方向") + " AS direction, "
                        + book.column("识别摄像机") + " AS camera_id, "
                        + book.column("道闸") + " AS barrier_id, facility_id "
                        + "FROM dynamicbusiness." + book.table()
                        + " WHERE id = ? AND deleted = false",
                laneId);
        if (rows.isEmpty()) {
            throw new ServiceException(404, "没有这条车道");
        }
        Map<String, Object> row = rows.get(0);
        Long owning = longValue(row.get("facility_id"));
        if (owning == null || owning != facilityId) {
            throw new ServiceException(404, "没有这条车道");
        }
        String direction = text(row.get("direction"));
        if (!VehiclePassDecision.IN.equals(direction) && !VehiclePassDecision.OUT.equals(direction)) {
            throw new ServiceException(400, "这条车道还没有方向");
        }
        return new Lane(laneId, owning, text(row.get("name")), direction, longValue(row.get("camera_id")), longValue(row.get("barrier_id")));
    }

    private Lane requireLaneById(long laneId) {
        Book book = book(LANE);
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                "SELECT id, name, " + book.column("方向") + " AS direction, "
                        + book.column("识别摄像机") + " AS camera_id, "
                        + book.column("道闸") + " AS barrier_id, facility_id "
                        + "FROM dynamicbusiness." + book.table()
                        + " WHERE id = ? AND deleted = false",
                laneId);
        if (rows.isEmpty()) {
            throw new ServiceException(404, "没有这条车道");
        }
        Map<String, Object> row = rows.get(0);
        Long owning = longValue(row.get("facility_id"));
        if (owning == null) {
            throw new ServiceException(404, "没有这条车道");
        }
        String direction = text(row.get("direction"));
        if (!VehiclePassDecision.IN.equals(direction) && !VehiclePassDecision.OUT.equals(direction)) {
            throw new ServiceException(400, "这条车道还没有方向");
        }
        return new Lane(laneId, owning, text(row.get("name")), direction, longValue(row.get("camera_id")), longValue(row.get("barrier_id")));
    }

    private Camera requireCamera(Long cameraId) {
        Camera camera = cameraOrMissing(cameraId);
        if (camera == null) {
            throw new ServiceException(400, cameraId == null || cameraId <= 0
                    ? "这条车道还没有识别摄像机"
                    : "识别设备不在线");
        }
        return camera;
    }

    private Camera cameraOrMissing(Long cameraId) {
        if (cameraId == null || cameraId <= 0) {
            return null;
        }
        Book equipment = book(EQUIPMENT);
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                "SELECT id, zone_id FROM dynamicbusiness." + equipment.table()
                        + " WHERE id = ? AND deleted = false",
                cameraId);
        if (rows.isEmpty()) {
            return null;
        }
        return new Camera(cameraId, longValue(rows.get(0).get("zone_id")));
    }

    private List<VehiclePassDecision.Grant> grants(long facilityId) {
        Book book = book(LIST);
        return jdbcTemplate.query(
                "SELECT name, " + book.column("名单") + " AS status, "
                        + book.column("有效期起") + " AS valid_from, "
                        + book.column("有效期止") + " AS valid_to, "
                        + book.column("允许车道") + " AS lanes "
                        + "FROM dynamicbusiness." + book.table()
                        + " WHERE deleted = false AND facility_id = ?",
                (rs, row) -> new VehiclePassDecision.Grant(
                        rs.getString("name"),
                        rs.getString("status"),
                        time(rs.getTimestamp("valid_from")),
                        time(rs.getTimestamp("valid_to")),
                        laneSet(rs.getString("lanes"))),
                facilityId);
    }

    /**
     * 公司车是车辆登记里的「公司车辆」型号。调度账还在时才带上还没结束的调度。
     * 调度不是数据目录时，公司车按没有调度处理，不挡住个人车。
     */
    private List<VehiclePassDecision.CompanyUse> companyUses(long facilityId) {
        if (!catalogReady(LIST)) {
            return List.of();
        }
        Book cars = book(LIST);
        if (!cars.has("使用状态")) {
            return List.of();
        }
        boolean jobsReady = catalogReady(DISPATCH);
        Book jobs = jobsReady ? book(DISPATCH) : null;
        if (jobs != null && (!jobs.has("车辆") || !jobs.has("有效期起") || !jobs.has("有效期止") || !jobs.has("结束时间"))) {
            jobs = null;
        }
        String sql = "SELECT car.name AS plate, car." + cars.column("使用状态") + " AS use_status, "
                + (jobs == null
                ? "NULL::bigint AS job_id, NULL::timestamp AS valid_from, NULL::timestamp AS valid_to "
                : "job.id AS job_id, job." + jobs.column("有效期起") + " AS valid_from, job."
                        + jobs.column("有效期止") + " AS valid_to ")
                + "FROM dynamicbusiness." + cars.table() + " car "
                + "JOIN dynamicbusiness.dynamic_model model ON model.id = car.model_id AND model.deleted = false "
                + "AND model.name = '公司车辆' "
                + (jobs == null
                ? ""
                : "LEFT JOIN dynamicbusiness." + jobs.table() + " job ON job." + jobs.column("车辆")
                        + " = car.id AND job.deleted = false AND job." + jobs.column("结束时间") + " IS NULL ")
                + "WHERE car.deleted = false AND car.facility_id = ?";
        return jdbcTemplate.query(
                sql,
                (rs, row) -> new VehiclePassDecision.CompanyUse(
                        rs.getString("plate"),
                        "在用".equals(rs.getString("use_status")),
                        rs.getObject("job_id") != null,
                        time(rs.getTimestamp("valid_from")),
                        time(rs.getTimestamp("valid_to"))),
                facilityId);
    }

    private boolean catalogReady(String catalog) {
        Long tenantId = TenantContextHolder.getRequiredTenantId();
        Integer count = jdbcTemplate.queryForObject(
                """
                        SELECT COUNT(*)
                        FROM dynamicbusiness.dynamic_entity_type
                        WHERE tenant_id = ? AND code = ? AND deleted = false
                          AND dedicated_table_name IS NOT NULL
                        """,
                Integer.class, tenantId, catalog);
        return count != null && count > 0;
    }

    private List<VehiclePassDecision.Visit> visits(long facilityId) {
        Book book = book(PASS);
        return jdbcTemplate.query(
                "SELECT id, name, "
                        + book.column("同意时间") + " AS agreed_at, "
                        + book.column("拒绝时间") + " AS rejected_at, "
                        + book.column("有效期起") + " AS valid_from, "
                        + book.column("有效期止") + " AS valid_to, "
                        + book.column("进入时间") + " AS entered_at, "
                        + book.column("离开时间") + " AS left_at "
                        + "FROM dynamicbusiness." + book.table()
                        + " WHERE deleted = false AND facility_id = ?",
                (rs, row) -> new VehiclePassDecision.Visit(
                        rs.getLong("id"),
                        rs.getString("name"),
                        time(rs.getTimestamp("agreed_at")),
                        time(rs.getTimestamp("rejected_at")),
                        time(rs.getTimestamp("valid_from")),
                        time(rs.getTimestamp("valid_to")),
                        time(rs.getTimestamp("entered_at")),
                        time(rs.getTimestamp("left_at"))),
                facilityId);
    }

    private VehiclePassDecision.Visit openVisit(String plate, List<VehiclePassDecision.Visit> visits) {
        VehiclePassDecision.Visit found = null;
        for (VehiclePassDecision.Visit visit : visits) {
            if (!VehiclePassDecision.plateOf(plate).equals(VehiclePassDecision.plateOf(visit.plate()))) {
                continue;
            }
            if (visit.enteredAt() != null && visit.leftAt() == null && (found == null || visit.id() > found.id())) {
                found = visit;
            }
        }
        return found;
    }

    private PassRow requirePass(long passId) {
        Book book = book(PASS);
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                "SELECT id, name, facility_id, "
                        + book.column("方向") + " AS direction, "
                        + book.column("车道") + " AS lane_id, "
                        + book.column("比对结果") + " AS match_result, "
                        + book.column("放行原因") + " AS release_reason, "
                        + book.column("进入时间") + " AS entered_at, "
                        + book.column("离开时间") + " AS left_at, "
                        + book.column("车牌颜色") + " AS plate_color, "
                        + book.column("全景照片") + " AS scene_photo, "
                        + book.column("车牌照片") + " AS plate_photo "
                        + "FROM dynamicbusiness." + book.table()
                        + " WHERE id = ? AND deleted = false",
                passId);
        if (rows.isEmpty()) {
            throw new ServiceException(404, "没有这一条过车");
        }
        Map<String, Object> row = rows.get(0);
        Long facilityId = longValue(row.get("facility_id"));
        Long laneId = longValue(row.get("lane_id"));
        if (facilityId == null || laneId == null) {
            throw new ServiceException(400, "这一条过车还没有车道");
        }
        Lane lane = requireLane(facilityId, laneId);
        return new PassRow(
                passId,
                facilityId,
                laneId,
                text(row.get("name")),
                text(row.get("direction")),
                text(row.get("match_result")),
                text(row.get("release_reason")),
                row.get("entered_at") != null,
                row.get("left_at") != null,
                text(row.get("plate_color")),
                text(row.get("scene_photo")),
                text(row.get("plate_photo")),
                lane.barrierId());
    }

    private Book book(String catalog) {
        Long tenantId = TenantContextHolder.getRequiredTenantId();
        List<Map<String, Object>> types = jdbcTemplate.queryForList(
                """
                        SELECT dedicated_table_name
                        FROM dynamicbusiness.dynamic_entity_type
                        WHERE tenant_id = ? AND code = ? AND deleted = false
                        """,
                tenantId, catalog);
        if (types.isEmpty() || types.get(0).get("dedicated_table_name") == null) {
            throw new ServiceException(400, "这本账没有打开");
        }
        String table = String.valueOf(types.get(0).get("dedicated_table_name"));
        if (!table.matches("ent_[a-z0-9_]+")) {
            throw new ServiceException(400, "这本账没有打开");
        }
        List<Long> models = jdbcTemplate.query(
                """
                        SELECT id FROM dynamicbusiness.dynamic_model
                        WHERE tenant_id = ? AND entity_type_code = ? AND deleted = false
                        ORDER BY id LIMIT 1
                        """,
                (rs, row) -> rs.getLong(1), tenantId, catalog);
        if (models.isEmpty()) {
            throw new ServiceException(400, "这本账还没有型号，先不要登记");
        }
        Map<String, String> columns = new LinkedHashMap<>();
        jdbcTemplate.query(
                """
                        SELECT field_name, field_code
                        FROM dynamicbusiness.dynamic_entity_type_base_field
                        WHERE tenant_id = ? AND entity_type_code = ? AND deleted = false
                        """,
                rs -> {
                    String code = rs.getString("field_code");
                    String column = code == null ? "" : code.toLowerCase(java.util.Locale.ROOT).replace('-', '_');
                    if (column.matches("[a-z][a-z0-9_]*")) {
                        columns.put(rs.getString("field_name"), column);
                    }
                },
                tenantId, catalog);
        return new Book(table, models.get(0), columns);
    }

    private static Map<String, Object> liftParams(String plate, long laneId, String direction) {
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("车牌", plate);
        params.put("车道", laneId);
        params.put("方向", direction);
        return params;
    }

    private static String barrierFailure(Long barrierId) {
        if (barrierId == null || barrierId <= 0) {
            return "这条车道还没有道闸";
        }
        return "这台道闸还没有抬杆指令";
    }

    private String equipmentCode(Long equipmentId) {
        if (equipmentId == null || equipmentId <= 0) {
            return "";
        }
        Book equipment = book(EQUIPMENT);
        List<String> codes = jdbcTemplate.query(
                "SELECT code FROM dynamicbusiness." + equipment.table()
                        + " WHERE id = ? AND deleted = false",
                (rs, row) -> rs.getString("code"),
                equipmentId);
        if (codes.isEmpty() || codes.get(0) == null) {
            return "";
        }
        return codes.get(0).trim();
    }

    private List<String> barrierCodes(long facilityId) {
        Book lanes = book(LANE);
        Book equipment = book(EQUIPMENT);
        return jdbcTemplate.query(
                "SELECT DISTINCT eq.code AS code FROM dynamicbusiness." + lanes.table() + " lane "
                        + "JOIN dynamicbusiness." + equipment.table() + " eq "
                        + "ON eq.id = lane." + lanes.column("道闸") + " AND eq.deleted = false "
                        + "WHERE lane.deleted = false AND lane.facility_id = ? "
                        + "AND eq.code IS NOT NULL AND eq.code <> ''",
                (rs, row) -> rs.getString("code").trim(),
                facilityId);
    }

    private List<VehicleRestrictionDTO> guestRestrictions(long facilityId) {
        Book book = book(PASS);
        return jdbcTemplate.query(
                "SELECT name, "
                        + book.column("同意时间") + " AS agreed_at, "
                        + book.column("拒绝时间") + " AS rejected_at, "
                        + book.column("有效期起") + " AS valid_from, "
                        + book.column("有效期止") + " AS valid_to "
                        + "FROM dynamicbusiness." + book.table()
                        + " WHERE deleted = false AND facility_id = ? AND "
                        + book.column("登记时间") + " IS NOT NULL AND "
                        + book.column("离开时间") + " IS NULL",
                (rs, row) -> new VehicleRestrictionDTO(
                        rs.getString("name"),
                        "外来车",
                        guestEntry(rs.getTimestamp("agreed_at"), rs.getTimestamp("rejected_at")),
                        format(time(rs.getTimestamp("valid_from"))),
                        format(time(rs.getTimestamp("valid_to"))),
                        List.of()),
                facilityId);
    }

    private static String guestEntry(Timestamp agreed, Timestamp rejected) {
        if (rejected != null) {
            return "已拒绝";
        }
        if (agreed != null) {
            return "已同意";
        }
        return "待同意";
    }

    private VehicleGateCommandResp sendGate(String deviceCode, String actionCode, Map<String, Object> params) {
        CommonResult<VehicleGateCommandResp> result = vehicleGateCommandApi.send(
                new VehicleGateCommandReq(deviceCode, actionCode, params));
        if (result == null || !result.isSuccess() || result.getData() == null) {
            String text = result == null || result.getMsg() == null ? "指令没有发出" : result.getMsg();
            return new VehicleGateCommandResp(false, text);
        }
        return result.getData();
    }

    private static Set<Long> laneSet(String raw) {
        Set<Long> ids = new LinkedHashSet<>();
        if (raw == null || raw.isBlank()) {
            return ids;
        }
        for (String part : raw.split(",")) {
            try {
                long id = Long.parseLong(part.trim());
                if (id > 0) {
                    ids.add(id);
                }
            } catch (NumberFormatException ignored) {
                // 允许车道里的空段跳过。不是车道编号的文字不当成一条车道。
            }
        }
        return ids;
    }

    private static LocalDateTime time(Timestamp value) {
        return value == null ? null : value.toLocalDateTime();
    }

    private static Long longValue(Object value) {
        if (value instanceof Number number) {
            long id = number.longValue();
            return id > 0 ? id : null;
        }
        return null;
    }

    private static String text(Object value) {
        return value == null ? "" : String.valueOf(value).trim();
    }

    private static String format(LocalDateTime time) {
        return time == null ? "" : STAMP.format(time);
    }

    private record Book(String table, long modelId, Map<String, String> columns) {
        String code(String name) {
            return column(name);
        }

        String column(String name) {
            String column = columns.get(name);
            if (column == null) {
                throw new ServiceException(400, "还没有「" + name + "」这一栏");
            }
            return column;
        }

        boolean has(String name) {
            return columns.containsKey(name);
        }
    }

    private record Lane(long id, long facilityId, String name, String direction, Long cameraId, Long barrierId) {
    }

    private record Camera(long id, Long zoneId) {
    }

    private record PassRow(
            long id,
            long facilityId,
            long laneId,
            String plate,
            String direction,
            String matchResult,
            String message,
            boolean entered,
            boolean left,
            String plateColor,
            String scenePhoto,
            String platePhoto,
            Long barrierId) {
    }
}
