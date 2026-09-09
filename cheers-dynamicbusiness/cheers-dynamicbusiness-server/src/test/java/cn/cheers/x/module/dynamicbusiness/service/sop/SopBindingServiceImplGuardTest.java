package cn.cheers.x.module.dynamicbusiness.service.sop;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.sop.vo.SopMethodBindingUpsertReqVO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.sop.SopMethodBindingDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.sop.SopInstanceBindingMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.sop.SopMethodBindingMapper;
import cn.cheers.x.module.dynamicbusiness.service.entity.EntityService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * 切换守卫：检查项旧 method binding 禁止新增，避免与 SOP 标准包形成双权威。
 */
@ExtendWith(MockitoExtension.class)
class SopBindingServiceImplGuardTest {

    @Mock
    private SopMethodBindingMapper methodBindingMapper;

    @Mock
    private SopInstanceBindingMapper instanceBindingMapper;

    @Mock
    private EntityService entityService;

    @InjectMocks
    private SopBindingServiceImpl service;

    @Test
    void upsertMethod_newInspectionItemBinding_rejectedByGuard() {
        // Arrange
        SopMethodBindingUpsertReqVO req = new SopMethodBindingUpsertReqVO();
        req.setSubjectType("inspection_item");
        req.setSubjectId(1001L);
        req.setDimensionKey("execution_means");
        req.setDimensionValue("manual");
        req.setSopId(3001L);

        EntityRespVO sop = new EntityRespVO();
        sop.setId(3001L);
        when(entityService.get(3001L, SopFieldCodes.ENTITY_TYPE_CODE)).thenReturn(sop);
        when(methodBindingMapper.selectByIdentity(
                eq("inspection_item"), eq(1001L), eq("execution_means"), eq("MANUAL")))
                .thenReturn(null);

        // Act + Assert
        assertThrows(ServiceException.class, () -> service.upsertMethod(req));
        verify(methodBindingMapper, never()).insert(any(SopMethodBindingDO.class));
    }
}
