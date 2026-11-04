package cn.cheers.x.module.dynamicbusiness.service.entitytype;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entitytype.vo.EntityTypeBaseFieldSaveReqVO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entitytype.EntityTypeBaseFieldDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entitytype.EntityTypeDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.field.FieldDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.entitytype.EntityTypeBaseFieldMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.entitytype.EntityTypeMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.field.FieldMapper;
import cn.cheers.x.module.dynamicbusiness.enums.entitytype.EntityTypeEntryKindEnum;
import cn.cheers.x.module.dynamicbusiness.framework.facility.FacilityOwningFieldCodes;
import cn.cheers.x.module.dynamicbusiness.framework.tenant.TenantPhysicalTableNames;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.regex.Pattern;

/**
 * 站场级类型确保挂接「所属场站」基础字段（字段库 facility_id → 设施类型；专用表列）。
 * DOMAIN/SCOPE 复用底座表，不在入口编码上重复挂列。
 */
@Service
@Slf4j
public class FacilityOwningFieldEnsureService {

    private static final Pattern COLUMN_NAME = Pattern.compile("^[a-z][a-z0-9_]*$");

    @Resource
    private EntityTypeMapper entityTypeMapper;

    @Resource
    private JdbcTemplate jdbcTemplate;

    @Resource
    private EntityTypeBaseFieldMapper baseFieldMapper;

    @Resource
    private FieldMapper fieldMapper;

    @Resource
    @Lazy
    private EntityTypeBaseFieldService entityTypeBaseFieldService;

    @Resource
    @Lazy
    private BaseFieldLibrarySyncService baseFieldLibrarySyncService;

    /**
     * 当前租户下，为所有应挂接的站场级类型补「所属场站」（幂等）。
     * 用于存量类型在创建链路之外补齐；新建/更新仍走 {@link #ensureForEntityTypeCode}。
     */
    @Transactional(rollbackFor = Exception.class)
    public int ensureAllEligible() {
        int attached = 0;
        for (EntityTypeDO entityType : entityTypeMapper.selectAllList()) {
            if (!shouldEnsure(entityType)) {
                continue;
            }
            String code = entityType.getCode().trim();
            EntityTypeBaseFieldDO existing =
                    baseFieldMapper.selectByEntityTypeCodeAndFieldCode(code, FacilityOwningFieldCodes.FIELD_CODE);
            ensureForEntityTypeCode(code);
            if (existing == null) {
                attached++;
            }
        }
        if (attached > 0) {
            log.info("[FacilityOwningFieldEnsure] ensureAllEligible attached={}", attached);
        }
        return attached;
    }

    @Transactional(rollbackFor = Exception.class)
    public void ensureForEntityTypeCode(String entityTypeCode) {
        if (!StringUtils.hasText(entityTypeCode)) {
            return;
        }
        EntityTypeDO entityType = entityTypeMapper.selectByCode(entityTypeCode.trim());
        if (entityType == null || !shouldEnsure(entityType)) {
            return;
        }
        String code = entityType.getCode().trim();
        EntityTypeBaseFieldDO existing =
                baseFieldMapper.selectByEntityTypeCodeAndFieldCode(code, FacilityOwningFieldCodes.FIELD_CODE);
        FieldDO libraryField = fieldMapper.selectByCode(FacilityOwningFieldCodes.FIELD_CODE);
        if (existing != null) {
            // 这一类型上所属场站已经在。型号创建时会把基础字段分到型号，实体创建不再扫全部型号。
            return;
        }

        if (libraryField == null || libraryField.getId() == null) {
            throw new ServiceException(400,
                    "字段库缺少 " + FacilityOwningFieldCodes.FIELD_CODE
                            + "（所属场站，REF→facility）；请先执行 seed / V48 后再创建站场级类型");
        }

        EntityTypeBaseFieldSaveReqVO req = new EntityTypeBaseFieldSaveReqVO();
        req.setEntityTypeCode(code);
        req.setLibraryFieldId(libraryField.getId());
        req.setRequired(true);
        req.setIsSearchable(false);
        req.setIsFilterable(true);
        req.setIsSortable(true);
        req.setFieldName(FacilityOwningFieldCodes.DISPLAY_NAME);
        req.setDescription("站场级归属设施；创建写入、编辑只读");
        entityTypeBaseFieldService.createBaseField(req);
        log.info("[FacilityOwningFieldEnsure] attached facility_id to entityType={}", code);
    }

