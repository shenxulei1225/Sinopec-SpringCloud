package cn.cheers.x.module.dynamicbusiness.service.directory;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.module.dynamicbusiness.api.directory.dto.DepartmentDTO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entity.EntityDO;
import cn.cheers.x.module.dynamicbusiness.dal.repository.entity.EntityRepository;
import cn.cheers.x.module.dynamicbusiness.dal.repository.entity.PhysicalColumnFilter;
import cn.cheers.x.module.dynamicbusiness.service.entity.EntityDedicatedColumnService;
import cn.cheers.x.module.dynamicbusiness.service.entity.core.EntityCoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * 部门目录强类型查询。
 *
 * <p>权威来源：{@code department} 实体、{@code leader_user_id} 固定列和
 * {@code user_account.department_id} 固定列。</p>
 * <p>不负责修改组织数据。禁止访问系统部门/系统用户，也禁止从 custom_fields 或其它字段补值。</p>
 */
@Service
@RequiredArgsConstructor
public class DepartmentDirectoryQueryService {

    static final String DEPARTMENT_TYPE = "department";
    static final String USER_ACCOUNT_TYPE = "user_account";
    static final String LEADER_USER_ID = "leader_user_id";
    static final String DEPARTMENT_ID = "department_id";
    private static final int ENABLED = 1;

    private final EntityCoreService entityCoreService;
    private final EntityRepository entityRepository;
    private final EntityDedicatedColumnService dedicatedColumnService;

    /** 按 ID 读取部门。不存在时显式报错，不回落系统部门。 */
    public DepartmentDTO getDepartment(Long id) {
        return listDepartmentsByIds(List.of(requireId(id, "部门编号不能为空"))).get(0);
    }

    /**
     * 一次加载指定部门的核心列与固定列。
     *
     * <p>返回顺序跟去重后的入参一致。任一部门缺失时阻断，禁止静默丢行。</p>
     */
    public List<DepartmentDTO> listDepartmentsByIds(List<Long> ids) {
        List<Long> requested = normalizeIds(ids);
        if (requested.isEmpty()) {
            return List.of();
        }
        List<EntityDO> rows = entityCoreService.listByIdsWithDedicatedBaseFields(
                requested, DEPARTMENT_TYPE, false);
        requireAllPresent(requested, rows, "部门不存在：");
        return rows.stream().map(DepartmentDirectoryQueryService::toDepartment).toList();
    }

    /** 校验部门存在且启用。停用时显式报错，不猜默认状态。 */
    public void validateEnabled(Long id) {
        DepartmentDTO department = getDepartment(id);
        if (!Objects.equals(ENABLED, department.getStatus())) {
            throw new ServiceException(400, "部门已停用：" + id);
        }
    }

    /**
     * 读取含自身的部门子树闭包。
     *
     * <p>树节点一次加载，命中行再一次加载固定列。禁止逐层或逐部门查询。</p>
     */
    public List<DepartmentDTO> listSubtree(Long id) {
        Long rootId = requireId(id, "部门编号不能为空");
        List<EntityDO> treeRows = entityCoreService.listTreeEntities(DEPARTMENT_TYPE, null);
        EntityDO root = treeRows.stream()
                .filter(row -> Objects.equals(rootId, row.getId()))
                .findFirst()
                .orElseThrow(() -> new ServiceException(404, "部门不存在：" + rootId));
        if (root.getTreePath() == null || root.getTreePath().isBlank()) {
            throw new ServiceException(500, "部门树路径缺失：" + rootId);
        }
        List<Long> subtreeIds = treeRows.stream()
                .filter(row -> row != null && row.getId() != null)
                .filter(row -> Objects.equals(rootId, row.getId())
                        || (row.getTreePath() != null && row.getTreePath().startsWith(root.getTreePath())))
                .map(EntityDO::getId)
                .toList();
        return listDepartmentsByIds(subtreeIds);
    }

    /** 读取部门负责人用户目录 ID。未配置负责人时返回 null。 */
    public Long getLeaderUserAccountId(Long departmentId) {
        return getDepartment(departmentId).getLeaderUserId();
    }

