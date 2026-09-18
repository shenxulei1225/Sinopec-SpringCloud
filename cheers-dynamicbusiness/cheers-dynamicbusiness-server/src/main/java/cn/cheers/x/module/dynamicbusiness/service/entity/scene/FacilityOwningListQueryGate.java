package cn.cheers.x.module.dynamicbusiness.service.entity.scene;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.FieldFilterReqVO;
import cn.cheers.x.module.dynamicbusiness.framework.facility.FacilityOwningFieldCodes;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Locale;

/**
 * 站场级列表查询：没有所属场站就不能列出其他场站的点。
 *
 * <p>管什么：列表场景是否已带所属场站，或已经用实体 id 钉死了行。
 * 不管什么：怎么执行 SQL、全网类型、设施目录自身、租户隔离。
 * 禁止：缺站时仍把金桥、洛阳的点混进同一份下拉。</p>
 */
public final class FacilityOwningListQueryGate {

    public static final String MISSING_FACILITY_MESSAGE = "站场级列表须指定所属场站";

    private FacilityOwningListQueryGate() {
    }

    /**
     * 站场级列表：必须带所属场站，或已经按实体 id 点名（不是扫全表）。
     */
    public static void assertListHasOwningOrIdPin(boolean requiresOwning, Long entityId,
                                                 List<FieldFilterReqVO> filters) {
        if (!requiresOwning) {
            return;
        }
        if (isPinnedByEntityId(entityId, filters)) {
            return;
        }
        if (hasOwningFilter(filters)) {
            return;
        }
        throw new ServiceException(400, MISSING_FACILITY_MESSAGE);
    }

    static boolean hasOwningFilter(List<FieldFilterReqVO> filters) {
        if (filters == null || filters.isEmpty()) {
            return false;
        }
        for (FieldFilterReqVO filter : filters) {
            if (filter == null || !StringUtils.hasText(filter.getFieldCode())) {
                continue;
            }
            if (!FacilityOwningFieldCodes.FIELD_CODE.equals(filter.getFieldCode().trim())) {
                continue;
            }
            if (Boolean.TRUE.equals(filter.getRelationField())) {
                continue;
            }
            String op = filter.getOp() == null ? "" : filter.getOp().trim().toUpperCase(Locale.ROOT);
            if (!"EQ".equals(op) && !"IN".equals(op)) {
                continue;
            }
            if (FacilityOwningFieldCodes.extractId(filter.getValue()) != null) {
                return true;
            }
            if ("IN".equals(op) && hasPositiveIdInCollection(filter.getValue())) {
                return true;
            }
        }
        return false;
    }

    static boolean isPinnedByEntityId(Long entityId, List<FieldFilterReqVO> filters) {
        if (entityId != null && entityId > 0) {
            return true;
        }
        if (filters == null || filters.isEmpty()) {
            return false;
        }
        for (FieldFilterReqVO filter : filters) {
            if (filter == null || !StringUtils.hasText(filter.getFieldCode())) {
                continue;
            }
            if (!"id".equalsIgnoreCase(filter.getFieldCode().trim())) {
                continue;
            }
            String op = filter.getOp() == null ? "" : filter.getOp().trim().toUpperCase(Locale.ROOT);
            if ("EQ".equals(op) && FacilityOwningFieldCodes.extractId(filter.getValue()) != null) {
                return true;
            }
            if ("IN".equals(op) && hasPositiveIdInCollection(filter.getValue())) {
                return true;
            }
        }
        return false;
    }

    private static boolean hasPositiveIdInCollection(Object raw) {
        if (!(raw instanceof Iterable<?> items)) {
            return FacilityOwningFieldCodes.extractId(raw) != null;
        }
        for (Object item : items) {
            if (FacilityOwningFieldCodes.extractId(item) != null) {
                return true;
            }
        }
        return false;
    }
}
