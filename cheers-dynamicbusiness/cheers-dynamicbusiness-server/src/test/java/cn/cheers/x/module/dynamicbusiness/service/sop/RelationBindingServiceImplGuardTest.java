package cn.cheers.x.module.dynamicbusiness.service.sop;

import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.sop.vo.RelationMethodBindingUpsertReqVO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.sop.RelationMethodBindingDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.sop.RelationInstanceBindingMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.sop.RelationMethodBindingMapper;
import cn.cheers.x.module.dynamicbusiness.service.entity.EntityService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * 通用绑定写入：subject+dimension 仅作键，不额外写死检查业务守卫。
 */
@ExtendWith(MockitoExtension.class)
class RelationBindingServiceImplGuardTest {

    @Mock
    private RelationMethodBindingMapper methodBindingMapper;

    @Mock
    private RelationInstanceBindingMapper instanceBindingMapper;

    @Mock
    private EntityService entityService;

    @InjectMocks
    private RelationBindingServiceImpl service;

    @Test
    void upsertMethod_newInspectionItemBinding_insertedAsGenericTargetBinding() {
        RelationMethodBindingUpsertReqVO req = new RelationMethodBindingUpsertReqVO();
        req.setSubjectType("inspection_item");
        req.setSubjectId(1001L);
        req.setDimensionKey("execution_means");
        req.setDimensionValue("manual");
        req.setTargetType("sop");
        req.setTargetId(3001L);

        EntityRespVO target = new EntityRespVO();
        target.setId(3001L);
        when(entityService.get(3001L, "sop")).thenReturn(target);
        when(methodBindingMapper.selectByIdentity(
                eq("inspection_item"), eq(1001L), eq("execution_means"), eq("MANUAL")))
                .thenReturn(null);

        service.upsertMethod(req);

        verify(methodBindingMapper, times(1)).insert(any(RelationMethodBindingDO.class));
    }
}
