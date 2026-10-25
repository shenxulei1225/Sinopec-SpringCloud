package cn.cheers.x.module.dynamicbusiness.service.organization;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.module.dynamicbusiness.controller.admin.category.vo.CategoryTreeRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.organization.vo.OrganizationDepartmentRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.organization.vo.OrganizationTreeNodeRespVO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.category.CategoryCategoryRelationDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.category.CategoryCategoryRelationMapper;
import cn.cheers.x.module.dynamicbusiness.dal.repository.entity.EntityRepository;
import cn.cheers.x.module.dynamicbusiness.framework.tenant.TenantAssociationTableService;
import cn.cheers.x.module.dynamicbusiness.service.category.CategoryService;
import cn.cheers.x.module.dynamicbusiness.service.department.DepartmentCompanyCodes;
import cn.cheers.x.module.dynamicbusiness.service.department.DepartmentCompanyFieldEnsureService;
import jakarta.annotation.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

/**
 * 组织树只读。展开收起由组织架构页记住，这里不按层级分页。
 */
@Service
public class OrganizationTreeQueryServiceImpl implements OrganizationTreeQueryService {

    private static final String REGION = "region";
    private static final String FACILITY = "facility";
    private static final int ENABLED = 1;
    private static final java.util.regex.Pattern SAFE_TABLE =
            java.util.regex.Pattern.compile("^[a-z][a-z0-9_]*$");

    @Resource
    private CategoryService categoryService;

    @Resource
    private CategoryCategoryRelationMapper relationMapper;

    @Resource
    private TenantAssociationTableService tenantAssociationTableService;

    @Resource
    private DepartmentCompanyFieldEnsureService departmentCompanyFieldEnsureService;

    @Resource
    private EntityRepository entityRepository;

    @Resource
    private JdbcTemplate jdbcTemplate;

    @Override
    public List<OrganizationTreeNodeRespVO> getTree() {
        List<CategoryTreeRespVO> regionTree = categoryService.getCategoryTreeByType(REGION, ENABLED);
        List<CategoryTreeRespVO> facilityTree = categoryService.getCategoryTreeByType(FACILITY, ENABLED);
        tenantAssociationTableService.ensureCurrentTenantAssociationTables();
        List<CategoryCategoryRelationDO> relations = relationMapper.selectByTypePair(REGION, FACILITY);
        Map<Long, Long> regionByStation = singleHostByStation(relations);
        return OrganizationTreeComposer.compose(regionTree, placements(facilityTree, regionByStation));
    }

    @Override
    public List<OrganizationDepartmentRespVO> listCompanyDepartments(Long companyEntityId) {
        if (companyEntityId == null || companyEntityId <= 0) {
            throw new ServiceException(400, "所属公司不正确");
        }
        departmentCompanyFieldEnsureService.ensure();
        String table = entityRepository.resolvePhysicalTableName(DepartmentCompanyCodes.ENTITY_TYPE);
        if (table == null || !SAFE_TABLE.matcher(table).matches()) {
            throw new ServiceException(400, "无法解析部门表");
        }
        Boolean present = jdbcTemplate.queryForObject(
                "SELECT EXISTS (SELECT 1 FROM information_schema.columns "
                        + "WHERE table_schema = current_schema() AND table_name = ? AND column_name = 'company_id')",
                Boolean.class, table);
        if (!Boolean.TRUE.equals(present)) {
            return List.of();
        }
        return jdbcTemplate.query(
                "SELECT id, name FROM " + table
                        + " WHERE deleted = false AND company_id = ? AND facility_id IS NULL ORDER BY id",
                (rs, row) -> {
                    OrganizationDepartmentRespVO item = new OrganizationDepartmentRespVO();
                    item.setId(rs.getLong("id"));
                    item.setName(rs.getString("name"));
                    return item;
                },
                companyEntityId);
    }

    /** 同一座站挂了不止一个区域时，不放进结果，避免替它挑一个。 */
    static Map<Long, Long> singleHostByStation(List<CategoryCategoryRelationDO> relations) {
        Map<Long, LinkedHashSet<Long>> hosts = new LinkedHashMap<>();
        if (relations == null) {
            return Map.of();
        }
        for (CategoryCategoryRelationDO row : relations) {
            if (row == null || row.getMemberCategoryId() == null || row.getHostCategoryId() == null) {
                continue;
            }
            hosts.computeIfAbsent(row.getMemberCategoryId(), id -> new LinkedHashSet<>())
                    .add(row.getHostCategoryId());
        }
        Map<Long, Long> single = new LinkedHashMap<>();
        for (Map.Entry<Long, LinkedHashSet<Long>> entry : hosts.entrySet()) {
            Long hostId = OrganizationTreeComposer.singleRegionHost(entry.getValue());
            if (hostId != null) {
                single.put(entry.getKey(), hostId);
            }
        }
        return single;
    }

    private static List<OrganizationStationPlacement> placements(List<CategoryTreeRespVO> facilityTree,
                                                                 Map<Long, Long> regionByStation) {
        List<OrganizationStationPlacement> stations = new ArrayList<>();
        collectStations(facilityTree, regionByStation, stations);
        return stations;
    }

    private static void collectStations(List<CategoryTreeRespVO> nodes,
                                        Map<Long, Long> regionByStation,
                                        List<OrganizationStationPlacement> stations) {
        if (nodes == null) {
            return;
        }
        for (CategoryTreeRespVO node : nodes) {
            if (node == null) {
                continue;
            }
            if (Boolean.TRUE.equals(node.getIsEntity()) && node.getId() != null && node.getEntityId() != null) {
                stations.add(new OrganizationStationPlacement(
                        node.getId(),
                        node.getName(),
                        node.getEntityId(),
                        regionByStation.get(node.getId())));
            }
            collectStations(node.getChildren(), regionByStation, stations);
        }
    }
}
