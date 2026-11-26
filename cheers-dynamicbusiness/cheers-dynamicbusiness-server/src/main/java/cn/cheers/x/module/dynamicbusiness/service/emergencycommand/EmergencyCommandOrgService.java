package cn.cheers.x.module.dynamicbusiness.service.emergencycommand;

import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityRespVO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.datapermission.PostAssignmentDO;
import cn.cheers.x.module.dynamicbusiness.framework.facility.FacilityOwningFieldCodes;
import cn.cheers.x.module.dynamicbusiness.service.datapermission.PostAssignmentQueryService;
import cn.cheers.x.module.dynamicbusiness.service.datapermission.PostAssignmentQueryService.PostAssignmentView;
import cn.cheers.x.module.dynamicbusiness.service.entity.EntityService;
import com.alibaba.fastjson2.JSONObject;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 启动响应时，把预案上的指挥班子抄到这一起事件上，并按当时的任职写下人名。
 *
 * 管什么：事发场站和它所属作业区上的职务算事发单位，组织机构最上一层算公司。
 * 不管什么：预案页怎么编辑这套班子。编辑仍写在预案上。
 * 禁止：启动之后再按预案重算。人名空着就空着，不挑一个别人顶上。
 */
@Service
public class EmergencyCommandOrgService {

    private static final String ORGANIZATION = "organization";
    private static final String FACILITY = "facility";

    @Resource
    private EntityService entityService;
    @Resource
    private PostAssignmentQueryService postAssignmentQueryService;

    public JSONObject snapshot(EntityRespVO plan, EntityRespVO event) {
        List<EmergencyCommandOrg.Group> groups = EmergencyCommandOrg.parseTemplate(
                field(plan, EmergencyCommandFields.COMMAND_ORG_TEMPLATE));
        Long facilityId = FacilityOwningFieldCodes.extractId(field(event, FacilityOwningFieldCodes.FIELD_CODE));
        Long organizationId = organizationOf(facilityId);
        Map<String, String> unitHolders = new LinkedHashMap<>();
        if (organizationId != null) {
            unitHolders.putAll(holders(ORGANIZATION, organizationId));
        }
        if (facilityId != null) {
            unitHolders.putAll(holders(FACILITY, facilityId));
        }
        Map<String, String> companyHolders = holders(ORGANIZATION, companyRoot(organizationId));
        return EmergencyCommandOrg.snapshot(groups, unitHolders, companyHolders);
    }

    /** 场站上的所属作业区。场站不存在或没填时为空。 */
    private Long organizationOf(Long facilityId) {
        if (facilityId == null) {
            return null;
        }
        EntityRespVO facility = entityService.get(facilityId, FACILITY);
        if (facility == null) {
            return null;
        }
        return FacilityOwningFieldCodes.extractId(field(facility, "organization_id"));
    }

    /** 沿组织机构的上级走到最上一层。没有上级时，这一层自己就是公司。 */
    private Long companyRoot(Long organizationId) {
        Long current = organizationId;
        for (int hop = 0; hop < 8 && current != null; hop++) {
            EntityRespVO organization = entityService.get(current, ORGANIZATION);
            if (organization == null || organization.getParentId() == null || organization.getParentId() <= 0) {
                return current;
            }
            current = organization.getParentId();
        }
        return current;
    }

    /** 这个单元上今天仍在任的人。同一职务有多人时保留先读到的那位。 */
    private Map<String, String> holders(String registry, Long unitId) {
        Map<String, String> holders = new LinkedHashMap<>();
        if (unitId == null) {
            return holders;
        }
        LocalDate today = LocalDate.now();
        for (PostAssignmentView view : postAssignmentQueryService.listViewsByManagementUnitId(unitId)) {
            PostAssignmentDO row = view.row();
            String written = row.getManagementUnitRegistry();
            String actual = written == null || written.isBlank() ? ORGANIZATION : written.trim();
            if (!registry.equals(actual)) {
                continue;
            }
            if (!active(row, today)) {
                continue;
            }
            String postName = view.dutyPostName() == null ? "" : view.dutyPostName().trim();
            String person = view.userName() == null ? "" : view.userName().trim();
            if (postName.isEmpty() || person.isEmpty() || holders.containsKey(postName)) {
                continue;
            }
            holders.put(postName, person);
        }
        return holders;
    }

    private static boolean active(PostAssignmentDO row, LocalDate today) {
        if (row.getValidFrom() != null && row.getValidFrom().isAfter(today)) {
            return false;
        }
        return row.getValidTo() == null || !row.getValidTo().isBefore(today);
    }

    private static Object field(EntityRespVO entity, String code) {
        if (entity.getCustomFields() != null && entity.getCustomFields().get(code) != null) {
            return entity.getCustomFields().get(code);
        }
        if (entity.getBaseFields() != null) {
            return entity.getBaseFields().get(code);
        }
        return null;
    }
}
