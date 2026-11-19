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
 * 保存部门时写下它挂在哪：场站上的部门只写所属场站，组织机构节点上的部门只写所属组织。
 * 创建之后这两处都不再改。
 * 不负责：替没填归属的部门挑一处。两处都空就拒绝保存。
 */
@Component
public class DepartmentOwner {

    @Resource
    private EntityRepository entityRepository;
    @Resource
    private EntityDedicatedColumnService entityDedicatedColumnService;

    public void applyOnCreate(Map<String, Object> baseFields, Map<String, Object> customFields) {
        Long facilityId = readId(baseFields, customFields, FacilityOwningFieldCodes.FIELD_CODE);
        Long organizationId = readId(baseFields, customFields, DepartmentOrganizationCodes.FIELD_CODE);
        DepartmentOwnerRules.checkExactlyOne(facilityId, organizationId);
        if (facilityId != null) {
            writeFacility(baseFields, customFields, facilityId);
            return;
        }
        requireOrganization(organizationId);
        writeOrganization(baseFields, customFields, organizationId);
    }

    public void applyOnUpdate(Map<String, Object> baseFields, Map<String, Object> customFields, EntityDO dbEntity) {
        Map<String, Object> stored = new LinkedHashMap<>();
        if (dbEntity.getCustomFields() != null) {
            stored.putAll(dbEntity.getCustomFields());
        }
        entityDedicatedColumnService.mergePhysicalColumnsIntoBaseFields(dbEntity, stored);
        Long oldFacilityId = FacilityOwningFieldCodes.extractId(stored.get(FacilityOwningFieldCodes.FIELD_CODE));
        Long oldOrganizationId = FacilityOwningFieldCodes.extractId(stored.get(DepartmentOrganizationCodes.FIELD_CODE));
        Long facilityId = readId(baseFields, customFields, FacilityOwningFieldCodes.FIELD_CODE);
        Long organizationId = readId(baseFields, customFields, DepartmentOrganizationCodes.FIELD_CODE);
        if (oldFacilityId != null) {
            if (organizationId != null) {
                throw new ServiceException(400, DepartmentOwnerRules.BOTH);
            }
            if (facilityId != null && !oldFacilityId.equals(facilityId)) {
                throw new ServiceException(400, "所属场站创建后不可修改（挪站请走迁移能力）");
            }
            writeFacility(baseFields, customFields, oldFacilityId);
            return;
        }
        if (oldOrganizationId != null) {
            if (facilityId != null) {
                throw new ServiceException(400, DepartmentOwnerRules.BOTH);
            }
            if (organizationId != null && !oldOrganizationId.equals(organizationId)) {
                throw new ServiceException(400, DepartmentOwnerRules.ORGANIZATION_LOCKED);
            }
            writeOrganization(baseFields, customFields, oldOrganizationId);
            return;
        }
        DepartmentOwnerRules.checkExactlyOne(facilityId, organizationId);
        if (facilityId != null) {
            writeFacility(baseFields, customFields, facilityId);
            return;
        }
        requireOrganization(organizationId);
        writeOrganization(baseFields, customFields, organizationId);
    }

    private void requireOrganization(Long organizationId) {
        if (organizationId == null
                || entityRepository.findById(organizationId, DepartmentOrganizationCodes.TARGET) == null) {
            throw new ServiceException(400, DepartmentOwnerRules.ORGANIZATION_INVALID);
        }
    }

    private static void writeFacility(
            Map<String, Object> baseFields, Map<String, Object> customFields, Long facilityId) {
        baseFields.put(FacilityOwningFieldCodes.FIELD_CODE, FacilityOwningFieldCodes.toApiRef(facilityId));
        baseFields.remove(DepartmentOrganizationCodes.FIELD_CODE);
        if (customFields != null) {
            customFields.remove(FacilityOwningFieldCodes.FIELD_CODE);
            customFields.remove(DepartmentOrganizationCodes.FIELD_CODE);
        }
    }

    private static void writeOrganization(
            Map<String, Object> baseFields, Map<String, Object> customFields, Long organizationId) {
        Map<String, Object> ref = new LinkedHashMap<>(2);
        ref.put("entityTypeCode", DepartmentOrganizationCodes.TARGET);
        ref.put("id", organizationId);
        baseFields.put(DepartmentOrganizationCodes.FIELD_CODE, ref);
        baseFields.remove(FacilityOwningFieldCodes.FIELD_CODE);
        if (customFields != null) {
            customFields.remove(DepartmentOrganizationCodes.FIELD_CODE);
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
