package cn.cheers.x.module.dynamicbusiness.service.facility;

import cn.cheers.x.module.dynamicbusiness.framework.facility.FacilityOwningFieldCodes;
import cn.cheers.x.module.dynamicbusiness.framework.tenant.TenantPhysicalTableNames;
import jakarta.annotation.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 当前租户有哪些站。只读，不看登录人的授权范围。
 *
 * <p>给没有登录人的系统流程用：告警目录开通时给示例规则挑站、推送订阅没传站时找唯一那座站。
 * 登录人在界面上能选哪些站，见 {@code DataScopeQueryService#listMyFacilities}。</p>
 *
 * 不负责：给记录补所属场站；给目录挂所属场站字段；有多座站时替调用方挑一座。
 */
@Service
public class FacilityStationQueryService {

    @Resource
    private JdbcTemplate jdbcTemplate;

    /** 当前租户未删除的站，按编号排序。没有站时返回空列表。 */
    public List<Long> listStationIds() {
        String table = TenantPhysicalTableNames.entityPhysicalTable(FacilityOwningFieldCodes.TARGET_ENTITY_TYPE);
        if (!tableExists(table)) {
            return List.of();
        }
        return jdbcTemplate.query(
                "SELECT id FROM dynamicbusiness." + table + " WHERE deleted = false AND id > 0 ORDER BY id",
                (rs, row) -> rs.getLong("id"));
    }

    /**
     * 这一租户正好只有一座站时返回它的编号。没有站，或有多座，返回 null。
     * null 表示不能替记录挑站，调用方要报错或跳过。
     */
    public Long onlyStationId() {
        String table = TenantPhysicalTableNames.entityPhysicalTable(FacilityOwningFieldCodes.TARGET_ENTITY_TYPE);
        if (!tableExists(table)) {
            return null;
        }
        List<Long> ids = jdbcTemplate.query(
                "SELECT id FROM dynamicbusiness." + table + " WHERE deleted = false LIMIT 2",
                (rs, row) -> rs.getLong("id"));
        if (ids.size() != 1) {
            return null;
        }
        Long id = ids.get(0);
        return id != null && id > 0 ? id : null;
    }

    private boolean tableExists(String table) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT count(*) FROM information_schema.tables WHERE table_schema = 'dynamicbusiness' AND table_name = ?",
                Integer.class, table);
        return count != null && count > 0;
    }
}
