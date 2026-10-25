package cn.cheers.x.module.dynamicbusiness.service.useraccount;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityCreateReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.useraccount.vo.UserAccountCreateReqVO;
import cn.cheers.x.module.dynamicbusiness.framework.facility.FacilityOwningFieldCodes;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * 新建用户时先看输入。空账号、空口令直接拒绝，不编默认值。
 * 场站上的人要有所属场站。平台上的人要有可进入的业务租户。两边只填一边。
 */
public final class UserAccountCreateRules {

    private static final Pattern ACCOUNT = Pattern.compile("^[a-zA-Z0-9]{4,30}$");
    private static final int PASSWORD_MIN = 4;
    private static final int PASSWORD_MAX = 16;

    private UserAccountCreateRules() {
    }

    public static void check(UserAccountCreateReqVO request) {
        if (request == null) {
            throw new ServiceException(400, "新建用户的内容不能为空");
        }
        EntityCreateReqVO create = new EntityCreateReqVO();
        Map<String, Object> baseFields = new LinkedHashMap<>();
        baseFields.put("code", request.getAccount());
        baseFields.put("name", request.getNickname());
        baseFields.put(UserAccountCodes.MOBILE, request.getMobile());
        baseFields.put("status", request.getStatus());
        if (request.getFacilityId() != null) {
            baseFields.put(FacilityOwningFieldCodes.FIELD_CODE, request.getFacilityId());
        }
        if (request.getVisitTenantIds() != null) {
            baseFields.put(UserAccountCodes.VISIT_TENANT_IDS, request.getVisitTenantIds());
        }
        create.setBaseFields(baseFields);
        checkCatalogCreate(create, request.getPassword());
        request.setAccount(text(baseFields.get("code")));
        request.setNickname(text(baseFields.get("name")));
        request.setMobile(text(baseFields.get(UserAccountCodes.MOBILE)));
        request.setVisitTenantIds(readTenants(baseFields.get(UserAccountCodes.VISIT_TENANT_IDS)));
        Long departmentId = request.getDepartmentCategoryId();
        if (departmentId != null && departmentId <= 0) {
            throw new ServiceException(400, "部门不正确");
        }
    }

    /**
     * 数据管理保存用户目录时看这一次提交。口令不在字段里。
     * 所属场站和可进入的业务租户只留一边。
     */
    public static void checkCatalogCreate(EntityCreateReqVO request, String password) {
        if (request == null) {
            throw new ServiceException(400, "新建用户的内容不能为空");
        }
        Map<String, Object> baseFields = request.getBaseFields() == null
                ? new LinkedHashMap<>() : request.getBaseFields();
        Map<String, Object> customFields = request.getCustomFields() == null
                ? new LinkedHashMap<>() : request.getCustomFields();
        request.setBaseFields(baseFields);
        request.setCustomFields(customFields);
        baseFields.remove("password");
        customFields.remove("password");

        String account = trim(text(first(baseFields, customFields, "code")));
        String nickname = trim(text(first(baseFields, customFields, "name")));
        String mobile = trim(text(first(baseFields, customFields, UserAccountCodes.MOBILE)));
        writeBack(baseFields, customFields, "code", account);
        writeBack(baseFields, customFields, "name", nickname);
        writeBack(baseFields, customFields, UserAccountCodes.MOBILE, mobile);

        if (!StringUtils.hasText(account)) {
            throw new ServiceException(400, "账号不能为空");
        }
        if (!ACCOUNT.matcher(account).matches()) {
            throw new ServiceException(400, "账号只能是 4 到 30 位字母或数字");
        }
        if (!StringUtils.hasText(nickname)) {
            throw new ServiceException(400, "昵称不能为空");
        }
        if (nickname.length() > 30) {
            throw new ServiceException(400, "昵称不能超过 30 个字");
        }
        if (!StringUtils.hasText(mobile)) {
            throw new ServiceException(400, "手机不能为空");
        }
        if (mobile.length() > 20) {
            throw new ServiceException(400, "手机不能超过 20 个字");
        }
        checkPassword(password);
        Integer status = readStatus(first(baseFields, customFields, "status"));
        if (status == null) {
            throw new ServiceException(400, "状态不能为空");
        }
        if (status != UserAccountCodes.ENABLED && status != UserAccountCodes.DISABLED) {
            throw new ServiceException(400, "状态只能是启用或停用");
        }
        Long facilityId = readFacilityId(first(baseFields, customFields, FacilityOwningFieldCodes.FIELD_CODE));
        List<Long> tenants = readTenants(first(baseFields, customFields, UserAccountCodes.VISIT_TENANT_IDS));
        boolean hasFacility = facilityId != null;
        boolean hasTenants = !tenants.isEmpty();
        if (hasFacility && hasTenants) {
            throw new ServiceException(400, "所属场站和可进入的业务租户只填一边");
        }
        if (!hasFacility && !hasTenants) {
            throw new ServiceException(400, "要选定所属场站，或记下可以进入的业务租户");
        }
    }

