package cn.cheers.x.module.dynamicbusiness.service.cataloggrant;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.framework.security.core.util.SecurityFrameworkUtils;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.FieldFilterReqVO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.cataloggrant.CatalogRoleStationScopeDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entity.EntityDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entitytype.EntityTypeDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.field.FieldDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.model.ModelDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.model.ModelFieldAssignmentDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.cataloggrant.CatalogRoleGrantMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.cataloggrant.CatalogRoleStationScopeMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.entitytype.EntityTypeMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.field.FieldMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.model.ModelFieldAssignmentMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.model.ModelMapper;
import cn.cheers.x.module.dynamicbusiness.framework.facility.FacilityOwningFieldCodes;
import cn.cheers.x.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.cheers.x.system.api.permission.PermissionApi;
import cn.cheers.x.system.api.permission.RoleApi;
import cn.cheers.x.system.enums.permission.RoleCodeEnum;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 数据目录的场站范围。
 * 负责：按角色保存查看范围和写入范围；登录人打开数据管理时，按他挂的角色收窄列表和保存。
 * 不负责：把人分成全局或场站。不读人员记录上的所属场站来判断角色。不改角色表上的系统部门数据范围。
 * 禁止：按某个数据目录编码写死分支。哪个目录要限制，只看这一张范围表。
 */
@Service
public class CatalogStationScopeService {

    public static final String UNRESTRICTED = "UNRESTRICTED";
    public static final String ALL = "ALL";
    public static final String CURRENT = "CURRENT";
    /** 右上角当前站。只在范围是仅当前站时读取。不写入查询条件。 */
    public static final String CURRENT_FACILITY_HEADER = "X-Current-Facility-Id";
    private static final String FACILITY_PROVIDER = "dynamic-entity:facility";
    private static final String WORK_SCOPE_FACILITY = "FACILITY";
    private static final Set<String> SCOPES = Set.of(UNRESTRICTED, ALL, CURRENT);

    @Resource
    private CatalogRoleStationScopeMapper stationScopeMapper;
    @Resource
    private CatalogRoleGrantMapper catalogRoleGrantMapper;
    @Resource
    private EntityTypeMapper entityTypeMapper;
    @Resource
    private ModelMapper modelMapper;
    @Resource
    private ModelFieldAssignmentMapper modelFieldAssignmentMapper;
    @Resource
    private FieldMapper fieldMapper;
    @Resource
    private PermissionApi permissionApi;
    @Resource
    private RoleApi roleApi;

    public List<CatalogStationScope> listByRoleId(Long roleId) {
        List<CatalogStationScope> rows = new ArrayList<>();
        for (CatalogRoleStationScopeDO row : stationScopeMapper.selectListByRoleId(roleId)) {
            if (!StringUtils.hasText(row.getRegistryCode()) || !SCOPES.contains(row.getViewScope())
                    || !SCOPES.contains(row.getWriteScope())) {
                continue;
            }
            rows.add(new CatalogStationScope(row.getRegistryCode().trim(), row.getViewScope(), row.getWriteScope()));
        }
        return rows;
    }

    /**
     * 整份替换这个角色的场站范围。
     * 两边都是全部场站时不落行。没有行时按全部场站读。
     */
    @Transactional(rollbackFor = Exception.class)
    public void replaceRoleScopes(Long roleId, List<CatalogStationScope> scopes) {
        if (roleId == null) {
            throw new ServiceException(400, "请选择角色");
        }
        stationScopeMapper.deleteByRoleId(roleId);
        if (scopes == null) {
            return;
        }
        for (CatalogStationScope scope : scopes) {
            if (scope == null || !StringUtils.hasText(scope.registryCode())) {
                throw new ServiceException(400, "请选择数据目录");
            }
            String viewScope = normalizeScope(scope.viewScope());
            String writeScope = normalizeScope(scope.writeScope());
            if (ALL.equals(viewScope) && ALL.equals(writeScope)) {
                continue;
            }
            String registryCode = scope.registryCode().trim();
            if (entityTypeMapper.selectByCode(registryCode) == null) {
                throw new ServiceException(400, "数据目录不存在：" + registryCode);
            }
            CatalogRoleStationScopeDO row = new CatalogRoleStationScopeDO();
            row.setRoleId(roleId);
            row.setRegistryCode(registryCode);
            row.setViewScope(viewScope);
            row.setWriteScope(writeScope);
            stationScopeMapper.insert(row);
        }
    }

