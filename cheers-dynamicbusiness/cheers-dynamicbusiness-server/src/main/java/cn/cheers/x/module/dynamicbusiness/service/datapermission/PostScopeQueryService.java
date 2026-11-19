package cn.cheers.x.module.dynamicbusiness.service.datapermission;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.datapermission.DutyPostRoleDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entitytype.EntityTypeDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.cataloggrant.CatalogRoleGrantMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.datapermission.DutyPostRoleMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.datapermission.ManagementMethodMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.entitytype.EntityTypeMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 「能看的数据」：某个组织里任某个职务的人，能看到哪个范围、哪些目录、能做哪些操作。
 * 组织架构页的职务分组、数据资源管理「人员的数据范围」每条任职展开时用。
 *
 * <p>和登录后真正执行的规则是同一套（{@link DataScopeQueryService}）：目录要登记在这个组织类型的可管目录里，
 * 并且职务绑定的角色有这本目录的实体授权；范围是这个组织和它的下级，各本目录按归属声明逐跳换算。
 * 结果与是谁任职无关，同一组织同一职务的人看到的一样。</p>
 *
 * 不负责：写任职（{@link PostAssignmentService}）；登录人此刻的放行判断（{@link DataScopeQueryService}）。
 * 只读不写，缺登记、缺职务时报错，不补数据。
 */
@Service
public class PostScopeQueryService {

    private static final String ENTITY = "entity";
    /** 页面上按这个顺序显示：查看、新增、修改、删除。 */
    private static final List<String> OPERATIONS = List.of("query", "create", "update", "delete");
    /** 范围里一本目录最多返回这么多条名称，其余只给条数。集团一级能覆盖上千座站。 */
    private static final int MAX_RECORDS_PER_CATALOG = 100;

    @Resource
    private DataScopeConditionResolver conditionResolver;
    @Resource
    private ManagementUnitCatalogQueryService managementUnitCatalogQueryService;
    @Resource
    private CatalogOwnershipQueryService catalogOwnershipQueryService;
    @Resource
    private ManagementMethodMapper managementMethodMapper;
    @Resource
    private CatalogRoleGrantMapper catalogRoleGrantMapper;
    @Resource
    private DutyPostRoleMapper dutyPostRoleMapper;
    @Resource
    private EntityTypeMapper entityTypeMapper;

    /**
     * 范围里某本目录的记录，例如「场站：港尾分输站、漳州分输站… 共 12 座」。
     *
     * @param total   范围里启用的记录总数
     * @param records 前 {@value #MAX_RECORDS_PER_CATALOG} 条，按名称排
     */
    public record ScopeCoverage(String registryCode, String catalogName, int total, List<ScopeRecord> records) {
    }

    /** 能看的一本目录，以及职务绑定的角色在它上面能做哪些操作（query/create/update/delete）。 */
    public record CatalogAccess(String registryCode, String name, String groupName, List<String> operations) {
    }

    /** 这类组织能管的一本目录。「改权限」弹窗只列这些：别的目录勾了授权，在这里任职也不生效。 */
    public record ManageableCatalog(String registryCode, String name, String groupName) {
    }

    /**
     * @param manageableCatalogs 这类组织能管的全部目录（可管目录），不论职务有没有授权
     * @param coverage       范围：第一项是组织本身和下级，其后按「可选的管理方式」逐本列出范围里的记录；
     *                       只列这个职务能看的目录
     * @param scopedCatalogs 按组织划分的目录，只看范围里的记录
     * @param openCatalogs   不按组织划分的目录（检查项这类共用资料），看全部记录
     */
    public record PostScope(Long managementUnitId, String managementUnitName, String unitTypeName,
                            Long dutyPostId, String dutyPostName, List<ManageableCatalog> manageableCatalogs,
                            List<ScopeCoverage> coverage,
                            List<CatalogAccess> scopedCatalogs, List<CatalogAccess> openCatalogs) {
    }