    public static void checkProfile(String nickname, String mobile, Integer status) {
        if (!StringUtils.hasText(nickname)) {
            throw new ServiceException(400, "昵称不能为空");
        }
        if (nickname.length() > 30) {
            throw new ServiceException(400, "昵称不能超过 30 个字");
        }
        if (!StringUtils.hasText(mobile)) {
            throw new ServiceException(400, "手机不能为空");
        }
        if (mobile.length() > 20) {
            throw new ServiceException(400, "手机不能超过 20 个字");
        }
        if (status == null) {
            throw new ServiceException(400, "状态不能为空");
        }
        if (status != UserAccountCodes.ENABLED && status != UserAccountCodes.DISABLED) {
            throw new ServiceException(400, "状态只能是启用或停用");
        }
    }

    public static void checkPassword(String password) {
        if (!StringUtils.hasText(password)) {
            throw new ServiceException(400, "口令不能为空");
        }
        int passwordLength = password.length();
        if (passwordLength < PASSWORD_MIN || passwordLength > PASSWORD_MAX) {
            throw new ServiceException(400, "口令长度要在 4 到 16 位");
        }
    }

    private static Object first(Map<String, Object> baseFields, Map<String, Object> customFields, String key) {
        if (baseFields.containsKey(key) && baseFields.get(key) != null) {
            return baseFields.get(key);
        }
        return customFields.get(key);
    }

    private static void writeBack(
            Map<String, Object> baseFields, Map<String, Object> customFields, String key, String value) {
        if (baseFields.containsKey(key)) {
            baseFields.put(key, value);
            return;
        }
        if (customFields.containsKey(key)) {
            customFields.put(key, value);
        }
    }

    private static Long readFacilityId(Object raw) {
        if (raw == null) {
            return null;
        }
        if (raw instanceof String text && !StringUtils.hasText(text)) {
            return null;
        }
        Long id = readLong(raw instanceof Map<?, ?> map ? map.get("id") : raw);
        if (id == null) {
            return null;
        }
        if (id <= 0) {
            throw new ServiceException(400, "所属场站不正确");
        }
        return id;
    }

    private static List<Long> readTenants(Object raw) {
        if (raw == null) {
            return List.of();
        }
        Set<Long> unique = new LinkedHashSet<>();
        if (raw instanceof Collection<?> items) {
            for (Object item : items) {
                addTenant(unique, item);
            }
            return List.copyOf(unique);
        }
        if (raw instanceof String text) {
            if (!StringUtils.hasText(text)) {
                return List.of();
            }
            for (String part : text.split(",")) {
                if (StringUtils.hasText(part)) {
                    addTenant(unique, part.trim());
                }
            }
            return List.copyOf(unique);
        }
        addTenant(unique, raw);
        return List.copyOf(unique);
    }

    private static void addTenant(Set<Long> unique, Object raw) {
        Long tenantId = readLong(raw);
        if (tenantId == null || tenantId <= 0) {
            throw new ServiceException(400, "可进入的业务租户不正确");
        }
        if (!unique.add(tenantId)) {
            throw new ServiceException(400, "可进入的业务租户不能重复");
        }
    }

    private static Integer readStatus(Object raw) {
        if (raw == null) {
            return null;
        }
        if (raw instanceof Number number) {
            return number.intValue();
        }
        if (raw instanceof String text) {
            if (!StringUtils.hasText(text)) {
                return null;
            }
            try {
                return Integer.valueOf(text.trim());
            } catch (NumberFormatException ex) {
                throw new ServiceException(400, "状态只能是启用或停用");
            }
        }
        throw new ServiceException(400, "状态只能是启用或停用");
    }

    private static Long readLong(Object raw) {
        if (raw == null) {
            return null;
        }
        if (raw instanceof Number number) {
            return number.longValue();
        }
        if (raw instanceof String text && StringUtils.hasText(text)) {
            try {
                return Long.valueOf(text.trim());
            } catch (NumberFormatException ex) {
                return null;
            }
        }
        return null;
    }

    private static String text(Object value) {
        return value == null ? "" : String.valueOf(value);
    }

    private static String trim(String value) {
        return value == null ? "" : value.trim();
    }
}
