package cn.cheers.x.module.dynamicbusiness.service.point;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.module.dynamicbusiness.api.point.dto.PathStationPointSyncReqDTO;
import cn.cheers.x.module.dynamicbusiness.api.point.dto.PathStationPointSyncRespDTO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityCreateReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityDeleteReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityUpdateReqVO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entity.EntityDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.model.ModelDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.model.ModelMapper;
import cn.cheers.x.module.dynamicbusiness.dal.repository.entity.EntityRepository;
import cn.cheers.x.module.dynamicbusiness.dal.repository.entity.PhysicalColumnFilter;
import cn.cheers.x.module.dynamicbusiness.framework.facility.FacilityOwningFieldCodes;
import cn.cheers.x.module.dynamicbusiness.service.entity.EntityService;
import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * 路网节点 → 数据管理「路网点位」同步（DOMAIN route_network / 型号 point_route_network）。
 *
 * <p>管什么：按设施 + 节点 nodeId upsert 到路网点位型号；写入点位类型（停靠站/途径点/门点）与归属网；
 * 已从路网移除的同步点位软删。</p>
 * <p>不管什么：路网几何、边权重、三维编辑；通用点位（NATIVE point_standard）；
 * 巡检业务如何筛点（REF / SOP 另算）。</p>
 * <p>禁止：把坐标写入点位台账；一期点位类型仅认 STATION/TRAVERSAL/DOOR（写死枚举，不开放用户自增）；
 * 禁止写入通用「标准点位」型号；禁止在未按精确 code 对账时盲目 create（会撞 code+tenant 唯一索引）。</p>
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PathStationPointSyncService {

    public static final String POINT_ENTITY_TYPE = "point";
    /** 路网 DOMAIN 型号（domain=route_network）；同步权威落点 */
    public static final String POINT_ROUTE_NETWORK_MODEL_CODE = "point_route_network";
    /** @deprecated 路网同步已改用 {@link #POINT_ROUTE_NETWORK_MODEL_CODE}；保留常量避免外部编译依赖断裂 */
    @Deprecated
    public static final String POINT_STANDARD_MODEL_CODE = "point_standard";
    /** 路网节点 id（种子 FLD-PNT-002；语义键 station_node_id 兼容旧数据） */
    public static final String PATH_NODE_FIELD_SEED_CODE = "FLD-PNT-002";
    public static final String PATH_NODE_FIELD_SEMANTIC = "station_node_id";
    /** 点位类型（种子 FLD-PNT-004；一期写死 STATION/TRAVERSAL/DOOR） */
    public static final String POINT_KIND_FIELD_SEED_CODE = "FLD-PNT-004";
    public static final String POINT_KIND_FIELD_SEMANTIC = "point_kind";
    /** 归属网（种子 FLD-PNT-005；MULTI_SELECT 数组 HUMAN/GROUND_ROBOT/UAV） */
    public static final String MEMBERSHIP_FIELD_SEED_CODE = "FLD-PNT-005";
    public static final String MEMBERSHIP_FIELD_SEMANTIC = "network_membership";
    /** 型号分配必填：所属设施 REF（与 facility_id 同指设施，须一并写入 customFields） */
    public static final String FACILITY_FIELD_SEED_CODE = "FLD-PNT-001";

    /** @deprecated 使用 {@link #PATH_NODE_FIELD_SEED_CODE} */
    @Deprecated
    public static final String STATION_FIELD_SEED_CODE = PATH_NODE_FIELD_SEED_CODE;
    /** @deprecated 使用 {@link #PATH_NODE_FIELD_SEMANTIC} */
    @Deprecated
    public static final String STATION_FIELD_SEMANTIC = PATH_NODE_FIELD_SEMANTIC;

    private static final Set<String> ALLOWED_POINT_KINDS = Set.of("STATION", "TRAVERSAL", "DOOR");
    private static final Set<String> ALLOWED_MEMBERSHIPS = Set.of("HUMAN", "GROUND_ROBOT", "UAV");
    private static final List<String> DEFAULT_MEMBERSHIPS =
            List.of("HUMAN", "GROUND_ROBOT", "UAV");

    private final EntityService entityService;
    private final EntityRepository entityRepository;
    private final ModelMapper modelMapper;

    @Transactional(rollbackFor = Exception.class)
    public PathStationPointSyncRespDTO syncFromPathNetwork(PathStationPointSyncReqDTO req) {
        if (req == null || req.getFacilityId() == null || req.getFacilityId() <= 0) {
            throw new ServiceException(400, "设施编号无效，无法同步点位");
        }
        Long facilityId = req.getFacilityId();
        ModelDO model = modelMapper.selectByCode(POINT_ROUTE_NETWORK_MODEL_CODE);
        if (model == null || model.getId() == null) {
            throw new ServiceException(500, "路网点位型号不存在：" + POINT_ROUTE_NETWORK_MODEL_CODE);
        }

        List<PathStationPointSyncReqDTO.StationNodeItem> stations =
                req.getStations() == null ? List.of() : req.getStations();

        // 权威对账：本设施下 PN-*（不限旧型号），upsert 时迁到路网型号
        Map<String, EntityDO> existingByCode = indexSyncedPoints(facilityId);
        Set<String> keepCodes = new HashSet<>();
        int created = 0;
        int updated = 0;

        for (PathStationPointSyncReqDTO.StationNodeItem item : stations) {
            if (item == null || StrUtil.isBlank(item.getNodeId())) {
                continue;
            }
            String nodeId = item.getNodeId().trim();
            String pointKind = normalizePointKind(item.getNodeType());
            List<String> memberships = normalizeMemberships(item.getMemberships());
            String code = buildPointCode(facilityId, nodeId);
            keepCodes.add(code);
            String name = StrUtil.blankToDefault(
                    item.getDisplayName() == null ? null : item.getDisplayName().trim(),
                    nodeId);
            EntityDO existing = existingByCode.get(code);
            if (existing == null) {
                // 索引未命中时仍按精确 code 再查一次，避免误 create 撞唯一索引
                existing = entityRepository.findByExactCode(POINT_ENTITY_TYPE, code);
                if (existing != null) {
                    existingByCode.put(code, existing);
                }
            }
            if (existing == null) {
                createPoint(model.getId(), facilityId, code, name, nodeId, pointKind, memberships);
                created++;
            } else {
                updatePoint(existing, model.getId(), facilityId, code, name, nodeId, pointKind, memberships);
                updated++;
            }
        }

        int deleted = 0;
        for (Map.Entry<String, EntityDO> e : existingByCode.entrySet()) {
            if (keepCodes.contains(e.getKey())) {
                continue;
            }
            EntityDeleteReqVO del = new EntityDeleteReqVO();
            del.setId(e.getValue().getId());
            del.setEntityTypeCode(POINT_ENTITY_TYPE);
            del.setForceDelete(false);
            entityService.delete(del);
            deleted++;
        }

        log.info("[PathStationPointSync] facilityId={} created={} updated={} deleted={}",
                facilityId, created, updated, deleted);
        return PathStationPointSyncRespDTO.builder()
                .created(created)
                .updated(updated)
                .deleted(deleted)
                .build();
    }

    /**
     * 一期点位类型写死三种；非法或空值默认 STATION（兼容旧 Feign 未传 nodeType）。
     */
    public static String normalizePointKind(String raw) {
        if (StrUtil.isBlank(raw)) {
            return "STATION";
        }
        String kind = raw.trim().toUpperCase(Locale.ROOT);
        if (!ALLOWED_POINT_KINDS.contains(kind)) {
            throw new ServiceException(400, "不支持的点位类型（一期仅 STATION/TRAVERSAL/DOOR）：" + raw);
        }
        return kind;
    }

    /**
     * 归属网：空 → 三网全开（兼容旧数据）；过滤非法码；去重保序。
     */
    public static List<String> normalizeMemberships(List<String> raw) {
        if (raw == null || raw.isEmpty()) {
            return List.copyOf(DEFAULT_MEMBERSHIPS);
        }
        List<String> out = new ArrayList<>();
        for (String item : raw) {
            if (StrUtil.isBlank(item)) {
                continue;
            }
            String m = item.trim().toUpperCase(Locale.ROOT);
            if (!ALLOWED_MEMBERSHIPS.contains(m)) {
                throw new ServiceException(400,
                        "不支持的归属网（仅 HUMAN/GROUND_ROBOT/UAV）：" + item);
            }
            if (!out.contains(m)) {
                out.add(m);
            }
        }
        if (out.isEmpty()) {
            return List.copyOf(DEFAULT_MEMBERSHIPS);
        }
        return List.copyOf(out);
    }

    /**
     * 同步点位业务编码：PN-{facilityId}-{sanitizedNodeId}，便于按设施对账与软删。
     */
    public static String buildPointCode(long facilityId, String nodeId) {
        String safe = nodeId == null ? "" : nodeId.trim().replaceAll("[^A-Za-z0-9_-]", "_");
        if (safe.length() > 64) {
            safe = safe.substring(0, 64);
        }
        String code = "PN-" + facilityId + "-" + safe;
        if (code.length() > 100) {
            code = code.substring(0, 100);
        }
        return code;
    }

    public static boolean isSyncedPointCode(long facilityId, String code) {
        if (StrUtil.isBlank(code)) {
            return false;
        }
        return code.startsWith("PN-" + facilityId + "-");
    }

    /**
     * 列出本设施已同步点位：facility_id = 设施 + 编码前缀 PN-{facilityId}-。
     * 不按型号过滤——存量可能仍在标准点位型号，upsert 时迁入路网型号。
     */
    private Map<String, EntityDO> indexSyncedPoints(long facilityId) {
        EntityRepository.EntityQuery query = EntityRepository.EntityQuery.builder()
                .entityTypeCode(POINT_ENTITY_TYPE)
                .physicalFilters(List.of(
                        new PhysicalColumnFilter(FacilityOwningFieldCodes.FIELD_CODE, "EQ", facilityId)))
                .build();
        List<EntityDO> rows = entityRepository.findAll(query);
        Map<String, EntityDO> byCode = new HashMap<>();
        if (CollectionUtils.isEmpty(rows)) {
            return byCode;
        }
        for (EntityDO row : rows) {
            if (row == null || StrUtil.isBlank(row.getCode())) {
                continue;
            }
            if (!isSyncedPointCode(facilityId, row.getCode())) {
                continue;
            }
            byCode.put(row.getCode(), row);
        }
        return byCode;
    }

    private void createPoint(Long modelId, Long facilityId, String code, String name,
                             String nodeId, String pointKind, List<String> memberships) {
        EntityCreateReqVO create = new EntityCreateReqVO();
        create.setBaseFields(buildBaseFields(modelId, facilityId, code, name));
        create.setCustomFields(buildCustomFields(facilityId, nodeId, pointKind, memberships));
        entityService.create(create);
    }

    private void updatePoint(EntityDO existing, Long modelId, Long facilityId,
                             String code, String name, String nodeId, String pointKind,
                             List<String> memberships) {
        EntityUpdateReqVO update = new EntityUpdateReqVO();
        update.setId(existing.getId());
        update.setBaseFields(buildBaseFields(modelId, facilityId, code, name));
        Map<String, Object> custom = new LinkedHashMap<>();
        if (existing.getCustomFields() != null) {
            custom.putAll(existing.getCustomFields());
        }
        custom.putAll(buildCustomFields(facilityId, nodeId, pointKind, memberships));
        update.setCustomFields(custom);
        entityService.update(update);
    }

    private static Map<String, Object> buildBaseFields(
            Long modelId, Long facilityId, String code, String name) {
        Map<String, Object> base = new LinkedHashMap<>();
        base.put("entityTypeCode", POINT_ENTITY_TYPE);
        base.put("modelId", modelId);
        base.put("name", name);
        base.put("code", code);
        base.put("status", 1);
        base.put(FacilityOwningFieldCodes.FIELD_CODE, FacilityOwningFieldCodes.toApiRef(facilityId));
        return base;
    }

    /**
     * 型号 custom：设施 + 路网节点 id + 点位类型 + 归属网。
     * FLD-PNT-001 与 base facility_id 同指设施，但校验层分开查，须都写。
     */
    private static Map<String, Object> buildCustomFields(
            long facilityId, String nodeId, String pointKind, List<String> memberships) {
        Map<String, Object> custom = new LinkedHashMap<>();
        custom.put(FACILITY_FIELD_SEED_CODE, FacilityOwningFieldCodes.toApiRef(facilityId));
        custom.put(PATH_NODE_FIELD_SEMANTIC, nodeId);
        custom.put(PATH_NODE_FIELD_SEED_CODE, nodeId);
        custom.put(POINT_KIND_FIELD_SEMANTIC, pointKind);
        custom.put(POINT_KIND_FIELD_SEED_CODE, pointKind);
        custom.put(MEMBERSHIP_FIELD_SEMANTIC, memberships);
        custom.put(MEMBERSHIP_FIELD_SEED_CODE, memberships);
        return custom;
    }
}