    /**
     * 当前租户未删除的站，按编号排序。
     * 没有站时返回空列表。不从里面挑一座。
     */
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
     * 这一租户正好只有一座站时返回它的编号。没有站，或有多座，返回空。
     * 空着表示不能替记录挑站。
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

    /**
     * 把另一列里已经写下的站抄到所属场站。所属场站已经有值的行不动。
     * 源列名只允许字段编码。抄完后调用方再停用旧列，避免表单上出现两列所属场站。
     */
    public void copyColumnIntoOwningStation(String entityTypeCode, String sourceColumn) {
        if (!StringUtils.hasText(entityTypeCode) || sourceColumn == null || !COLUMN_NAME.matcher(sourceColumn).matches()) {
            throw new ServiceException(400, "不能从这一列抄所属场站");
        }
        String table = TenantPhysicalTableNames.entityPhysicalTable(entityTypeCode.trim());
        if (!hasColumn(table, FacilityOwningFieldCodes.FIELD_CODE) || !hasColumn(table, sourceColumn)) {
            return;
        }
        jdbcTemplate.update(
                "UPDATE dynamicbusiness." + table
                        + " SET " + FacilityOwningFieldCodes.FIELD_CODE + " = " + sourceColumn
                        + " WHERE deleted = false AND " + FacilityOwningFieldCodes.FIELD_CODE + " IS NULL AND "
                        + sourceColumn + " IS NOT NULL");
    }

    /**
     * 站场级目录里还没写所属场站的行，在这一租户只有一座站时写上这座站。
     * 有多座站时不挑，那些行继续空着，列表里不会冒出来。
     */
    public void fillBlankOwningStationWhenTenantHasOne(String entityTypeCode) {
        if (!StringUtils.hasText(entityTypeCode)) {
            return;
        }
        Long stationId = onlyStationId();
        if (stationId == null) {
            return;
        }
        EntityTypeDO type = entityTypeMapper.selectByCode(entityTypeCode.trim());
        if (type == null || !EntityTypeDO.WORK_SCOPE_FACILITY.equalsIgnoreCase(
                type.getWorkScope() == null ? "" : type.getWorkScope().trim())) {
            return;
        }
        String table = TenantPhysicalTableNames.entityPhysicalTable(entityTypeCode.trim());
        if (!hasColumn(table, FacilityOwningFieldCodes.FIELD_CODE)) {
            return;
        }
        int updated = jdbcTemplate.update(
                "UPDATE dynamicbusiness." + table
                        + " SET facility_id = ? WHERE deleted = false AND facility_id IS NULL",
                stationId);
        if (updated > 0) {
            log.info("[FacilityOwningFieldEnsure] filled facility_id entityType={} rows={} station={}",
                    entityTypeCode, updated, stationId);
        }
    }

    private boolean tableExists(String table) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT count(*) FROM information_schema.tables WHERE table_schema = 'dynamicbusiness' AND table_name = ?",
                Integer.class, table);
        return count != null && count > 0;
    }

    private boolean hasColumn(String table, String column) {
        if (!tableExists(table)) {
            return false;
        }
        Integer count = jdbcTemplate.queryForObject(
                "SELECT count(*) FROM information_schema.columns WHERE table_schema = 'dynamicbusiness' AND table_name = ? AND column_name = ?",
                Integer.class, table, column);
        return count != null && count > 0;
    }

    static boolean shouldEnsure(EntityTypeDO entityType) {
        if (entityType == null || !StringUtils.hasText(entityType.getCode())) {
            return false;
        }
        if (FacilityOwningFieldCodes.TARGET_ENTITY_TYPE.equalsIgnoreCase(entityType.getCode().trim())) {
            return false;
        }
        String scope = entityType.getWorkScope();
        if (!StringUtils.hasText(scope)) {
            scope = EntityTypeDO.WORK_SCOPE_FACILITY;
        }
        if (EntityTypeDO.WORK_SCOPE_NETWORK.equalsIgnoreCase(scope.trim())) {
            return false;
        }
        EntityTypeEntryKindEnum kind = EntityTypeEntryKindEnum.fromCode(entityType.getEntryKind());
        // 底座 / 分类自有表才挂列；DOMAIN、SCOPE、REUSE 共用底座存储
        return !kind.reusesBaseStorage();
    }
}
