package cn.cheers.x.facility.management.api.impl;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.framework.tenant.core.context.TenantContextHolder;
import cn.cheers.x.facility.management.api.FacilityStationApi;
import cn.cheers.x.facility.management.api.dto.FacilityStationRespDTO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 设施 Facade：只读访问动态业务 {@code ent_facility}（已退役 fac_site）。
 */
@Slf4j
@Service
public class FacilityStationApiImpl implements FacilityStationApi {

    private static final String SELECT_ONE = """
            SELECT id, code, name
            FROM ent_facility
            WHERE id = ? AND deleted = false AND status = 1
            """;

    private static final String SELECT_ALL_FOR_TENANT = """
            SELECT id, code, name
            FROM ent_facility
            WHERE deleted = false AND tenant_id = ?
            ORDER BY sort NULLS LAST, id
            """;

    private static final String SELECT_ALL_IDS_FOR_TENANT = """
            SELECT id FROM ent_facility
            WHERE deleted = false AND tenant_id = ?
            ORDER BY id
            """;

    @Resource
    private JdbcTemplate jdbcTemplate;

    @Override
    public CommonResult<List<FacilityStationRespDTO>> getFacilityTree() {
        Long tenantId = TenantContextHolder.getTenantId();
        if (tenantId == null) {
            // 无租户上下文时无法安全过滤；返回空列表而非跨租户泄露
            log.warn("getFacilityTree: tenantId is null, returning empty list");
            return CommonResult.success(Collections.emptyList());
        }
        List<FacilityStationRespDTO> list = jdbcTemplate.query(SELECT_ALL_FOR_TENANT, (rs, rowNum) -> {
            FacilityStationRespDTO dto = new FacilityStationRespDTO();
            dto.setFacilityId(rs.getLong("id"));
            dto.setFacilityCode(rs.getString("code"));
            dto.setFacilityName(rs.getString("name"));
            dto.setParentId(0L);
            dto.setSortNo(0);
            return dto;
        }, tenantId);
        return CommonResult.success(list);
    }

    @Override
    public CommonResult<FacilityStationRespDTO> getFacility(Long facilityId) {
        if (facilityId == null) {
            return CommonResult.success(null);
        }
        try {
            FacilityStationRespDTO dto = jdbcTemplate.queryForObject(SELECT_ONE, (rs, rowNum) -> {
                FacilityStationRespDTO row = new FacilityStationRespDTO();
                row.setFacilityId(rs.getLong("id"));
                row.setFacilityCode(rs.getString("code"));
                row.setFacilityName(rs.getString("name"));
                row.setParentId(0L);
                row.setSortNo(0);
                return row;
            }, facilityId);
            return CommonResult.success(dto);
        } catch (EmptyResultDataAccessException ex) {
            return CommonResult.success(null);
        }
    }

    @Override
    public CommonResult<List<Long>> listChildIds(Long parentId) {
        if (parentId == null || parentId == 0) {
            Long tenantId = TenantContextHolder.getTenantId();
            if (tenantId == null) {
                return CommonResult.success(Collections.emptyList());
            }
            List<Long> ids = jdbcTemplate.query(SELECT_ALL_IDS_FOR_TENANT,
                    (rs, rowNum) -> rs.getLong("id"), tenantId);
            return CommonResult.success(ids);
        }
        // 无 fac_site 层级，非根 parentId 无子节点
        return CommonResult.success(new ArrayList<>());
    }

}