    /**
     * 当前登录人在这个数据目录上能看、能写到哪一座站，以及四项操作开没开。
     * 没登录、目录为空：四项都关。
     * 超级管理员：四项都开，范围是全部场站。
     */
    public CatalogStationScopeDecision resolveForLoginUser(String registryCode) {
        return resolve(SecurityFrameworkUtils.getLoginUserId(), registryCode);
    }

    public CatalogStationScopeDecision resolve(Long userId, String registryCode) {
        if (userId == null || !StringUtils.hasText(registryCode)) {
            return closed();
        }
        String code = registryCode.trim();
        Boolean elevated = permissionApi.hasAnyRoles(userId, RoleCodeEnum.SUPER_ADMIN.getCode()).getCheckedData();
        if (Boolean.TRUE.equals(elevated)) {
            return new CatalogStationScopeDecision(ALL, ALL, true, true, true, true);
        }
        Collection<Long> roleIds = roleApi.getUserRoleIdListByUserId(userId).getCheckedData();
        if (roleIds == null || roleIds.isEmpty()) {
            return closed();
        }
        boolean canQuery = catalogRoleGrantMapper.existsGrant(roleIds, code, "entity", "query");
        boolean canCreate = catalogRoleGrantMapper.existsGrant(roleIds, code, "entity", "create");
        boolean canUpdate = catalogRoleGrantMapper.existsGrant(roleIds, code, "entity", "update");
        boolean canDelete = catalogRoleGrantMapper.existsGrant(roleIds, code, "entity", "delete");
        Map<Long, CatalogRoleStationScopeDO> byRole = new LinkedHashMap<>();
        for (CatalogRoleStationScopeDO row : stationScopeMapper.selectListByRoleIdsAndRegistry(roleIds, code)) {
            if (row.getRoleId() != null) {
                byRole.put(row.getRoleId(), row);
            }
        }
        String viewScope = null;
        String writeScope = null;
        for (Long roleId : roleIds) {
            CatalogRoleStationScopeDO row = byRole.get(roleId);
            String view = row == null ? ALL : normalizeScope(row.getViewScope());
            String write = row == null ? ALL : normalizeScope(row.getWriteScope());
            if (catalogRoleGrantMapper.existsGrant(List.of(roleId), code, "entity", "query")) {
                viewScope = viewScope == null ? view : wider(viewScope, view);
            }
            if (catalogRoleGrantMapper.existsGrant(List.of(roleId), code, "entity", "create")
                    || catalogRoleGrantMapper.existsGrant(List.of(roleId), code, "entity", "update")
                    || catalogRoleGrantMapper.existsGrant(List.of(roleId), code, "entity", "delete")) {
                writeScope = writeScope == null ? write : wider(writeScope, write);
            }
        }
        return new CatalogStationScopeDecision(
                viewScope == null ? ALL : viewScope,
                writeScope == null ? ALL : writeScope,
                canQuery, canCreate, canUpdate, canDelete);
    }

    /**
     * 查看范围是仅当前站时，列表只留右上角当前站。
     * 已经带上的其它筛选保留。指向场站的筛选改成当前站。
     * 查看范围是全部场站时，保留请求里已经写上的场站条件（页面上点中的那一座站）。
     * 没有这道条件时，不改用右上角当前站。
     */
    public List<FieldFilterReqVO> constrainListFilters(String registryCode, List<FieldFilterReqVO> filters) {
        if (!StringUtils.hasText(registryCode)) {
            return filters;
        }
        CatalogStationScopeDecision decision = resolveForLoginUser(registryCode);
        if (ALL.equals(decision.viewScope())) {
            return filters;
        }
        if (!CURRENT.equals(decision.viewScope())) {
            return filters;
        }
        List<String> fieldCodes = facilityFieldCodes(registryCode.trim());
        if (fieldCodes.isEmpty()) {
            throw new ServiceException(400, "这个目录的查看范围是仅当前站，但没有所属场站字段");
        }
        Long current = requiredCurrentFacilityId();
        List<FieldFilterReqVO> next = new ArrayList<>();
        if (filters != null) {
            for (FieldFilterReqVO filter : filters) {
                if (filter == null || !fieldCodes.contains(filter.getFieldCode())) {
                    next.add(filter);
                }
            }
        }
        for (String fieldCode : fieldCodes) {
            FieldFilterReqVO filter = new FieldFilterReqVO();
            filter.setFieldCode(fieldCode);
            filter.setOp("EQ");
            filter.setValue(current);
            next.add(filter);
        }
        return next;
    }

