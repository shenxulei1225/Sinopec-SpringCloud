package cn.cheers.x.module.dynamicbusiness.service.field;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.field.FieldDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.model.ModelFieldAssignmentDO;
import cn.cheers.x.module.dynamicbusiness.framework.facility.FacilityOwningFieldCodes;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CustomFieldValidationServiceImplTest {

    private final CustomFieldValidationServiceImpl service = new CustomFieldValidationServiceImpl();

    @Test
    void validateCustomFields_baseSourcedRequired_readsFromBaseFields() {
        FieldDO facilityField = FieldDO.builder()
                .id(9001L)
                .code(FacilityOwningFieldCodes.FIELD_CODE)
                .name(FacilityOwningFieldCodes.DISPLAY_NAME)
                .type("ENTITY_REF")
                .build();
        ModelFieldAssignmentDO assignment = ModelFieldAssignmentDO.builder()
                .fieldId(9001L)
                .required(true)
                .fieldSource("BASE")
                .build();

        Map<Long, FieldDO> fieldMap = Map.of(9001L, facilityField);
        Map<Long, ModelFieldAssignmentDO> assignmentMap = Map.of(9001L, assignment);

        Map<String, Object> base = new LinkedHashMap<>();
        base.put(FacilityOwningFieldCodes.FIELD_CODE, FacilityOwningFieldCodes.toApiRef(45L));
        Map<String, Object> custom = new LinkedHashMap<>();
        custom.put("FLD-PNT-001", FacilityOwningFieldCodes.toApiRef(45L));

        assertDoesNotThrow(() -> service.validateCustomFields(fieldMap, assignmentMap, base, custom));
    }

    @Test
    void validateCustomFields_baseSourcedRequired_missingInBothMaps_fails() {
        FieldDO facilityField = FieldDO.builder()
                .id(9001L)
                .code(FacilityOwningFieldCodes.FIELD_CODE)
                .name(FacilityOwningFieldCodes.DISPLAY_NAME)
                .type("ENTITY_REF")
                .build();
        ModelFieldAssignmentDO assignment = ModelFieldAssignmentDO.builder()
                .fieldId(9001L)
                .required(true)
                .fieldSource("BASE")
                .build();

        Map<Long, FieldDO> fieldMap = Map.of(9001L, facilityField);
        Map<Long, ModelFieldAssignmentDO> assignmentMap = Map.of(9001L, assignment);

        assertThrows(ServiceException.class,
                () -> service.validateCustomFields(fieldMap, assignmentMap, Map.of(), Map.of()));
    }
}
