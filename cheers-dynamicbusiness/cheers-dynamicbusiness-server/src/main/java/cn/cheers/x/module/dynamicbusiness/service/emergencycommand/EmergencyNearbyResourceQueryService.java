package cn.cheers.x.module.dynamicbusiness.service.emergencycommand;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.module.dynamicbusiness.controller.admin.category.vo.CategoryTreeRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.emergencycommand.vo.EmergencyNearbyResourceRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityRespVO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entity.EntityDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.model.ModelDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.model.ModelMapper;
import cn.cheers.x.module.dynamicbusiness.service.category.CategoryService;
import cn.cheers.x.module.dynamicbusiness.service.datapermission.DataScopeQueryService;
import cn.cheers.x.module.dynamicbusiness.framework.facility.FacilityOwningFieldCodes;
import cn.cheers.x.module.dynamicbusiness.service.entity.EntityService;
import cn.cheers.x.module.dynamicbusiness.service.entity.core.EntityCoreService;
import cn.cheers.x.module.dynamicbusiness.service.entity.relation.EntityCategoryRelationService;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 指挥页右上角名单：以事件中心为圆心，按直线距离从近到远列出一本目录。
 *
 * 管什么：
 * - 只打开应急资源、外部救援资源、应急队伍这三本。一次只列其中一本。
 * - 外部救援按医院、消防、公安归组。挂了分类用分类名，没挂分类时用型号名归到这三组。
 * - 能看到这起事件的人，就能看到它周边全部记录。不按所属场站或登录人的数据范围收窄。
 * - 半径为空表示不限：有位置的按距离排在前面，没填位置的排在最后，距离为空。
 *   给了半径时只留半径内有位置的资源。
 * - 挂在场站、自己没写坐标的资源，用这座场站的坐标算距离。标明放在仓库的，只用自己的坐标。
 * - 预案写了资源种类时，名称或分类对得上的标成需要。没写种类时不标。
 *
 * 不管什么：不写任何数据。事件没有中心点时直接拒绝。仓库没有坐标时不拿场站坐标顶上。
 */
@Service
public class EmergencyNearbyResourceQueryService {

    private static final double EARTH_RADIUS_KM = 6371.0088;

    @Resource
    private DataScopeQueryService dataScopeQueryService;
    @Resource
    private EntityService entityService;
    @Resource
    private EntityCoreService entityCoreService;
    @Resource
    private CategoryService categoryService;
    @Resource
    private EntityCategoryRelationService entityCategoryRelationService;
    @Resource
    private ModelMapper modelMapper;

    /**
     * @param eventId 应急事件编号；登录人看不到这起事件时 403
     * @param radiusKm 半径（公里）；为空表示不限
     * @param catalog 应急资源、外部救援资源或应急队伍；空着打开应急资源
     * @return 资源行，从近到远；没有位置的排最后（只在不限半径时出现）
     */
    public List<EmergencyNearbyResourceRespVO> listNearby(String eventId, Double radiusKm, String catalog) {
        if (radiusKm != null && radiusKm <= 0) {
            throw new ServiceException(400, "半径必须大于 0");
        }
        String catalogCode = requireNearbyCatalog(catalog);
        EventAnchor anchor = requireEventAnchor(eventId);
        Map<Long, double[]> facilityPoints = new HashMap<>();
        Map<Long, String> kindById = kindByResourceId(catalogCode);
        Map<Long, String> modelNames = EmergencyCommandFields.EXTERNAL_CATALOG.equals(catalogCode)
                ? modelNames(catalogCode) : Map.of();
        List<Long> ids = entityCoreService.listEntities(catalogCode, null, null)
                .stream().map(EntityDO::getId).toList();
        List<EmergencyNearbyResourceRespVO> rows = new ArrayList<>();
        for (EntityDO resource : entityCoreService.listByIdsWithDedicatedBaseFields(
                ids, catalogCode)) {
            String kind = kindById.get(resource.getId());
            if (EmergencyCommandFields.EXTERNAL_CATALOG.equals(catalogCode)) {
                kind = externalGroup(kind, modelNames.get(resource.getModelId()));
            }
            EmergencyNearbyResourceRespVO row = toRow(resource, kind, anchor, facilityPoints);
            if (radiusKm == null || (row.getDistanceKm() != null && row.getDistanceKm() <= radiusKm)) {
                rows.add(row);
            }
        }
        rows.sort(Comparator
                .comparing(EmergencyNearbyResourceRespVO::getDistanceKm, Comparator.nullsLast(Comparator.naturalOrder()))
                .thenComparing(EmergencyNearbyResourceRespVO::getName, Comparator.nullsLast(Comparator.naturalOrder())));
        return rows;
    }