    public PostScope describe(Long managementUnitId, Long dutyPostId) {
        String unitCatalog = managementUnitCatalogQueryService.findManagementUnitRegistryCode();
        if (unitCatalog == null) {
            throw new ServiceException(400, "还没有在数据资源管理里登记组织类型");
        }
        if (managementUnitId == null || dutyPostId == null) {
            throw new ServiceException(400, "请选择组织和职务");
        }
        ManagementUnitCatalogQueryService.ManagementUnitType unitType = requireUnitType(unitCatalog, managementUnitId);
        if (!conditionResolver.recordExists(PostAssignmentService.DUTY_POST_CATALOG, dutyPostId)) {
            throw new ServiceException(400, "职务不存在：" + dutyPostId);
        }
        Map<String, Map<Long, String>> names = conditionResolver.selectNamesByCatalog(List.of(
                Map.entry(unitCatalog, managementUnitId),
                Map.entry(PostAssignmentService.DUTY_POST_CATALOG, dutyPostId)));
        Set<Long> roleIds = rolesOf(dutyPostId);
        List<CatalogAccess> scoped = new ArrayList<>();
        List<CatalogAccess> open = new ArrayList<>();
        List<ManageableCatalog> manageable = new ArrayList<>();
        for (String code : unitType.registryCodes()) {
            EntityTypeDO type = entityTypeMapper.selectByCode(code);
            manageable.add(new ManageableCatalog(code, type == null ? null : type.getName(),
                    type == null ? null : type.getGroupName()));
            List<String> operations = operationsOf(roleIds, code);
            if (operations.isEmpty()) {
                continue;
            }
            CatalogAccess access = new CatalogAccess(code, type == null ? null : type.getName(),
                    type == null ? null : type.getGroupName(), operations);
            (catalogOwnershipQueryService.isRestricted(code) ? scoped : open).add(access);
        }
        return new PostScope(managementUnitId, names.getOrDefault(unitCatalog, Map.of()).get(managementUnitId),
                unitType.modelName(), dutyPostId,
                names.getOrDefault(PostAssignmentService.DUTY_POST_CATALOG, Map.of()).get(dutyPostId), manageable,
                coverage(unitCatalog, managementUnitId, scoped), scoped, open);
    }

    /** 组织必须在管理单元目录里，类型已登记；否则这条任职本来就不给任何范围。 */
    private ManagementUnitCatalogQueryService.ManagementUnitType requireUnitType(String unitCatalog, Long unitId) {
        Long modelId = conditionResolver.selectModelIds(unitCatalog, List.of(unitId)).get(unitId);
        if (modelId == null) {
            throw new ServiceException(400, "组织不存在：#" + unitId);
        }
        return managementUnitCatalogQueryService.getRegistration().types().stream()
                .filter(type -> type.modelId().equals(modelId))
                .findFirst()
                .orElseThrow(() -> new ServiceException(400, "这个组织的类型没有登记为参与数据权限的组织类型"));
    }

    /**
     * 第一项总是组织本身和下级，说明范围从哪里起算；
     * 其后按可选的管理方式的顺序，只列这个职务能看的那几本目录。
     */
    private List<ScopeCoverage> coverage(String unitCatalog, Long unitId, List<CatalogAccess> scoped) {
        Set<String> visible = new LinkedHashSet<>();
        scoped.forEach(access -> visible.add(access.registryCode()));
        List<String> codes = new ArrayList<>();
        codes.add(unitCatalog);
        for (String method : managementMethodMapper.selectRegistryCodesOrdered()) {
            if (!method.equals(unitCatalog) && visible.contains(method)) {
                codes.add(method);
            }
        }
        ScopeNode start = new ScopeNode(unitCatalog, unitId, true);
        List<ScopeCoverage> out = new ArrayList<>();
        for (String code : codes) {
            List<DataScopeCondition> conditions = conditionResolver.conditionsFor(code, start);
            if (conditions.isEmpty()) {
                continue;
            }
            List<ScopeRecord> records = conditionResolver.selectEnabledRecords(code,
                    conditionResolver.selectIds(code, conditions, null));
            EntityTypeDO type = entityTypeMapper.selectByCode(code);
            out.add(new ScopeCoverage(code, type == null ? null : type.getName(), records.size(),
                    records.subList(0, Math.min(records.size(), MAX_RECORDS_PER_CATALOG))));
        }
        return out;
    }

    private Set<Long> rolesOf(Long dutyPostId) {
        Set<Long> roleIds = new LinkedHashSet<>();
        for (DutyPostRoleDO binding : dutyPostRoleMapper.selectListByDutyPostIds(List.of(dutyPostId))) {
            roleIds.add(binding.getRoleId());
        }
        return roleIds;
    }

    private List<String> operationsOf(Set<Long> roleIds, String registryCode) {
        if (roleIds.isEmpty()) {
            return List.of();
        }
        return OPERATIONS.stream()
                .filter(operation -> catalogRoleGrantMapper.existsGrant(roleIds, registryCode, ENTITY, operation))
                .toList();
    }
}
