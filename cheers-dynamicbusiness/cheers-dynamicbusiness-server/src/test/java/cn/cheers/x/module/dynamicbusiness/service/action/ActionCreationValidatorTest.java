package cn.cheers.x.module.dynamicbusiness.service.action;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.model.ModelDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.model.ModelFieldAssignmentDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.model.ModelMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.model.ModelFieldAssignmentMapper;
import cn.cheers.x.module.dynamicbusiness.service.analysis.AnalysisCapabilityAvailabilityChecker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ActionCreationValidatorTest {

    private AnalysisCapabilityAvailabilityChecker capabilityAvailabilityChecker;
    private ModelMapper modelMapper;
    private ModelFieldAssignmentMapper modelFieldAssignmentMapper;
    private ActionCreationValidator validator;

    @BeforeEach
    void setUp() {
        capabilityAvailabilityChecker = mock(AnalysisCapabilityAvailabilityChecker.class);
        modelMapper = mock(ModelMapper.class);
        modelFieldAssignmentMapper = mock(ModelFieldAssignmentMapper.class);
        validator = new ActionCreationValidator(
                capabilityAvailabilityChecker, modelMapper, modelFieldAssignmentMapper);
    }

    @Test
    void intelligentAnalysisAction_requiresAvailableCapability() {
        when(modelMapper.selectById(2L)).thenReturn(actionModel(2L, "action_intelligent_analysis"));
        when(modelFieldAssignmentMapper.selectByModelCodeAndFieldCode(
                "action_intelligent_analysis", ActionFieldCodes.ACTION_DOMAIN))
                .thenReturn(domainAssignment("INTELLIGENT_ANALYSIS"));
        assertThrows(ServiceException.class, () -> validator.validate("action", 2L, new java.util.HashMap<>()));

        validator.validate("action", 2L, new java.util.HashMap<>(Map.of(
                ActionFieldCodes.ANALYSIS_CAPABILITY_ID, 41L)));

        verify(capabilityAvailabilityChecker).assertAvailable(41L);
    }

    @Test
    void deviceAction_cannotCarryAnalysisCapability() {
        when(modelMapper.selectById(1L)).thenReturn(actionModel(1L, "action_device"));
        when(modelFieldAssignmentMapper.selectByModelCodeAndFieldCode(
                "action_device", ActionFieldCodes.ACTION_DOMAIN))
                .thenReturn(domainAssignment("DEVICE"));
        assertThrows(ServiceException.class, () -> validator.validate("action", 1L, new java.util.HashMap<>(Map.of(
                ActionFieldCodes.ANALYSIS_CAPABILITY_ID, 41L))));
        verify(capabilityAvailabilityChecker, never()).assertAvailable(41L);
    }

    @Test
    void executableAction_requiresDomainDefaultFromFieldAssignment() {
        when(modelMapper.selectById(1L)).thenReturn(actionModel(1L, "action_device"));

        assertThrows(ServiceException.class,
                () -> validator.validate("action", 1L, new java.util.HashMap<>()));
    }

    private static ModelDO actionModel(Long id, String code) {
        ModelDO model = new ModelDO();
        model.setId(id);
        model.setCode(code);
        model.setEntityTypeCode("action");
        model.setDeleted(false);
        return model;
    }

    private static ModelFieldAssignmentDO domainAssignment(String value) {
        ModelFieldAssignmentDO assignment = new ModelFieldAssignmentDO();
        assignment.setFieldCode(ActionFieldCodes.ACTION_DOMAIN);
        assignment.setDefaultValue(value);
        return assignment;
    }
}