    /**
     * 写入范围是仅当前站时，提交或已保存的所属场站必须等于右上角当前站。
     * 创建没填所属场站则拒绝。修改没改这一项时，核对库里已经保存的值。
     */
    public void assertWriteMatchesCurrentStation(String registryCode, String entityTypeCode,
                                                 Map<String, Object> baseFields, Map<String, Object> customFields,
                                                 EntityDO stored) {
        String catalog = firstText(registryCode, entityTypeCode);
        if (!StringUtils.hasText(catalog)) {
            return;
        }
        if (!CURRENT.equals(resolveForLoginUser(catalog).writeScope())) {
            return;
        }
        String storageCode = firstText(entityTypeCode, catalog);
        List<String> fieldCodes = facilityFieldCodes(storageCode);
        if (fieldCodes.isEmpty()) {
            throw new ServiceException(400, "这个目录的写入范围是仅当前站，但没有所属场站字段");
        }
        Long current = requiredCurrentFacilityId();
        if (mentionsFacility(fieldCodes, baseFields, customFields)) {
            assertFacilityValues(fieldCodes, current, baseFields, customFields);
            return;
        }
        if (stored == null) {
            throw new ServiceException(400, "只能保存当前站的数据，请填写所属场站");
        }
        assertFacilityValues(fieldCodes, current, stored.getDedicatedBaseFieldValues(), stored.getCustomFields());
    }

    /** 查看范围是仅当前站时，详情里的所属场站必须是当前站。 */
    public void assertViewMatchesCurrentStation(String registryCode, EntityDO stored) {
        if (!StringUtils.hasText(registryCode) || stored == null) {
            return;
        }
        if (!CURRENT.equals(resolveForLoginUser(registryCode).viewScope())) {
            return;
        }
        List<String> fieldCodes = facilityFieldCodes(registryCode.trim());
        if (fieldCodes.isEmpty()) {
            throw new ServiceException(400, "这个目录的查看范围是仅当前站，但没有所属场站字段");
        }
        assertFacilityValues(fieldCodes, requiredCurrentFacilityId(),
                stored.getDedicatedBaseFieldValues(), stored.getCustomFields());
    }

    /**
     * 这个数据类型里指向场站的字段。
     * 站场级目录认所属场站列。全网目录认字段库里目标是场站的关联字段。
     */
    public List<String> facilityFieldCodes(String entityTypeCode) {
        if (!StringUtils.hasText(entityTypeCode)) {
            return List.of();
        }
        String code = entityTypeCode.trim();
        Set<String> fieldCodes = new LinkedHashSet<>();
        EntityTypeDO type = entityTypeMapper.selectByCode(code);
        if (type != null && WORK_SCOPE_FACILITY.equalsIgnoreCase(type.getWorkScope())) {
            fieldCodes.add(FacilityOwningFieldCodes.FIELD_CODE);
        }
        List<ModelDO> models = modelMapper.selectByEntityTypeCode(code);
        if (models == null || models.isEmpty()) {
            return List.copyOf(fieldCodes);
        }
        List<Long> modelIds = new ArrayList<>();
        for (ModelDO model : models) {
            if (model.getId() != null) {
                modelIds.add(model.getId());
            }
        }
        List<ModelFieldAssignmentDO> assignments = modelFieldAssignmentMapper.selectByModelIds(modelIds);
        Set<Long> fieldIds = new LinkedHashSet<>();
        for (ModelFieldAssignmentDO assignment : assignments) {
            if (assignment.getFieldId() != null) {
                fieldIds.add(assignment.getFieldId());
            }
        }
        Map<Long, FieldDO> fields = new LinkedHashMap<>();
        if (!fieldIds.isEmpty()) {
            for (FieldDO field : fieldMapper.selectList(new LambdaQueryWrapperX<FieldDO>().in(FieldDO::getId, fieldIds))) {
                fields.put(field.getId(), field);
            }
        }
        for (ModelFieldAssignmentDO assignment : assignments) {
            if (!StringUtils.hasText(assignment.getFieldCode())) {
                continue;
            }
            FieldDO field = fields.get(assignment.getFieldId());
            if (pointsAtFacility(assignment, field)) {
                fieldCodes.add(assignment.getFieldCode().trim());
            }
        }
        return List.copyOf(fieldCodes);
    }