    /**
     * 按部门分组批量读取用户目录 ID。
     *
     * <p>先批量校验部门，再用 {@code user_account.department_id} 做库内 IN 筛选，
     * 最后一次加载命中用户。禁止逐部门查询和系统用户兜底。</p>
     */
    public Map<Long, List<Long>> listUserAccountIdsByDepartmentIds(List<Long> departmentIds) {
        List<Long> requested = normalizeIds(departmentIds);
        if (requested.isEmpty()) {
            return Map.of();
        }
        listDepartmentsByIds(requested);
        String column = dedicatedColumnService.resolvePhysicalColumn(USER_ACCOUNT_TYPE, DEPARTMENT_ID);
        if (column == null) {
            throw new ServiceException(500, "用户目录缺少所属部门基础字段");
        }
        List<Long> allUserIds = entityCoreService.listEntities(USER_ACCOUNT_TYPE, null, null).stream()
                .map(EntityDO::getId)
                .filter(Objects::nonNull)
                .toList();
        List<Long> matchedIds = entityRepository.retainOrderedIdsByPhysicalFilters(
                allUserIds,
                USER_ACCOUNT_TYPE,
                List.of(new PhysicalColumnFilter(column, "IN", requested)));
        List<EntityDO> users = entityCoreService.listByIdsWithDedicatedBaseFields(
                matchedIds, USER_ACCOUNT_TYPE, false);

        Map<Long, List<Long>> grouped = new LinkedHashMap<>();
        requested.forEach(departmentId -> grouped.put(departmentId, new ArrayList<>()));
        for (EntityDO user : users) {
            Long departmentId = physicalLong(user, DEPARTMENT_ID);
            List<Long> ids = grouped.get(departmentId);
            if (ids != null && user.getId() != null) {
                ids.add(user.getId());
            }
        }
        Map<Long, List<Long>> result = new LinkedHashMap<>();
        grouped.forEach((departmentId, userIds) -> result.put(departmentId, List.copyOf(userIds)));
        return Collections.unmodifiableMap(result);
    }

    private static DepartmentDTO toDepartment(EntityDO row) {
        DepartmentDTO dto = new DepartmentDTO();
        dto.setId(row.getId());
        dto.setCode(row.getCode());
        dto.setName(row.getName());
        dto.setParentId(row.getParentId());
        dto.setStatus(row.getStatus());
        dto.setLeaderUserId(physicalLong(row, LEADER_USER_ID));
        return dto;
    }

    private static Long physicalLong(EntityDO row, String fieldCode) {
        if (row == null || row.getDedicatedBaseFieldValues() == null) {
            return null;
        }
        Object value = row.getDedicatedBaseFieldValues().get(fieldCode);
        if (value instanceof Number number) {
            return number.longValue();
        }
        if (value == null) {
            return null;
        }
        try {
            return Long.valueOf(String.valueOf(value).trim());
        } catch (NumberFormatException ex) {
            throw new ServiceException(500, "基础字段不是有效编号：" + fieldCode);
        }
    }

    private static List<Long> normalizeIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        Set<Long> unique = new LinkedHashSet<>();
        for (Long id : ids) {
            unique.add(requireId(id, "编号列表不能包含空值"));
        }
        return List.copyOf(unique);
    }

    private static Long requireId(Long id, String message) {
        if (id == null || id <= 0) {
            throw new ServiceException(400, message);
        }
        return id;
    }

    private static void requireAllPresent(List<Long> requested, List<EntityDO> rows, String messagePrefix) {
        Set<Long> loaded = new LinkedHashSet<>();
        if (rows != null) {
            rows.stream().map(EntityDO::getId).filter(Objects::nonNull).forEach(loaded::add);
        }
        List<Long> missing = requested.stream().filter(id -> !loaded.contains(id)).toList();
        if (!missing.isEmpty()) {
            throw new ServiceException(404, messagePrefix + missing);
        }
    }
}
