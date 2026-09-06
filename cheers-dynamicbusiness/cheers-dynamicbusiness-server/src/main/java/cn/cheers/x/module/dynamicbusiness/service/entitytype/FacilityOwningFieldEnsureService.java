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
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * 站场级类型确保挂接「所属场站」基础字段（字段库 facility_id → 设施类型；专用表列）。
 * DOMAIN/SCOPE 复用底座表，不在入口编码上重复挂列。
 */
@Service
@Slf4j
public class FacilityOwningFieldEnsureService {

    @Resource
    private EntityTypeMapper entityTypeMapper;

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
            if (libraryField != null) {
                baseFieldLibrarySyncService.assignLibraryFieldToAllModels(code, libraryField, existing);
            }
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