    static String wider(String left, String right) {
        return rank(right) > rank(left) ? right : left;
    }

    private static int rank(String scope) {
        if (ALL.equals(scope)) {
            return 2;
        }
        return 1;
    }

    private static CatalogStationScopeDecision closed() {
        return new CatalogStationScopeDecision(UNRESTRICTED, UNRESTRICTED, false, false, false, false);
    }

    private static String normalizeScope(String scope) {
        if (UNRESTRICTED.equals(scope) || ALL.equals(scope)) {
            return ALL;
        }
        if (CURRENT.equals(scope)) {
            return CURRENT;
        }
        throw new ServiceException(400, "场站范围只允许全部场站、仅当前站");
    }

    private static boolean pointsAtFacility(ModelFieldAssignmentDO assignment, FieldDO field) {
        if (FacilityOwningFieldCodes.TARGET_ENTITY_TYPE.equals(assignment.getTargetEntityType())) {
            return true;
        }
        if (field == null) {
            return FacilityOwningFieldCodes.FIELD_CODE.equals(assignment.getFieldCode());
        }
        if (FACILITY_PROVIDER.equals(field.getProviderCode())) {
            return true;
        }
        return FacilityOwningFieldCodes.FIELD_CODE.equals(field.getCode())
                || FacilityOwningFieldCodes.TARGET_ENTITY_TYPE.equals(field.getSemanticType());
    }

    private static boolean mentionsFacility(List<String> fieldCodes, Map<String, Object> first, Map<String, Object> second) {
        return containsAny(fieldCodes, first) || containsAny(fieldCodes, second);
    }

    private static boolean containsAny(List<String> fieldCodes, Map<String, Object> values) {
        if (values == null) {
            return false;
        }
        for (String fieldCode : fieldCodes) {
            if (values.containsKey(fieldCode)) {
                return true;
            }
        }
        return false;
    }

    private static void assertFacilityValues(List<String> fieldCodes, Long current,
                                             Map<String, Object> first, Map<String, Object> second) {
        boolean foundInFirst = matchMap(fieldCodes, current, first);
        boolean foundInSecond = matchMap(fieldCodes, current, second);
        boolean found = foundInFirst || foundInSecond;
        if (!found) {
            throw new ServiceException(400, "只能处理当前站的数据，这条记录没有所属场站");
        }
    }

    private static boolean matchMap(List<String> fieldCodes, Long current, Map<String, Object> values) {
        if (values == null) {
            return false;
        }
        boolean found = false;
        for (String fieldCode : fieldCodes) {
            if (!values.containsKey(fieldCode)) {
                continue;
            }
            found = true;
            Long id = FacilityOwningFieldCodes.extractId(values.get(fieldCode));
            if (!current.equals(id)) {
                throw new ServiceException(400, "只能处理当前站的数据");
            }
        }
        return found;
    }

    private Long requiredCurrentFacilityId() {
        ServletRequestAttributes attributes = RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes servlet
                ? servlet : null;
        if (attributes == null) {
            throw new ServiceException(400, "请先在右上角选择当前站");
        }
        HttpServletRequest request = attributes.getRequest();
        String raw = request.getHeader(CURRENT_FACILITY_HEADER);
        Long id = FacilityOwningFieldCodes.extractId(raw);
        if (id == null) {
            throw new ServiceException(400, "请先在右上角选择当前站");
        }
        return id;
    }

    private static String firstText(String preferred, String fallback) {
        if (StringUtils.hasText(preferred)) {
            return preferred.trim();
        }
        if (StringUtils.hasText(fallback)) {
            return fallback.trim();
        }
        return null;
    }
}
