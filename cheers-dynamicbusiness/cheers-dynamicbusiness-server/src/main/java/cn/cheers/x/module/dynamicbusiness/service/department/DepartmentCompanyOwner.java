package cn.cheers.x.module.dynamicbusiness.service.department;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entity.EntityDO;
import cn.cheers.x.module.dynamicbusiness.dal.repository.entity.EntityRepository;
import cn.cheers.x.module.dynamicbusiness.framework.facility.FacilityOwningFieldCodes;
import cn.cheers.x.module.dynamicbusiness.service.entity.EntityDedicatedColumnService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 保存部门时写下所属。
 * 场站上的部门只写所属场站。公司或分公司上的部门只写所属公司。
 * 创建之后这两处都不再改。
 */
@Component
public class DepartmentCompanyOwner {

    @Resource
    private DepartmentCompanyFieldEnsureService departmentCompanyFieldEnsureService;
    @Resource
    private EntityRepository entityRepository;
    @Resource
    private EntityDedicatedColumnService entityDedicatedColumnService;

    public void applyOnCreate(Map<String, Object> baseFields, Map<String, Object> customFields) {
        departmentCompanyFieldEnsureService.ensure();
        Long facilityId = readId(baseFields, customFields, FacilityOwningFieldCodes.FIELD_CODE);
        Long companyId = readId(baseFields, customFields, DepartmentCompanyCodes.FIELD_CODE);
        DepartmentOwnerRules.checkExactlyOne(facilityId, companyId);
        if (facilityId != null) {
            writeFacility(baseFields, customFields, facilityId);
            return;
        }
        requireRegion(companyId);
        writeCompany(baseFields, customFields, companyId);
    }

    public void applyOnUpdate(Map<String, Object> baseFields, Map<String, Object> customFields, EntityDO dbEntity) {
        departmentCompanyFieldEnsureService.ensure();
        Map<String, Object> stored = new LinkedHashMap<>();
        if (dbEntity.getCustomFields() != null) {
            stored.putAll(dbEntity.getCustomFields());
        }
        entityDedicatedColumnService.mergePhysicalColumnsIntoBaseFields(dbEntity, stored);
        Long oldFacilityId = FacilityOwningFieldCodes.extractId(stored.get(FacilityOwningFieldCodes.FIELD_CODE));
        Long oldCompanyId = FacilityOwningFieldCodes.extractId(stored.get(DepartmentCompanyCodes.FIELD_CODE));
        Long facilityId = readId(baseFields, customFields, FacilityOwningFieldCodes.FIELD_CODE);
        Long companyId = readId(baseFields, customFields, DepartmentCompanyCodes.FIELD_CODE);
        if (oldFacilityId != null) {
            if (companyId != null) {
                throw new ServiceException(400, DepartmentOwnerRules.BOTH);
            }
            if (facilityId != null && !oldFacilityId.equals(facilityId)) {
                throw new ServiceException(400, "所属场站创建后不可修改（挪站请走迁移能力）");
            }
            writeFacility(baseFields, customFields, oldFacilityId);
            return;
        }
        if (oldCompanyId != null) {
            if (facilityId != null) {
                throw new ServiceException(400, DepartmentOwnerRules.BOTH);
            }
            if (companyId != null && !oldCompanyId.equals(companyId)) {
                throw new ServiceException(400, DepartmentOwnerRules.COMPANY_LOCKED);
            }
            writeCompany(baseFields, customFields, oldCompanyId);
            return;
        }
        DepartmentOwnerRules.checkExactlyOne(facilityId, companyId);
        if (facilityId != null) {
            writeFacility(baseFields, customFields, facilityId);
            return;
        }
        requireRegion(companyId);
        writeCompany(baseFields, customFields, companyId);
    }

    private void requireRegion(Long companyId) {
        if (companyId == null || entityRepository.findById(companyId, DepartmentCompanyCodes.TARGET) == null) {
            throw new ServiceException(400, DepartmentOwnerRules.COMPANY_INVALID);
        }
    }

    private static void writeFacility(
            Map<String, Object> baseFields, Map<String, Object> customFields, Long facilityId) {
        baseFields.put(FacilityOwningFieldCodes.FIELD_CODE, FacilityOwningFieldCodes.toApiRef(facilityId));
        baseFields.remove(DepartmentCompanyCodes.FIELD_CODE);
        if (customFields != null) {
            customFields.remove(FacilityOwningFieldCodes.FIELD_CODE);
            customFields.remove(DepartmentCompanyCodes.FIELD_CODE);
        }
    }

    private static void writeCompany(
            Map<String, Object> baseFields, Map<String, Object> customFields, Long companyId) {
        Map<String, Object> ref = new LinkedHashMap<>(2);
        ref.put("entityTypeCode", DepartmentCompanyCodes.TARGET);
        ref.put("id", companyId);
        baseFields.put(DepartmentCompanyCodes.FIELD_CODE, ref);
        baseFields.remove(FacilityOwningFieldCodes.FIELD_CODE);
        if (customFields != null) {
            customFields.remove(DepartmentCompanyCodes.FIELD_CODE);
            customFields.remove(FacilityOwningFieldCodes.FIELD_CODE);
        }
    }

    private static Long readId(Map<String, Object> baseFields, Map<String, Object> customFields, String key) {
        if (baseFields != null && baseFields.containsKey(key)) {
            return FacilityOwningFieldCodes.extractId(baseFields.get(key));
        }
        if (customFields != null) {
            return FacilityOwningFieldCodes.extractId(customFields.get(key));
        }
        return null;
    }
}
