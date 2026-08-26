package cn.cheers.x.module.dynamicbusiness.service.model.governance;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.field.FieldDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.model.ModelDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.model.ModelFieldAssignmentDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.field.FieldMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.model.ModelFieldAssignmentMapper;
import cn.cheers.x.module.dynamicbusiness.service.model.core.ModelCoreService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class LocalPackagePromotionServiceImplTest {

    private final ModelCoreService modelCoreService = mock(ModelCoreService.class);
    private final ModelFieldAssignmentMapper assignmentMapper = mock(ModelFieldAssignmentMapper.class);
    private final FieldMapper fieldMapper = mock(FieldMapper.class);
    private final MasterDataCapabilityChecker capabilityChecker = mock(MasterDataCapabilityChecker.class);

    private LocalPackagePromotionService service;

    @BeforeEach
    void setUp() {
        service = new LocalPackagePromotionServiceImpl(
                modelCoreService, assignmentMapper, fieldMapper, capabilityChecker);
    }

    @Test
    void promotesLocalFieldsBeforePromotingModel() {
        ModelDO model = model(1L, "LOCAL", 10L);
        ModelFieldAssignmentDO assignment = assignment(100L, 1L, 11L, "F-LOCAL");
        FieldDO localField = field(11L, "F-LOCAL", "LOCAL", 10L);
        when(capabilityChecker.canPromotePackage()).thenReturn(true);
        when(modelCoreService.get(1L)).thenReturn(model);
        when(assignmentMapper.selectByModelId(1L)).thenReturn(List.of(assignment));
        when(fieldMapper.selectById(11L)).thenReturn(localField);

        service.promoteLocalPackage(1L, Map.of());

        InOrder order = inOrder(fieldMapper, modelCoreService);
        order.verify(fieldMapper).updateById(org.mockito.ArgumentMatchers.<FieldDO>argThat(field ->
                field.getId().equals(11L)
                        && "COMPANY".equals(field.getGovernanceStatus())
                        && field.getOriginFacilityId() == null));
        order.verify(modelCoreService).update(org.mockito.ArgumentMatchers.argThat(updated ->
                updated.getId().equals(1L)
                        && "COMPANY".equals(updated.getGovernanceStatus())
                        && updated.getOriginFacilityId() == null));
    }

    @Test
    void mergeRepointsAssignmentAndRetainsLocalField() {
        ModelDO model = model(2L, "LOCAL", 10L);
        ModelFieldAssignmentDO assignment = assignment(200L, 2L, 21L, "F-LOCAL");
        FieldDO localField = field(21L, "F-LOCAL", "LOCAL", 10L);
        FieldDO companyField = field(22L, "F-COMPANY", "COMPANY", null);
        when(capabilityChecker.canPromotePackage()).thenReturn(true);
        when(modelCoreService.get(2L)).thenReturn(model);
        when(assignmentMapper.selectByModelId(2L)).thenReturn(List.of(assignment));
        when(fieldMapper.selectById(21L)).thenReturn(localField);
        when(fieldMapper.selectByCode("F-COMPANY")).thenReturn(companyField);

        service.promoteLocalPackage(2L, Map.of("F-LOCAL", "F-COMPANY"));

        verify(assignmentMapper).updateById(org.mockito.ArgumentMatchers.<ModelFieldAssignmentDO>argThat(updated ->
                updated.getId().equals(200L)
                        && updated.getFieldId().equals(22L)
                        && "F-COMPANY".equals(updated.getFieldCode())));
        verify(fieldMapper, never()).updateById(org.mockito.ArgumentMatchers.<FieldDO>any());
        assertEquals("LOCAL", localField.getGovernanceStatus());
    }

    @Test
    void fieldFailureNeverLeavesModelPromotedAndMethodIsTransactional() throws Exception {
        ModelDO model = model(3L, "LOCAL", 10L);
        ModelFieldAssignmentDO assignment = assignment(300L, 3L, 31L, "F-LOCAL");
        FieldDO localField = field(31L, "F-LOCAL", "LOCAL", 10L);
        when(capabilityChecker.canPromotePackage()).thenReturn(true);
        when(modelCoreService.get(3L)).thenReturn(model);
        when(assignmentMapper.selectByModelId(3L)).thenReturn(List.of(assignment));
        when(fieldMapper.selectById(31L)).thenReturn(localField);
        when(fieldMapper.updateById(org.mockito.ArgumentMatchers.<FieldDO>any()))
                .thenThrow(new IllegalStateException("db failure"));

        assertThrows(IllegalStateException.class, () -> service.promoteLocalPackage(3L, Map.of()));
        verify(modelCoreService, never()).update(org.mockito.ArgumentMatchers.any());

        Method method = LocalPackagePromotionServiceImpl.class
                .getMethod("promoteLocalPackage", Long.class, Map.class);
        Transactional transactional = method.getAnnotation(Transactional.class);
        assertTrue(transactional != null
                && List.of(transactional.rollbackFor()).contains(Exception.class));
    }

    @Test
    void promotionRequiresCapability() {
        when(capabilityChecker.canPromotePackage()).thenReturn(false);

        assertThrows(ServiceException.class, () -> service.promoteLocalPackage(4L, Map.of()));

        verify(modelCoreService, never()).get(4L);
    }

    private static ModelDO model(Long id, String governanceStatus, Long originFacilityId) {
        ModelDO model = new ModelDO();
        model.setId(id);
        model.setGovernanceStatus(governanceStatus);
        model.setOriginFacilityId(originFacilityId);
        return model;
    }

    private static ModelFieldAssignmentDO assignment(
            Long id, Long modelId, Long fieldId, String fieldCode) {
        ModelFieldAssignmentDO assignment = new ModelFieldAssignmentDO();
        assignment.setId(id);
        assignment.setModelId(modelId);
        assignment.setFieldId(fieldId);
        assignment.setFieldCode(fieldCode);
        return assignment;
    }

    private static FieldDO field(
            Long id, String code, String governanceStatus, Long originFacilityId) {
        FieldDO field = new FieldDO();
        field.setId(id);
        field.setCode(code);
        field.setGovernanceStatus(governanceStatus);
        field.setOriginFacilityId(originFacilityId);
        return field;
    }
}
