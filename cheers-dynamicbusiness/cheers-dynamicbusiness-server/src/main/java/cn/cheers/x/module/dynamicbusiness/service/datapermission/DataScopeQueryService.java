package cn.cheers.x.module.dynamicbusiness.service.datapermission;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.framework.security.core.util.SecurityFrameworkUtils;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.FieldFilterReqVO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.datapermission.CatalogOwnershipDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.datapermission.UserDataScopeDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.cataloggrant.CatalogRoleGrantMapper;
import cn.cheers.x.module.dynamicbusiness.framework.facility.FacilityOwningFieldCodes;
import cn.cheers.x.module.dynamicbusiness.framework.entity.EntityTableNameHandler;
import cn.cheers.x.module.dynamicbusiness.service.department.DepartmentOrganizationCodes;
import cn.cheers.x.module.dynamicbusiness.service.department.DepartmentOwnerRules;
import cn.cheers.x.module.dynamicbusiness.service.cataloggrant.CatalogEntryAccessService;
import cn.cheers.x.module.dynamicbusiness.service.tenantcapability.CatalogBusinessEnablementGate;
import cn.cheers.x.module.dynamicbusiness.service.tenantcapability.CatalogBusinessEnablementGate.ListCut;
import cn.cheers.x.system.api.permission.RoleApi;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 数据范围计算：登录人在某本数据目录上，这次能看、能写哪些记录。
 *
 * <p>规则（权威：docs/中台/能力定义/数据权限能力/数据权限能力（权威）.md 第 2.4、2.6、3 节）：</p>
 * <ul>
 *   <li>目录既不是管理单元目录、也没写归属声明：不按单元分，有功能就能看全部。</li>
 *   <li>否则看用户目录里这个人单独保存的数据范围。登录后按登录账号对上人员再读。
 *       角色（直接分配的，以及担任的职务换成的）在这本目录上有这项操作，
 *       才按每个选中的单元和它的下级换算条件。条与条之间取并集。</li>
 *   <li>没有数据范围，或角色没有这项操作：列表为空，详情和写入拒绝。不按操作者自己的部门猜一个单元。</li>
 *   <li>人员目录不声明归属，列表仍看全部。新建、修改、删除挂了部门的人员时，按这个部门所在的组织或场站核对登录人保存的数据范围。</li>
 *   <li>没有登录人（定时任务、消息消费等系统调用）：不按人员授权核对，读写都不收窄。</li>
 *   <li>右上角选中的站（请求头 {@value #BROWSE_FACILITY_HEADER}）：列表、上级树、导出再收窄到属于这座站的记录。
 *       它当作一行「场站目录 · 这座站 · 不含下级」的范围换算，和授权范围取交集，只缩小不扩大。
 *       到不了场站的目录（管道、标准等）和场站目录本身不受影响。详情和写入不看它。</li>
 * </ul>
 *
 * <p>场景速查：</p>
 * <ul>
 *   <li>列表查询 → {@link #constrainListFilters}</li>
 *   <li>看详情、路径 → {@link #assertVisible} / {@link #assertVisibleInTarget}</li>
 *   <li>导出、上级树 → {@link #retainVisible}</li>
 *   <li>分类树上挂的记录 → {@link #retainCoveredInBrowseFacility}：场站节点列出能看的全部站；能按场站归属的记录再收到右上角选中的站</li>
 *   <li>新建、修改、删除 → {@link #assertWritable}</li>
     *   <li>右上角可选的站 → {@link #listMyFacilities}</li>
     *   <li>数据页左边的管线段 → {@link #listMyPipelineSegments}</li>
     *   <li>应急资源地图判断画面盖住哪段 → {@link #listMapStations}</li>
 * </ul>
 *
 * 列表、详情、写入在数据范围之后，再交给 {@link CatalogBusinessEnablementGate} 核对这场站有没有开通这本目录所属业务。
 * 不负责：功能授权本身（见 {@code CatalogAccess}）；写任何数据；补归属声明或归属字段值。
 */
@Service
public class DataScopeQueryService {

    public static final String QUERY = "query";
    public static final String CREATE = "create";
    public static final String UPDATE = "update";
    public static final String DELETE = "delete";
    /** 右上角当前浏览的那座站。前端只给实体查询带它；应急页查资源这类不跟右上角走的请求不带。 */
    public static final String BROWSE_FACILITY_HEADER = "X-Browse-Facility-Id";
    private static final String ENTITY = "entity";
    private static final String ID = "id";
    /** 人员目录。本身不声明归属，写入时改看它所挂部门的管理单位。 */
    private static final String USER_ACCOUNT = "user_account";
    private static final String DEPARTMENT = "department";
    private static final String ORGANIZATION = "organization";

    @Resource
    private PostAssignmentQueryService postAssignmentQueryService;
    @Resource
    private CatalogOwnershipQueryService catalogOwnershipQueryService;
    @Resource
    private CatalogBusinessEnablementGate catalogBusinessEnablementGate;
    @Resource
    private UserDataScopeQueryService userDataScopeQueryService;
    @Resource
    private RoleApi roleApi;
    @Resource
    private CatalogRoleGrantMapper catalogRoleGrantMapper;
    @Resource
    private DataScopeConditionResolver conditionResolver;
    @Resource
    private CatalogEntryAccessService catalogEntryAccessService;
    @Resource
    private EntityTableNameHandler entityTableNameHandler;
    @Resource
    private JdbcTemplate jdbcTemplate;

    /**
     * 列表筛选加上数据范围，再收窄到右上角选中的站。
     *
     * @return nothingVisible 为 true 时调用方直接返回空页；否则用 filters 继续查
     */
    public ScopedFilters constrainListFilters(String registryCode, List<FieldFilterReqVO> filters) {
        String catalog = requireCatalog(registryCode);
        ScopedFilters scoped = ScopedFilters.of(filters == null ? List.of() : filters);
        if (!isSystemCall() && catalogOwnershipQueryService.isRestricted(catalog)) {
            scoped = narrowList(catalog, conditionsForLoginUser(catalog, QUERY), scoped.filters());
        }
        List<DataScopeCondition> browse = browseConditions(catalog);
        if (!scoped.nothingVisible() && !browse.isEmpty()) {
            scoped = narrowList(catalog, browse, scoped.filters());
        }
        return applyEnablement(catalog, scoped);
    }

    /**
     * 按一组条件（行之间取并集）收窄列表。
     * 所有条件都落在同一个单值归属字段上时，用这一列 IN，并和已有同一列的筛选取交集；
     * 否则先算出符合的记录编号，再加一条「编号在这些里」。
     */
    private ScopedFilters narrowList(String catalog, List<DataScopeCondition> conditions,
                                     List<FieldFilterReqVO> current) {
        if (conditions.isEmpty()) {
            return ScopedFilters.nothing();
        }
        String sharedField = sharedSingleField(conditions);
        if (sharedField != null) {
            ScopedFilters narrowed = narrowField(sharedField, unionAllowed(conditions), current);
            if (narrowed != null) {
                return narrowed;
            }
        }
        List<Long> ids = conditionResolver.selectIds(catalog, conditions, null);
        if (ids.isEmpty()) {
            return ScopedFilters.nothing();
        }
        List<FieldFilterReqVO> next = new ArrayList<>(current);
        next.add(inFilter(ID, ids));
        return ScopedFilters.of(next);
    }

    /** 数据范围收窄之后，再去掉所在场站没开通这项业务的记录。 */
    private ScopedFilters applyEnablement(String catalog, ScopedFilters scoped) {
        if (scoped.nothingVisible() || isSystemCall()) {
            return scoped;
        }
        ListCut cut = catalogBusinessEnablementGate.listCut(catalog);
        if (!cut.apply()) {
            return scoped;
        }
        if (cut.allowedIds().isEmpty()) {
            return ScopedFilters.nothing();
        }
        ScopedFilters narrowed = narrowField(cut.fieldCode(), new LinkedHashSet<>(cut.allowedIds()), scoped.filters());
        if (narrowed != null) {
            return narrowed;
        }
        List<FieldFilterReqVO> next = new ArrayList<>(scoped.filters());
        next.add(inFilter(cut.fieldCode(), cut.allowedIds()));
        return ScopedFilters.of(next);
    }

    /** 看详情：这条记录必须落在登录人的查看范围里，否则 403。 */
    public void assertVisible(String registryCode, Long entityId) {
        String catalog = requireCatalog(registryCode);
        if (entityId == null || isSystemCall()) {
            return;
        }
        if (catalogOwnershipQueryService.isRestricted(catalog) && !recordInScope(catalog, QUERY, entityId)) {
            throw new ServiceException(403, "这条记录不在你的数据范围里");
        }
        catalogBusinessEnablementGate.assertRecord(catalog, entityId);
    }

    /**
     * 数据范围按这次实际读写的目录算：请求类型就是本页或本页底座时按本页，是另一本目录时按那本目录。
     * 没带本页目录的是服务内部读写（如任务执行落记录、复制），按类型自身的目录算。
     */
    public String scopeCatalogOf(String registryCode, String requestedTypeCode) {
        if (!StringUtils.hasText(registryCode)) {
            return requestedTypeCode;
        }
        return catalogEntryAccessService.resolve(registryCode, requestedTypeCode).entry().getRegistryCode();
    }

    /** 按本页目录和请求类型找到实际读的目录，再核对这条记录在查看范围里。 */
    public void assertVisibleInTarget(String registryCode, String requestedTypeCode, Long entityId) {
        assertVisible(scopeCatalogOf(registryCode, requestedTypeCode), entityId);
    }

    /**
     * 一批记录里只留登录人能看的、且属于右上角选中那座站的，保持原顺序。导出、上级树这类一次取整批再输出的入口用。
     * 目录不按单元分、系统调用、没选站或目录到不了场站时，对应那一步不收窄。
     */
    public List<Long> retainVisible(String registryCode, List<Long> ids) {
        String catalog = requireCatalog(registryCode);
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        List<Long> kept = ids;
        if (!isSystemCall() && catalogOwnershipQueryService.isRestricted(catalog)) {
            kept = retainMatching(catalog, conditionsForLoginUser(catalog, QUERY), kept);
        }
        List<DataScopeCondition> browse = browseConditions(catalog);
        if (!browse.isEmpty() && !kept.isEmpty()) {
            kept = retainMatching(catalog, browse, kept);
        }
        return catalogBusinessEnablementGate.retain(catalog, kept);
    }

    private List<Long> retainMatching(String catalog, List<DataScopeCondition> conditions, List<Long> ids) {
        if (conditions.isEmpty()) {
            return List.of();
        }
        Set<Long> matched = new LinkedHashSet<>(conditionResolver.selectIds(catalog, conditions, null));
        return ids.stream().filter(matched::contains).toList();
    }

    /**
     * 分类树上的场站节点：只留登录人任职覆盖到的，保持原顺序。
     * 和右上角可选的站同一口径：只看任职所在单元和可管目录，不看角色功能，也不看右上角选中的站。
     * 目录不按单元分或系统调用时原样返回。
     */
    public List<Long> retainCoveredByPosts(String registryCode, List<Long> ids) {
        String catalog = requireCatalog(registryCode);
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        if (isSystemCall() || !catalogOwnershipQueryService.isRestricted(catalog)) {
            return ids;
        }
        return retainMatching(catalog, conditionsCoveredByPosts(catalog), ids);
    }

    /**
     * 分类树上挂着的记录：先留任职覆盖到的。
     * 这本目录能按场站归属、并且右上角选了站时，再收到这座站。
     * 场站目录本身、到不了场站的目录、没选站：不收成一座站。
     */
    public List<Long> retainCoveredInBrowseFacility(String registryCode, List<Long> ids) {
        List<Long> covered = retainCoveredByPosts(registryCode, ids);
        if (covered.isEmpty()) {
            return covered;
        }
        String catalog = requireCatalog(registryCode);
        List<DataScopeCondition> browse = browseConditions(catalog);
        List<Long> kept = browse.isEmpty() ? covered : retainMatching(catalog, browse, covered);
        return catalogBusinessEnablementGate.retain(catalog, kept);
    }

    /** 这本目录按数据范围分，且这次是登录人在操作。 */
    public boolean restrictsLoginUser(String registryCode) {
        return !isSystemCall() && catalogOwnershipQueryService.isRestricted(requireCatalog(registryCode));
    }

    /**
     * 新建、修改、删除前核对数据范围和归属字段。
     * <ul>
     *   <li>修改、删除：库里这条记录必须在这项操作的范围里。</li>
     *   <li>新建、或修改时提交了归属字段：新值必须在范围里；必填的归属字段不能缺。</li>
     *   <li>人员：按所挂部门的组织或场站核对。不在登录人的数据范围里就拒绝。</li>
     * </ul>
     *
     * @param storedEntityId 修改、删除时库里的记录编号；新建传 null
     */
    public void assertWritable(String registryCode, String operation, Long storedEntityId,
                               Map<String, Object> baseFields, Map<String, Object> customFields) {
        String catalog = requireCatalog(registryCode);
        if (isSystemCall()) {
            return;
        }
        if (USER_ACCOUNT.equals(catalog)) {
            assertPersonDepartmentInScope(operation, storedEntityId, baseFields, customFields);
        }
        if (catalogOwnershipQueryService.isRestricted(catalog)) {
            List<DataScopeCondition> conditions = conditionsForLoginUser(catalog, operation);
            if (conditions.isEmpty()) {
                throw new ServiceException(403, "你在这本数据目录上没有可写的数据范围");
            }
            if (storedEntityId != null
                    && conditionResolver.selectIds(catalog, conditions, storedEntityId).isEmpty()) {
                throw new ServiceException(403, "这条记录不在你的数据范围里");
            }
            if (!DELETE.equals(operation)) {
                assertOwnershipValues(catalog, conditions, storedEntityId, baseFields, customFields);
            }
        }
        if (DELETE.equals(operation)) {
            catalogBusinessEnablementGate.assertRecord(catalog, storedEntityId);
            return;
        }
        catalogBusinessEnablementGate.assertWrite(catalog, storedEntityId, baseFields, customFields);
    }

    /**
     * 人员挂在部门上。部门挂在组织或场站上。
     * 新建、修改、删除这个人时，这个单位必须落在登录人保存的数据范围里（含下级）。
     * 没有挂部门的人员不按单元核对。不按操作者自己的部门猜范围。
     */
    private void assertPersonDepartmentInScope(String operation, Long storedEntityId,
                                                Map<String, Object> baseFields, Map<String, Object> customFields) {
        Long submitted = firstRefId(valueOf(baseFields, customFields, "department_id"));
        Long stored = CREATE.equals(operation) ? null : columnId(USER_ACCOUNT, storedEntityId, "department_id");
        Set<Long> departmentIds = new LinkedHashSet<>();
        if (submitted != null) {
            departmentIds.add(submitted);
        }
        if (stored != null) {
            departmentIds.add(stored);
        }
        for (Long departmentId : departmentIds) {
            assertDepartmentCovered(departmentId);
        }
    }

    /** 这个部门所在的组织或场站，必须在登录人的数据范围里。两处都没挂就拒绝。 */
    private void assertDepartmentCovered(Long departmentId) {
        DepartmentHang hang = departmentHang(departmentId);
        if (hang == null) {
            throw new ServiceException(403, "这个部门不存在");
        }
        if (hang.organizationId() == null && hang.facilityId() == null) {
            throw new ServiceException(403, "这个部门没有挂到管理单位");
        }
        if (hang.organizationId() != null && !unitCovered(ORGANIZATION, hang.organizationId())) {
            throw new ServiceException(403, "这个部门不在你的数据范围里");
        }
        if (hang.facilityId() != null && !unitCovered(FacilityOwningFieldCodes.TARGET_ENTITY_TYPE, hang.facilityId())) {
            throw new ServiceException(403, "这个部门不在你的数据范围里");
        }
    }

    /** 登录人保存的数据范围（含下级）是否盖住这一条组织或场站。不看角色在这本目录上的操作。 */
    private boolean unitCovered(String catalog, Long unitId) {
        List<DataScopeCondition> conditions = conditionsCoveredByPosts(catalog);
        return !conditions.isEmpty() && !conditionResolver.selectIds(catalog, conditions, unitId).isEmpty();
    }

    private DepartmentHang departmentHang(Long departmentId) {
        if (departmentId == null) {
            return null;
        }
        List<DepartmentHang> rows = jdbcTemplate.query(
                "SELECT organization_id, facility_id FROM " + physicalTable(DEPARTMENT)
                        + " WHERE deleted = false AND id = ?",
                (rs, rowNum) -> new DepartmentHang(readLong(rs, 1), readLong(rs, 2)),
                departmentId);
        return rows.isEmpty() ? null : rows.get(0);
    }

    private Long columnId(String registryCode, Long entityId, String column) {
        if (entityId == null || !"department_id".equals(column)) {
            return null;
        }
        List<Long> values = jdbcTemplate.query(
                "SELECT " + column + " FROM " + physicalTable(registryCode) + " WHERE deleted = false AND id = ?",
                (rs, rowNum) -> readLong(rs, 1),
                entityId);
        return values.isEmpty() ? null : values.get(0);
    }

    private String physicalTable(String registryCode) {
        String table = entityTableNameHandler.resolvePhysicalTableName(registryCode);
        if (!StringUtils.hasText(table) || !table.matches("[A-Za-z_][A-Za-z0-9_]*")) {
            throw new ServiceException(403, "无法核对这些人所在部门的管理单位");
        }
        return table;
    }

    private static Long readLong(java.sql.ResultSet rs, int index) throws java.sql.SQLException {
        long value = rs.getLong(index);
        return rs.wasNull() ? null : value;
    }

    /** 部门挂在组织上，或挂在场站上。两列都空表示没挂管理单位。 */
    private record DepartmentHang(Long organizationId, Long facilityId) {
    }

    /**
     * 右上角可选的站：登录人有效任职覆盖到的启用场站，按名称排序。不看角色功能。
     * 一个人能看几座站只由任职决定。没有任职、或单元类型没登记场站目录时返回空列表。
     */
    public List<ScopeRecord> listMyFacilities() {
        String catalog = FacilityOwningFieldCodes.TARGET_ENTITY_TYPE;
        return conditionResolver.selectEnabledRecords(catalog,
                conditionResolver.selectIds(catalog, conditionsCoveredByPosts(catalog), null));
    }

    /**
     * 登录人能看的管线和管线段。
     * 任职范围往往只落到管线段上。这里把这些段的上级管线一并带回，数据页才能按管线目录的上下级嵌成树。
     * 上级停用或不存在就停，不再往上。没有任职时返回空列表。
     */
    public List<PipelineSegmentNode> listMyPipelineSegments() {
        String catalog = "pipeline";
        Set<Long> ids = new LinkedHashSet<>(
                conditionResolver.selectIds(catalog, conditionsCoveredByPosts(catalog), null));
        Set<Long> frontier = new LinkedHashSet<>(ids);
        while (!frontier.isEmpty()) {
            Map<Long, Long> parents = conditionResolver.selectEnabledParentIds(catalog, frontier);
            frontier = new LinkedHashSet<>();
            for (Long parentId : parents.values()) {
                if (parentId != null && parentId > 0 && ids.add(parentId)) {
                    frontier.add(parentId);
                }
            }
        }
        return conditionResolver.selectEnabledTreeRecords(catalog, ids);
    }

    /**
     * 登录人能看的站，带经纬度和所属管线段。
     * 应急资源地图用站是否落在画面里判断盖住了哪段。没有坐标的站不参与判断，仍把编号和名称返回。
     */
    public List<MapStation> listMapStations() {
        String catalog = FacilityOwningFieldCodes.TARGET_ENTITY_TYPE;
        return conditionResolver.selectMapStations(
                conditionResolver.selectIds(catalog, conditionsCoveredByPosts(catalog), null));
    }

    /**
     * 右上角选中的站在这本目录上的条件。
     * 没带请求头、不在接口请求里、或这本目录逐跳到不了场站时返回空列表，表示不收窄。
     * 场站目录本身也不收窄：场站台账列出登录人能看的全部场站，右上角选站只决定看哪座站的下级记录。
     * 请求头不是正整数时报 400，不当作没选站。
     */
    private List<DataScopeCondition> browseConditions(String catalog) {
        Long facilityId = browseFacilityId();
        if (facilityId == null || FacilityOwningFieldCodes.TARGET_ENTITY_TYPE.equals(catalog)) {
            return List.of();
        }
        return conditionResolver.conditionsFor(catalog,
                new ScopeNode(FacilityOwningFieldCodes.TARGET_ENTITY_TYPE, facilityId, false));
    }

    /**
     * 这次请求是否按某一座站浏览台账（数据页左侧场站列或右上角选站框）。
     * 没带请求头时返回 null。
     */
    public Long currentBrowseFacilityId() {
        return browseFacilityId();
    }

    private static Long browseFacilityId() {
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        if (!(attributes instanceof ServletRequestAttributes servlet)) {
            return null;
        }
        HttpServletRequest request = servlet.getRequest();
        String raw = request.getHeader(BROWSE_FACILITY_HEADER);
        if (!StringUtils.hasText(raw)) {
            return null;
        }
        try {
            long id = Long.parseLong(raw.trim());
            if (id > 0) {
                return id;
            }
        } catch (NumberFormatException ignored) {
            // 落到下面统一报错
        }
        throw new ServiceException(400, "右上角选中的站编号不对：" + raw);
    }

    /** 没有登录人：定时任务、消息消费等系统调用，不按人员授权核对。接口请求都有登录人。 */
    private static boolean isSystemCall() {
        return SecurityFrameworkUtils.getLoginUserId() == null;
    }

    private boolean recordInScope(String catalog, String operation, Long entityId) {
        List<DataScopeCondition> conditions = conditionsForLoginUser(catalog, operation);
        return !conditions.isEmpty() && !conditionResolver.selectIds(catalog, conditions, entityId).isEmpty();
    }

    /**
     * 逐条任职：单元类型登记了这本目录，并且职务绑定的角色在这本目录上有这项实体操作，
     * 才按这个单元和它的下级换算条件。
     */
    private List<DataScopeCondition> conditionsForLoginUser(String catalog, String operation) {
        return conditionsOfPosts(catalog, operation);
    }

    /** 逐条任职：单元类型登记了这本目录，就按这个单元和它的下级换算条件。不看角色功能。 */
    private List<DataScopeCondition> conditionsCoveredByPosts(String catalog) {
        return conditionsOfPosts(catalog, null);
    }

    /**
     * 这个人保存的数据范围。有这项操作的角色才换算条件；operation 为空时只看范围，不看角色。
     */
    private List<DataScopeCondition> conditionsOfPosts(String catalog, String operation) {
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        List<UserDataScopeDO> rows = userDataScopeQueryService.listForLoginUser(userId);
        if (rows.isEmpty()) {
            return List.of();
        }
        if (operation != null) {
            Set<Long> roleIds = capabilityRoleIds(userId);
            if (roleIds.isEmpty() || !catalogRoleGrantMapper.existsGrant(roleIds, catalog, ENTITY, operation)) {
                return List.of();
            }
        }
        List<DataScopeCondition> conditions = new ArrayList<>();
        for (UserDataScopeDO row : rows) {
            if (row.getUnitId() == null || !StringUtils.hasText(row.getRegistryCode())) {
                continue;
            }
            conditions.addAll(conditionResolver.conditionsFor(catalog,
                    new ScopeNode(row.getRegistryCode().trim(), row.getUnitId(), true)));
        }
        return conditions;
    }

    /** 直接分配的角色，加上担任的职务和有效任职换成的角色。 */
    private Set<Long> capabilityRoleIds(Long userId) {
        Set<Long> roleIds = new LinkedHashSet<>();
        Collection<Long> direct = roleApi.getUserRoleIdListByUserId(userId).getCheckedData();
        if (direct != null) {
            roleIds.addAll(direct);
        }
        roleIds.addAll(postAssignmentQueryService.listActivePostRoleIds(userId));
        return roleIds;
    }

    /**
     * 提交的归属字段值必须被至少一行条件接受；必填的归属字段新建时不能缺，修改时不能清空。
     * 记录编号上的条件（这本目录就是范围所在目录）只接受修改，新建时由上级判断：
     * 新建的上级必须在范围里，例如只管西三线的人可以在西三线下建管线段。
     *
     * @param storedEntityId 修改时库里的记录编号；新建传 null
     */
    private void assertOwnershipValues(String catalog, List<DataScopeCondition> conditions, Long storedEntityId,
                                       Map<String, Object> baseFields, Map<String, Object> customFields) {
        boolean creating = storedEntityId == null;
        if (creating && DepartmentOrganizationCodes.ENTITY_TYPE.equals(catalog)) {
            Long facilityId = firstRefId(valueOf(baseFields, customFields, FacilityOwningFieldCodes.FIELD_CODE));
            Long organizationId = firstRefId(valueOf(baseFields, customFields, DepartmentOrganizationCodes.FIELD_CODE));
            if (facilityId == null && organizationId == null) {
                throw new ServiceException(400, DepartmentOwnerRules.NEITHER);
            }
        }
        for (CatalogOwnershipDO declaration : catalogOwnershipQueryService.listByRegistryCode(catalog)) {
            String field = declaration.getFieldCode();
            boolean submitted = contains(baseFields, field) || contains(customFields, field);
            boolean empty = refIds(valueOf(baseFields, customFields, field)).isEmpty();
            if (creating && DepartmentOrganizationCodes.ENTITY_TYPE.equals(catalog)
                    && (FacilityOwningFieldCodes.FIELD_CODE.equals(field)
                    || DepartmentOrganizationCodes.FIELD_CODE.equals(field))) {
                continue;
            }
            if (Boolean.TRUE.equals(declaration.getRequired()) && empty && (creating || submitted)) {
                throw new ServiceException(400, "缺少归属字段：" + field);
            }
        }
        for (DataScopeCondition condition : conditions) {
            if (accepts(catalog, condition, storedEntityId, baseFields, customFields)) {
                return;
            }
        }
        throw new ServiceException(403, "提交的归属不在你的数据范围里");
    }

    /**
     * 这一行条件接不接受这次提交的归属值。
     * <ul>
     *   <li>单值归属（所属场站）：新值必须在范围里。</li>
     *   <li>多值归属（场站的所属管线段）：只能增删自己范围里的段，别人管的段原样保留；
     *       保存后至少还挂着一段自己范围里的，否则记录就出了自己的范围。新建时库里没有旧值，等于每段都要在范围里。</li>
     * </ul>
     */
    private boolean accepts(String catalog, DataScopeCondition condition, Long storedEntityId,
                            Map<String, Object> baseFields, Map<String, Object> customFields) {
        boolean creating = storedEntityId == null;
        if (condition.onRecordId()) {
            if (!creating) {
                return true;
            }
            Set<Long> parent = refIds(valueOf(baseFields, customFields, "parentId"));
            if (parent.isEmpty()) {
                parent = refIds(valueOf(baseFields, customFields, "parent_id"));
            }
            return !parent.isEmpty() && condition.allowedIds().containsAll(parent);
        }
        String field = condition.fieldCode();
        if (!creating && !contains(baseFields, field) && !contains(customFields, field)) {
            return true;
        }
        // 归属字段空着的记录不归到这一行的范围里，这一行不接受它
        Set<Long> values = refIds(valueOf(baseFields, customFields, field));
        if (!condition.multiValued()) {
            return !values.isEmpty() && condition.allowedIds().containsAll(values);
        }
        Set<Long> stored = creating ? Set.of()
                : conditionResolver.selectStoredMultiRefIds(catalog, field, storedEntityId);
        Set<Long> changed = new LinkedHashSet<>(values);
        changed.removeAll(stored);
        Set<Long> removed = new LinkedHashSet<>(stored);
        removed.removeAll(values);
        changed.addAll(removed);
        boolean stillMine = values.stream().anyMatch(condition.allowedIds()::contains);
        return stillMine && condition.allowedIds().containsAll(changed);
    }

    /** 收窄成一列 IN；请求里同一列的筛选不是等于或包含时返回 null，改用记录编号收窄。 */
    private static ScopedFilters narrowField(String field, Set<Long> allowed, List<FieldFilterReqVO> filters) {
        Set<Long> narrowed = new LinkedHashSet<>(allowed);
        List<FieldFilterReqVO> rest = new ArrayList<>();
        for (FieldFilterReqVO filter : filters) {
            if (filter == null || !field.equals(filter.getFieldCode())) {
                rest.add(filter);
                continue;
            }
            String op = filter.getOp() == null ? "" : filter.getOp().trim().toUpperCase();
            if (!"EQ".equals(op) && !"IN".equals(op)) {
                return null;
            }
            narrowed.retainAll(refIds(filter.getValue()));
        }
        if (narrowed.isEmpty()) {
            return ScopedFilters.nothing();
        }
        rest.add(inFilter(field, new ArrayList<>(narrowed)));
        return ScopedFilters.of(rest);
    }

    private static String sharedSingleField(List<DataScopeCondition> conditions) {
        String field = null;
        for (DataScopeCondition condition : conditions) {
            if (condition.onRecordId() || condition.multiValued()) {
                return null;
            }
            if (field != null && !field.equals(condition.fieldCode())) {
                return null;
            }
            field = condition.fieldCode();
        }
        return field;
    }

    private static Set<Long> unionAllowed(List<DataScopeCondition> conditions) {
        Set<Long> union = new LinkedHashSet<>();
        for (DataScopeCondition condition : conditions) {
            union.addAll(condition.allowedIds());
        }
        return union;
    }

    private static FieldFilterReqVO inFilter(String field, List<Long> ids) {
        FieldFilterReqVO filter = new FieldFilterReqVO();
        filter.setFieldCode(field);
        filter.setOp("IN");
        filter.setValue(ids);
        return filter;
    }

    private static String requireCatalog(String registryCode) {
        if (!StringUtils.hasText(registryCode)) {
            throw new ServiceException(403, "缺少本页数据目录，无法核对数据范围");
        }
        return registryCode.trim();
    }

    private static boolean contains(Map<String, Object> values, String key) {
        return values != null && values.containsKey(key);
    }

    private static Object valueOf(Map<String, Object> baseFields, Map<String, Object> customFields, String key) {
        if (contains(baseFields, key)) {
            return baseFields.get(key);
        }
        return contains(customFields, key) ? customFields.get(key) : null;
    }

    /** 引用值可能是编号、编号文本、{id: …} 或它们的列表。 */
    static Set<Long> refIds(Object raw) {
        Set<Long> ids = new LinkedHashSet<>();
        if (raw instanceof Collection<?> items) {
            for (Object item : items) {
                ids.addAll(refIds(item));
            }
            return ids;
        }
        Long id = FacilityOwningFieldCodes.extractId(raw);
        if (id != null) {
            ids.add(id);
        }
        return ids;
    }

    private static Long firstRefId(Object raw) {
        for (Long id : refIds(raw)) {
            return id;
        }
        return null;
    }

    /**
     * 列表筛选的结果。
     *
     * @param nothingVisible 登录人在这本目录上什么都看不到，调用方直接返回空页
     * @param filters        收窄后的筛选
     */
    public record ScopedFilters(boolean nothingVisible, List<FieldFilterReqVO> filters) {

        static ScopedFilters of(List<FieldFilterReqVO> filters) {
            return new ScopedFilters(false, filters);
        }

        static ScopedFilters nothing() {
            return new ScopedFilters(true, List.of());
        }
    }
}