    private record EventAnchor(double[] point, List<String> kinds) {
    }

    private EventAnchor requireEventAnchor(String eventId) {
        Long id = parseEventId(eventId);
        dataScopeQueryService.assertVisible(EmergencyCommandFields.EVENT_CATALOG, id);
        EntityRespVO event = entityService.get(id, EmergencyCommandFields.EVENT_CATALOG);
        if (event == null) {
            throw new ServiceException(404, "应急事件不存在");
        }
        Map<String, Object> base = event.getBaseFields() == null ? Map.of() : event.getBaseFields();
        Double longitude = number(base.get(EmergencyCommandFields.LONGITUDE));
        Double latitude = number(base.get(EmergencyCommandFields.LATITUDE));
        if (longitude == null || latitude == null) {
            throw new ServiceException(400, "这起事件还没有位置，请先补上事件中心点");
        }
        return new EventAnchor(new double[]{longitude, latitude}, kindsOf(event));
    }

    /** 事件已经选定的预案上写下的资源种类。还没选预案、或预案没写种类时为空。 */
    private List<String> kindsOf(EntityRespVO event) {
        Map<String, Object> custom = event.getCustomFields() == null ? Map.of() : event.getCustomFields();
        Map<String, Object> base = event.getBaseFields() == null ? Map.of() : event.getBaseFields();
        Object planRaw = custom.get(EmergencyCommandFields.PLAN_ID);
        if (planRaw == null) {
            planRaw = base.get(EmergencyCommandFields.PLAN_ID);
        }
        if (planRaw == null || String.valueOf(planRaw).isBlank()) {
            return List.of();
        }
        long planId;
        try {
            planId = Long.parseLong(String.valueOf(planRaw).trim());
        } catch (NumberFormatException ex) {
            return List.of();
        }
        EntityRespVO plan = entityService.get(planId, EmergencyCommandFields.PLAN_CATALOG);
        if (plan == null) {
            return List.of();
        }
        Object raw = plan.getCustomFields() == null ? null : plan.getCustomFields().get(EmergencyCommandFields.RESOURCE_KINDS);
        if (raw == null && plan.getBaseFields() != null) {
            raw = plan.getBaseFields().get(EmergencyCommandFields.RESOURCE_KINDS);
        }
        if (raw instanceof String text && !text.isBlank()) {
            raw = JSON.parse(text);
        }
        List<String> kinds = new ArrayList<>();
        if (raw instanceof JSONArray array) {
            for (int i = 0; i < array.size(); i++) {
                Object item = array.get(i);
                if (item != null && !String.valueOf(item).isBlank()) {
                    kinds.add(String.valueOf(item).trim());
                }
            }
        } else if (raw instanceof List<?> list) {
            for (Object item : list) {
                if (item != null && !String.valueOf(item).isBlank()) {
                    kinds.add(String.valueOf(item).trim());
                }
            }
        }
        return kinds;
    }

    /** 只允许右上角工具栏里的三本目录。 */
    private static String requireNearbyCatalog(String catalog) {
        String code = StringUtils.hasText(catalog) ? catalog.trim() : EmergencyCommandFields.RESOURCE_CATALOG;
        if (EmergencyCommandFields.RESOURCE_CATALOG.equals(code)
                || EmergencyCommandFields.TEAM_CATALOG.equals(code)
                || EmergencyCommandFields.EXTERNAL_CATALOG.equals(code)) {
            return code;
        }
        throw new ServiceException(400, "周边名单只打开应急资源、外部救援资源或应急队伍");
    }

