package cn.cheers.x.module.dynamicbusiness.service.directory;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.framework.common.pojo.PageParam;
import cn.cheers.x.framework.common.pojo.PageResult;
import cn.cheers.x.module.dynamicbusiness.api.directory.dto.UserAccountDTO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entity.EntityDO;
import cn.cheers.x.module.dynamicbusiness.dal.repository.entity.EntityRepository;
import cn.cheers.x.module.dynamicbusiness.dal.repository.entity.PhysicalColumnFilter;
import cn.cheers.x.module.dynamicbusiness.service.entity.EntityDedicatedColumnService;
import cn.cheers.x.module.dynamicbusiness.service.entity.core.EntityCoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * 用户目录强类型查询。
 *
 * <p>账号和昵称分别来自 {@code user_account} 的核心列 code/name。
 * 手机和所属部门只读固定列 mobile/department_id。</p>
 * <p>不负责修改用户。禁止读取 custom_fields 补值，也禁止访问系统用户表。</p>
 */
@Service
@RequiredArgsConstructor
public class UserAccountDirectoryQueryService {

    static final String USER_ACCOUNT_TYPE = "user_account";
    static final String MOBILE = "mobile";
    static final String DEPARTMENT_ID = "department_id";
    static final String VISIT_TENANT_IDS = "visit_tenant_ids";

    private final EntityCoreService entityCoreService;
    private final EntityRepository entityRepository;
    private final EntityDedicatedColumnService dedicatedColumnService;

    /** 按用户目录 ID 读取用户。不存在时显式报错。 */
    public UserAccountDTO getUserAccount(Long id) {
        if (id == null || id <= 0) {
            throw new ServiceException(400, "用户编号不能为空");
        }
        return listUserAccountsByIds(List.of(id)).get(0);
    }

    /**
     * 一次加载指定用户的核心列与固定列。
     *
     * <p>返回顺序跟去重后的入参一致。任一用户缺失时阻断，禁止静默丢行。</p>
     */
    public List<UserAccountDTO> listUserAccountsByIds(List<Long> ids) {
        List<Long> requested = normalizeIds(ids);
        if (requested.isEmpty()) {
            return List.of();
        }
        List<EntityDO> rows = entityCoreService.listByIdsWithDedicatedBaseFields(
                requested, USER_ACCOUNT_TYPE, false);
        requireAllPresent(requested, rows);
        return rows.stream().map(UserAccountDirectoryQueryService::toUserAccount).toList();
    }

    /** 按租户内唯一账号读取用户。不存在时显式报错，不查询系统用户。 */
    public UserAccountDTO getUserAccountByAccount(String account) {
        String normalized = account == null ? "" : account.trim();
        if (normalized.isEmpty()) {
            throw new ServiceException(400, "账号不能为空");
        }
        List<EntityDO> matched = entityCoreService.listByCodes(List.of(normalized), USER_ACCOUNT_TYPE);
        if (matched == null || matched.isEmpty()) {
            throw new ServiceException(404, "用户目录账号不存在：" + normalized);
        }
        if (matched.size() > 1) {
            throw new ServiceException(500, "用户目录账号不唯一：" + normalized);
        }
        return listUserAccountsByIds(List.of(matched.get(0).getId())).get(0);
    }

