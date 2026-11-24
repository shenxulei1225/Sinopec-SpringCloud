package cn.cheers.x.module.dynamicbusiness.service.organization;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.module.dynamicbusiness.controller.admin.organization.vo.OrganizationDepartmentRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.organization.vo.OrganizationTreeNodeRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.organization.vo.SolePlaceDepartmentsRespVO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entity.EntityDO;
import cn.cheers.x.module.dynamicbusiness.dal.repository.entity.EntityRepository;
import cn.cheers.x.module.dynamicbusiness.service.department.DepartmentOrganizationCodes;
import cn.cheers.x.module.dynamicbusiness.service.entity.core.EntityCoreService;
import jakarta.annotation.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * 组织树只读。展开收起由组织架构页记住，这里不按层级分页。
 * 组织架构页的树固定读组织架构这本数据目录。管理单元登记选了哪本目录，不改变这一栏。
 * 部门按部门上的「所属组织」列出。
 * 场站部门按场站上的「所属组织」带出（单选引用，权威是场站表上的专用列）。
 */
@Service
public class OrganizationTreeQueryServiceImpl implements OrganizationTreeQueryService {

    private static final String FACILITY_CATALOG = "facility";
    private static final Pattern SAFE_TABLE = Pattern.compile("^[a-z][a-z0-9_]*$");
    private static final Comparator<EntityDO> BY_SORT_THEN_ID = Comparator
            .comparing(EntityDO::getSort, Comparator.nullsLast(Comparator.naturalOrder()))
            .thenComparing(EntityDO::getId);

    @Resource
    private EntityCoreService entityCoreService;

    @Resource
    private EntityRepository entityRepository;

    @Resource
    private JdbcTemplate jdbcTemplate;

    @Override
    public List<OrganizationTreeNodeRespVO> getTree() {
        return listParentChild(DepartmentOrganizationCodes.TARGET);
    }

    @Override
    public List<OrganizationTreeNodeRespVO> listParentChild(String registryCode) {
        if (registryCode == null || registryCode.isBlank()) {
            return List.of();
        }
        List<OrganizationNodeRow> rows = entityCoreService
                .listEntities(registryCode.trim(), null, null)
                .stream()
                .sorted(BY_SORT_THEN_ID)
                .map(row -> new OrganizationNodeRow(row.getId(), row.getName(), row.getParentId(), row.getModelId()))
                .toList();
        return OrganizationTreeComposer.compose(registryCode.trim(), rows);
    }

    @Override
    public List<OrganizationDepartmentRespVO> listDepartments(Long organizationEntityId) {
        if (organizationEntityId == null || organizationEntityId <= 0) {
            throw new ServiceException(400, "所属组织不正确");
        }
        String departmentTable = requireTable(DepartmentOrganizationCodes.ENTITY_TYPE, "部门");
        String facilityTable = requireTable(FACILITY_CATALOG, "场站");
        // 1) 直接挂在这个组织节点上的部门；2) 所属组织是这个节点的场站里的部门（过渡显示，带站名）
        return jdbcTemplate.query(
                "SELECT d.id, d.name, d.parent_id, NULL::bigint AS facility_id, NULL AS facility_name, 0 AS grp, 0::bigint AS fid, d.sort"
                        + " FROM " + departmentTable + " d"
                        + " WHERE d.deleted = false AND d.organization_id = ? AND d.facility_id IS NULL"
                        + " UNION ALL"
                        + " SELECT d.id, d.name, d.parent_id, f.id, f.name, 1, f.id, d.sort"
                        + " FROM " + departmentTable + " d"
                        + " JOIN " + facilityTable + " f ON f.id = d.facility_id AND f.deleted = false"
                        + " WHERE d.deleted = false AND f.organization_id = ?"
                        + " ORDER BY grp, fid, sort NULLS LAST, id",
                (rs, row) -> {
                    OrganizationDepartmentRespVO item = new OrganizationDepartmentRespVO();
                    item.setId(rs.getLong("id"));
                    item.setName(rs.getString("name"));
                    long parentId = rs.getLong("parent_id");
                    item.setParentId(rs.wasNull() ? null : parentId);
                    long facilityId = rs.getLong("facility_id");
                    item.setFacilityId(rs.wasNull() ? null : facilityId);
                    item.setFacilityName(rs.getString("facility_name"));
                    return item;
                },
                organizationEntityId, organizationEntityId);
    }

    @Override
    public SolePlaceDepartmentsRespVO listSolePlaceDepartments() {
        String facilityTable = requireTable(FACILITY_CATALOG, "场站");
        String departmentTable = requireTable(DepartmentOrganizationCodes.ENTITY_TYPE, "部门");
        List<SolePlaceRow> places = jdbcTemplate.query(
                "SELECT id, name FROM " + facilityTable
                        + " WHERE deleted = false AND status = 1 ORDER BY id",
                (rs, row) -> new SolePlaceRow(rs.getLong("id"), rs.getString("name")));
        if (places.size() != 1) {
            return null;
        }
        SolePlaceRow place = places.get(0);
        List<OrganizationDepartmentRespVO> departments = jdbcTemplate.query(
                "SELECT id, name, parent_id FROM " + departmentTable
                        + " WHERE deleted = false AND facility_id = ? ORDER BY sort NULLS LAST, id",
                (rs, row) -> {
                    OrganizationDepartmentRespVO item = new OrganizationDepartmentRespVO();
                    item.setId(rs.getLong("id"));
                    item.setName(rs.getString("name"));
                    long parentId = rs.getLong("parent_id");
                    item.setParentId(rs.wasNull() ? null : parentId);
                    item.setFacilityId(place.id());
                    item.setFacilityName(place.name());
                    return item;
                },
                place.id());
        SolePlaceDepartmentsRespVO result = new SolePlaceDepartmentsRespVO();
        result.setFacilityId(place.id());
        result.setFacilityName(place.name());
        result.setDepartments(departments);
        return result;
    }

    @Override
    public Map<Long, String> displayNames(Collection<Long> departmentEntityIds) {
        List<Long> ids = departmentEntityIds == null
                ? List.of()
                : departmentEntityIds.stream()
                        .filter(id -> id != null && id > 0)
                        .distinct()
                        .toList();
        if (ids.isEmpty()) {
            return Map.of();
        }
        String departmentTable = requireTable(DepartmentOrganizationCodes.ENTITY_TYPE, "部门");
        String placeholders = String.join(",", ids.stream().map(id -> "?").toList());
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                "SELECT d.id, d.name FROM " + departmentTable + " d"
                        + " WHERE d.deleted = false AND d.id IN (" + placeholders + ")",
                ids.toArray());
        Map<Long, String> labels = new LinkedHashMap<>();
        for (Map<String, Object> row : rows) {
            Object rawId = row.get("id");
            if (!(rawId instanceof Number number)) {
                continue;
            }
            String name = row.get("name") == null ? "" : String.valueOf(row.get("name")).trim();
            if (name.isEmpty()) {
                continue;
            }
            labels.put(number.longValue(), name);
        }
        return labels;
    }

    private String requireTable(String entityTypeCode, String label) {
        String table = entityRepository.resolvePhysicalTableName(entityTypeCode);
        if (table == null || !SAFE_TABLE.matcher(table).matches()) {
            throw new ServiceException(400, "无法解析" + label + "表");
        }
        return table;
    }

    private record SolePlaceRow(long id, String name) {
    }
}