    /** 外部救援归到医院、消防、公安。分类优先，型号名里带这几个字时也能归上。对不上就空着。 */
    static String externalGroup(String categoryKind, String modelName) {
        String fromCategory = matchExternalGroup(categoryKind);
        if (fromCategory != null) {
            return fromCategory;
        }
        return matchExternalGroup(modelName);
    }

    private static String matchExternalGroup(String raw) {
        if (!StringUtils.hasText(raw)) {
            return null;
        }
        String name = raw.trim();
        if (name.contains("医院")) {
            return "医院";
        }
        if (name.contains("消防")) {
            return "消防";
        }
        if (name.contains("公安") || name.contains("派出所")) {
            return "公安";
        }
        return null;
    }

    private Map<Long, String> modelNames(String catalogCode) {
        Map<Long, String> names = new HashMap<>();
        for (ModelDO model : modelMapper.selectByEntityTypeCode(catalogCode)) {
            if (model.getId() != null && StringUtils.hasText(model.getName())) {
                names.put(model.getId(), model.getName().trim());
            }
        }
        return names;
    }

    /** 分类名取这本目录分类树上的节点。没挂分类时为空，页面不写「未归类」。 */
    private Map<Long, String> kindByResourceId(String catalogCode) {
        Map<Long, String> kindById = new HashMap<>();
        List<CategoryTreeRespVO> roots = categoryService.getCategoryTreeByType(catalogCode, 1);
        for (CategoryTreeRespVO root : roots == null ? List.<CategoryTreeRespVO>of() : roots) {
            collectKinds(catalogCode, root.getChildren(), kindById);
        }
        return kindById;
    }

    private void collectKinds(String catalogCode, List<CategoryTreeRespVO> nodes, Map<Long, String> kindById) {
        if (nodes == null) {
            return;
        }
        for (CategoryTreeRespVO node : nodes) {
            if (node.getId() != null && StringUtils.hasText(node.getName())) {
                for (Long resourceId : entityCategoryRelationService.listEntityIdsByCategoryIdOnly(
                        node.getId(), catalogCode)) {
                    kindById.putIfAbsent(resourceId, node.getName().trim());
                }
            }
            collectKinds(catalogCode, node.getChildren(), kindById);
        }
    }

    private EmergencyNearbyResourceRespVO toRow(EntityDO resource, String kind, EventAnchor anchor,
                                                Map<Long, double[]> facilityPoints) {
        Map<String, Object> base = resource.getDedicatedBaseFieldValues() == null
                ? Map.of() : resource.getDedicatedBaseFieldValues();
        Map<String, Object> custom = resource.getCustomFields() == null ? Map.of() : resource.getCustomFields();
        EmergencyNearbyResourceRespVO row = new EmergencyNearbyResourceRespVO();
        row.setId(resource.getId());
        row.setName(resource.getName());
        row.setKind(kind);
        row.setAvailableQuantity(number(base.get(EmergencyCommandFields.RESOURCE_AVAILABLE)));
        row.setTotalQuantity(number(base.get(EmergencyCommandFields.RESOURCE_TOTAL)));
        boolean warehouse = EmergencyCommandFields.STORAGE_WAREHOUSE.equals(text(custom.get(EmergencyCommandFields.STORAGE_PLACE)));
        fillLocation(row, base, custom, anchor.point());
        if (warehouse) {
            row.setPlace("仓库");
        } else {
            Long facilityId = FacilityOwningFieldCodes.extractId(
                    base.get(FacilityOwningFieldCodes.FIELD_CODE) != null
                            ? base.get(FacilityOwningFieldCodes.FIELD_CODE)
                            : custom.get(FacilityOwningFieldCodes.FIELD_CODE));
            if (facilityId != null) {
                row.setPlace("场站");
                if (row.getDistanceKm() == null) {
                    double[] point = facilityPoint(facilityId, facilityPoints);
                    if (point != null) {
                        row.setLongitude(point[0]);
                        row.setLatitude(point[1]);
                        row.setDistanceKm(straightLineKm(anchor.point()[0], anchor.point()[1], point[0], point[1]));
                    }
                }
            }
        }
        row.setNeeded(matchesKind(row.getName(), kind, anchor.kinds()));
        return row;
    }