    /**
     * 按手机在用户目录物理列筛选。数据库一次筛选，命中后一次批量装配。
     * 不扫描系统用户，也不把空手机当成登录名。
     */
    public UserAccountDTO getUserAccountByMobile(String mobile) {
        String normalized = mobile == null ? "" : mobile.trim();
        if (normalized.isEmpty()) {
            throw new ServiceException(400, "手机不能为空");
        }
        String column = dedicatedColumnService.resolvePhysicalColumn(USER_ACCOUNT_TYPE, MOBILE);
        if (column == null) {
            throw new ServiceException(500, "用户目录缺少手机基础字段");
        }
        List<Long> allIds = entityCoreService.listEntities(USER_ACCOUNT_TYPE, null, null).stream()
                .map(EntityDO::getId)
                .filter(Objects::nonNull)
                .toList();
        List<Long> matchedIds = entityRepository.retainOrderedIdsByPhysicalFilters(
                allIds, USER_ACCOUNT_TYPE,
                List.of(new PhysicalColumnFilter(column, "EQ", normalized)));
        if (matchedIds.isEmpty()) {
            throw new ServiceException(404, "用户目录手机不存在：" + normalized);
        }
        if (matchedIds.size() > 1) {
            throw new ServiceException(500, "用户目录手机不唯一：" + normalized);
        }
        return listUserAccountsByIds(matchedIds).get(0);
    }

    /**
     * 按昵称在用户目录名称列筛选。
     * 昵称为空时返回当前租户的全部用户目录记录。数据库一次筛选，再一次装配固定列。
     */
    public List<UserAccountDTO> listUserAccountsByNickname(String nickname) {
        String normalized = nickname == null ? "" : nickname.trim();
        PageResult<EntityDO> page = entityCoreService.pageEntities(
                USER_ACCOUNT_TYPE, null, null,
                normalized.isEmpty() ? null : normalized,
                1, PageParam.PAGE_SIZE_NONE);
        if (page == null || page.getList() == null || page.getList().isEmpty()) {
            return List.of();
        }
        List<Long> ids = page.getList().stream()
                .map(EntityDO::getId)
                .filter(Objects::nonNull)
                .toList();
        return listUserAccountsByIds(ids);
    }

    private static UserAccountDTO toUserAccount(EntityDO row) {
        UserAccountDTO dto = new UserAccountDTO();
        dto.setId(row.getId());
        dto.setAccount(row.getCode());
        dto.setNickname(row.getName());
        dto.setMobile(physicalText(row, MOBILE));
        dto.setStatus(row.getStatus());
        dto.setDepartmentId(physicalLong(row, DEPARTMENT_ID));
        dto.setVisitTenantIds(parseIds(physicalText(row, VISIT_TENANT_IDS)));
        return dto;
    }

    private static List<Long> parseIds(String text) {
        if (text == null || text.isBlank()) {
            return List.of();
        }
        List<Long> ids = new ArrayList<>();
        for (String part : text.split(",")) {
            String value = part.trim();
            if (value.isEmpty()) {
                continue;
            }
            try {
                ids.add(Long.valueOf(value));
            } catch (NumberFormatException ex) {
                throw new ServiceException(500, "可进入的业务租户不是有效编号：" + value);
            }
        }
        return List.copyOf(ids);
    }

    private static String physicalText(EntityDO row, String fieldCode) {
        Object value = physicalValue(row, fieldCode);
        return value == null ? null : String.valueOf(value);
    }

    private static Long physicalLong(EntityDO row, String fieldCode) {
        Object value = physicalValue(row, fieldCode);
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

    private static Object physicalValue(EntityDO row, String fieldCode) {
        if (row == null || row.getDedicatedBaseFieldValues() == null) {
            return null;
        }
        return row.getDedicatedBaseFieldValues().get(fieldCode);
    }

    private static List<Long> normalizeIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        Set<Long> unique = new LinkedHashSet<>();
        for (Long id : ids) {
            if (id == null || id <= 0) {
                throw new ServiceException(400, "用户编号列表不能包含空值");
            }
            unique.add(id);
        }
        return List.copyOf(unique);
    }

    private static void requireAllPresent(List<Long> requested, List<EntityDO> rows) {
        Set<Long> loaded = new LinkedHashSet<>();
        if (rows != null) {
            rows.stream().map(EntityDO::getId).filter(Objects::nonNull).forEach(loaded::add);
        }
        List<Long> missing = requested.stream().filter(id -> !loaded.contains(id)).toList();
        if (!missing.isEmpty()) {
            throw new ServiceException(404, "用户目录记录不存在：" + missing);
        }
    }
}