    /** 场站自己的坐标。这场站没有坐标时记空，后面的资源不再查一次。 */
    private double[] facilityPoint(Long facilityId, Map<Long, double[]> cache) {
        if (cache.containsKey(facilityId)) {
            return cache.get(facilityId);
        }
        EntityRespVO facility = entityService.get(facilityId, FacilityOwningFieldCodes.TARGET_ENTITY_TYPE);
        double[] point = null;
        if (facility != null && facility.getBaseFields() != null) {
            Double longitude = number(facility.getBaseFields().get(EmergencyCommandFields.LONGITUDE));
            Double latitude = number(facility.getBaseFields().get(EmergencyCommandFields.LATITUDE));
            if (longitude != null && latitude != null) {
                point = new double[]{longitude, latitude};
            }
        }
        cache.put(facilityId, point);
        return point;
    }

    /** 预案没写种类时不标。名称或分类里出现这个种类才标。 */
    private static boolean matchesKind(String name, String kind, List<String> kinds) {
        if (kinds == null || kinds.isEmpty()) {
            return false;
        }
        for (String item : kinds) {
            if (item.isEmpty()) {
                continue;
            }
            if (kind != null && kind.contains(item)) {
                return true;
            }
            if (name != null && name.contains(item)) {
                return true;
            }
        }
        return false;
    }

    private static String text(Object raw) {
        return raw == null ? "" : String.valueOf(raw).trim();
    }

    /** 先认坐标点，没有再认经度纬度。两处都空着就不算距离。 */
    private static void fillLocation(EmergencyNearbyResourceRespVO row, Map<String, Object> base,
                                     Map<String, Object> custom, double[] center) {
        if (fillFromPoint(row, base.get(EmergencyCommandFields.RESOURCE_COORDINATE), center)) {
            return;
        }
        if (fillFromPoint(row, custom.get(EmergencyCommandFields.RESOURCE_COORDINATE), center)) {
            return;
        }
        Double longitude = firstNumber(base.get(EmergencyCommandFields.LONGITUDE), custom.get(EmergencyCommandFields.LONGITUDE));
        Double latitude = firstNumber(base.get(EmergencyCommandFields.LATITUDE), custom.get(EmergencyCommandFields.LATITUDE));
        if (longitude == null || latitude == null) {
            return;
        }
        row.setLongitude(longitude);
        row.setLatitude(latitude);
        row.setDistanceKm(straightLineKm(center[0], center[1], longitude, latitude));
    }

    private static boolean fillFromPoint(EmergencyNearbyResourceRespVO row, Object raw, double[] center) {
        if (!(raw instanceof Map<?, ?> point)) {
            return false;
        }
        Double longitude = number(point.get("longitude"));
        Double latitude = number(point.get("latitude"));
        if (longitude == null || latitude == null) {
            return false;
        }
        row.setLongitude(longitude);
        row.setLatitude(latitude);
        row.setDistanceKm(straightLineKm(center[0], center[1], longitude, latitude));
        return true;
    }

    private static Double firstNumber(Object primary, Object fallback) {
        Double value = number(primary);
        return value != null ? value : number(fallback);
    }

    /** 球面直线距离（公里），保留两位小数。 */
    static double straightLineKm(double lng1, double lat1, double lng2, double lat2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double km = 2 * EARTH_RADIUS_KM * Math.asin(Math.min(1, Math.sqrt(a)));
        return Math.round(km * 100) / 100.0;
    }

    private static Long parseEventId(String raw) {
        try {
            return Long.valueOf(raw.trim());
        } catch (RuntimeException ex) {
            throw new ServiceException(400, "事件编号不正确");
        }
    }

    private static Double number(Object raw) {
        if (raw instanceof Number value) {
            return value.doubleValue();
        }
        if (raw instanceof String value && StringUtils.hasText(value)) {
            try {
                return Double.valueOf(value.trim());
            } catch (NumberFormatException ex) {
                return null;
            }
        }
        return null;
    }
}
